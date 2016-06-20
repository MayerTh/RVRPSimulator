/**
 * Copyright (C) 2016 Thomas Mayer (thomas.mayer@unibw.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vrpsim.core.model.behaviour;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.behaviour.activities.StorableTransportActivity;
import vrpsim.core.model.behaviour.activities.util.ActivityDoActionResult;
import vrpsim.core.model.behaviour.activities.util.ActivityPrepareActionResult;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventOwner;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.exceptions.NetworkException;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.exceptions.detail.ErrorDuringEventProcessingException;
import vrpsim.core.model.util.exceptions.detail.InvalidTourException;
import vrpsim.core.model.util.exceptions.detail.RejectEventException;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;

/**
 * @date 19.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class Tour implements ITour {

	private static Logger logger = LoggerFactory.getLogger(Tour.class);

	private TourContext context;
	private List<IActivity> orderedActivities;
	private int activityIndex;
	private List<IEventType> eventTypes;

	private List<Double> costList;
	private double currentTourCosts;

	public Tour(TourContext context, List<IActivity> orderedActivities) throws NetworkException {

		this.activityIndex = 0;
		this.context = context;
		this.orderedActivities = orderedActivities;

		this.costList = new ArrayList<>();
		this.currentTourCosts = 0.0;

		this.eventTypes = new ArrayList<IEventType>();
		this.eventTypes.add(new IEventType() {
			@Override
			public String getType() {
				return IEventType.ACTIVITY_EVENT;
			}
		});

		IVRPSimulationModelNetworkElement driversHome = this.context.getCurrentDriver()
				.getVRPSimulationModelStructureElementParameters().getHome();
		IVRPSimulationModelNetworkElement vehiclesHome = this.context.getCurrentVehicle()
				.getVRPSimulationModelStructureElementParameters().getHome();

		if (!driversHome.equals(vehiclesHome)) {
			throw new InvalidTourException("Drivers home is not equal to vehicles home. Drivers home: " + driversHome
					+ ", vehicles home: " + vehiclesHome);
		}

		if (!driversHome.equals(this.context.getCurrentPlace())) {
			throw new InvalidTourException("Drivers home is not equal to current place. Drivers home: " + driversHome
					+ ", current place: " + this.context.getCurrentPlace());
		}

		this.validateActivity(this.orderedActivities.get(activityIndex), this.context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.behaviour.ITour#getCurrentTourCosts()
	 */
	@Override
	public Double getCurrentTourCosts() {
		return (this.costList.size() == 0) ? this.currentTourCosts : this.costList.get(this.costList.size() - 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.events.IEventOwner#getAllEventTypes()
	 */
	public List<IEventType> getAllEventTypes() {
		return this.eventTypes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.events.IEventOwner#getOwnEvents(vrpsim.core.simulator.
	 * IClock)
	 */
	public List<IEvent> getInitialEvents(IClock clock) {
		List<IEvent> events = new ArrayList<IEvent>();
		events.add(createEvent(this.context.getActivityStart(), this.activityIndex, false));
		return events;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.events.IEventOwner#processOwnEvent(vrpsim.core.model.
	 * events.IEvent, vrpsim.core.simulator.IClock)
	 */
	public List<IEvent> processEvent(IEvent event, IClock clock, EventListService eventListAnalyzer)
			throws EventException {

		if (!(event instanceof TourEvent) || !event.getType().equals(this.eventTypes.get(0))) {
			throw new RejectEventException("Can not process event " + event);
		}

		TourEvent tEvent = (TourEvent) event;
		int index = tEvent.getActivityIndex();
		IActivity activityWorkWith = this.orderedActivities.get(index);

		try {
			validateActivity(activityWorkWith, context);
		} catch (NetworkException e) {
			e.printStackTrace();
			throw new ErrorDuringEventProcessingException("Can not process event cause of "
					+ e.getClass().getSimpleName() + ", validation failed for activity working with " + activityWorkWith
					+ ". Orginial message:" + e.getMessage());
		}

		IEvent newEvent = null;
		if (!tEvent.doAction) {
			// Call prepareAction
			try {
				context.clearElementsUpdated();
				ActivityPrepareActionResult result = activityWorkWith.prepareAction(clock, context);
				if (result.isPrepareActionSuccessful()) {

					newEvent = createEvent(result.getTimeTillDoAction(), activityIndex, true);

				} else {

					ITime timeTillOccurenceOfBlockingElement = eventListAnalyzer
							.getTimeTillOccurenceFor(result.getResponsibleElement(), clock);

					if (timeTillOccurenceOfBlockingElement == null) {

						String error = "Activity " + activityWorkWith + " blocked by " + result.getResponsibleElement()
								+ ", but no regarding time given by " + eventListAnalyzer.getClass().getSimpleName()
								+ ".";
						logger.error(error);
						throw new ErrorDuringEventProcessingException(error);
					}

					newEvent = createEvent(timeTillOccurenceOfBlockingElement, activityIndex, false);
					logger.info(
							"Prepare action of activity {} failed bbecause it is blocked by {}. New event created at time {}.",
							activityWorkWith,
							result.getResponsibleElement().getVRPSimulationModelElementParameters().getId(),
							timeTillOccurenceOfBlockingElement.toString());
				}

			} catch (VRPArithmeticException | NetworkException e) {
				e.printStackTrace();
				throw new ErrorDuringEventProcessingException("Can not process event cause of "
						+ e.getClass().getSimpleName() + " within prepareAction of activity " + activityWorkWith
						+ ". Orginial message:" + e.getMessage());
			}

		} else {
			// Call doAction
			try {
				context.clearElementsUpdated();
				ActivityDoActionResult result = activityWorkWith.doAction(clock, context);

				if (activityIndex >= this.orderedActivities.size() - 1) {
					this.activityIndex = 0;
					this.costList.add(new Double(this.currentTourCosts));
					this.currentTourCosts = 0.0;

					if (this.isPeriodic()) {
						newEvent = createEvent(clock.getCurrentSimulationTime().createTimeFrom(0.0), activityIndex,
								false);
					}

				} else {
					this.activityIndex++;
					this.currentTourCosts += result.getDoActionCosts();
					newEvent = createEvent(clock.getCurrentSimulationTime().createTimeFrom(0.0), activityIndex, false);
				}

			} catch (StorageException | VRPArithmeticException e) {
				e.printStackTrace();
				throw new ErrorDuringEventProcessingException("Can not process event cause of "
						+ e.getClass().getSimpleName() + " within doAction of activity " + activityWorkWith
						+ ". Orginial message:" + e.getMessage());
			}
		}

		List<IEvent> events = new ArrayList<>();
		events.add(newEvent);
		return events;
	}

	private void validateActivity(IActivity activity, TourContext context) throws NetworkException {
		if (!(activity instanceof StorableTransportActivity)
				&& !activity.getJob().getPlaceOfJobExecution().equals(context.getCurrentPlace())) {
			throw new InvalidTourException("Job execution place is not equal to current place. Place of execution: "
					+ activity.getJob().getPlaceOfJobExecution().getVRPSimulationModelElementParameters().getId()
					+ ", current place: " + context.getCurrentPlace().getVRPSimulationModelElementParameters().getId());
		}
	}

	private IEvent createEvent(ITime timeTillOccurence, int activityIndex, boolean doAction) {
		return new TourEvent(this.eventTypes.get(0), this, timeTillOccurence, activityIndex, doAction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.behaviour.ITour#getOrderedActivties()
	 */
	@Override
	public List<IActivity> getOrderedActivties() {
		return this.orderedActivities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.behaviour.ITour#isPeriodic()
	 */
	@Override
	public boolean isPeriodic() {
		return false;
	}

	@Override
	public TourContext getTourContext() {
		return this.context;
	}

	public static class TourEvent implements IEvent {

		private ITime simulationTimeOfOccurrence;
		private final IEventType type;
		private final IEventOwner owner;
		private final ITime timeTillOccurence;

		private final int activityIndex;
		private final boolean doAction;

		public TourEvent(IEventType type, IEventOwner owner, ITime timeTillOccurence, int activityIndex,
				boolean doAction) {
			this.owner = owner;
			this.type = type;
			this.timeTillOccurence = timeTillOccurence;
			this.activityIndex = activityIndex;
			this.doAction = doAction;
		}

		public int getActivityIndex() {
			return this.activityIndex;
		}

		public boolean isDoAction() {
			return this.doAction;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see vrpsim.core.model.events.IEvent#getTimeTillOccurrence()
		 */
		@Override
		public ITime getTimeTillOccurrence() {
			return this.timeTillOccurence;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * vrpsim.core.model.events.IEvent#setSimulationTimeOfOccurrence(vrpsim.
		 * core.simulator.ITime)
		 */
		@Override
		public void setSimulationTimeOfOccurrence(ITime time) {
			this.simulationTimeOfOccurrence = time;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see vrpsim.core.model.events.IEvent#getSimulationTimeOfOccurence()
		 */
		@Override
		public ITime getSimulationTimeOfOccurence() {
			return this.simulationTimeOfOccurrence;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see vrpsim.core.model.events.IEvent#getPriority()
		 */
		@Override
		public Integer getPriority() {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see vrpsim.core.model.events.IEvent#getType()
		 */
		@Override
		public IEventType getType() {
			return this.type;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see vrpsim.core.model.events.IEvent#getOwner()
		 */
		@Override
		public IEventOwner getOwner() {
			return this.owner;
		}

	}

}
