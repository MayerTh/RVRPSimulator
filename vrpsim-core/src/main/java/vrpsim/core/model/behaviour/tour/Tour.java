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
package vrpsim.core.model.behaviour.tour;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.behaviour.activities.IActivity;
import vrpsim.core.model.behaviour.activities.util.ActivityDoActionResult;
import vrpsim.core.model.behaviour.activities.util.ActivityPrepareActionResult;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.util.exceptions.BehaviourException;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.exceptions.NetworkException;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.exceptions.detail.ErrorDuringEventProcessingException;
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

	private final TourContext context;
	private final IActivity startActivity;

	private List<IEventType> eventTypes;
	private List<Double> costList;
	private double currentTourCosts;

	private boolean areRessoucresAllocatedAlready = false;

	public Tour(TourContext context, IActivity startActivity) {

		this.context = context;
		this.startActivity = startActivity;
		this.costList = new ArrayList<>();
		this.currentTourCosts = 0.0;

		this.eventTypes = new ArrayList<IEventType>();
		this.eventTypes.add(new IEventType() {
			@Override
			public String getType() {
				return IEventType.ACTIVITY_EVENT;
			}
		});
	}

	@Override
	public Double getCurrentTourCosts() {
		return (this.costList.size() == 0) ? this.currentTourCosts : this.costList.get(this.costList.size() - 1);
	}

	@Override
	public List<IEventType> getAllEventTypes() {
		return this.eventTypes;
	}

	@Override
	public List<IEvent> getInitialEvents(IClock clock) {
		List<IEvent> events = new ArrayList<IEvent>();
		events.add(createEvent(this.context.getActivityStart(), this.startActivity, false));
		return events;
	}

	@Override
	public List<IEvent> processEvent(IEvent event, IClock clock, EventListService eventListService)
			throws EventException {

		if (!(event instanceof TourEvent) || !event.getType().equals(this.eventTypes.get(0))) {
			throw new RejectEventException("Can not process event " + event);
		}

		if (!this.areRessoucresAllocatedAlready) {
			if (!context.getVehicle().isAvailable(clock) || !context.getDriver().isAvailable(clock)) {
				throw new ErrorDuringEventProcessingException(
						"Vehicle or driver for tour is not available and can not be allocated.");
			} else {
				allocate();
			}
		}

		TourEvent tEvent = (TourEvent) event;
		IActivity activityWorkWith = tEvent.getActivityToExecute();

		try {
			activityWorkWith.validate(this.context);
		} catch (BehaviourException e) {
			e.printStackTrace();
			throw new ErrorDuringEventProcessingException("Can not process event cause of "
					+ e.getClass().getSimpleName() + ", validation failed for activity working with " + activityWorkWith
					+ ". Orginial message:" + e.getMessage());
		}

		IEvent newEvent = null;
		if (!tEvent.isDoAction()) {
			// Call prepareAction of activity.
			try {
				context.clearElementsUpdated();
				ActivityPrepareActionResult result = activityWorkWith.prepareAction(clock, context);
				if (result.isPrepareActionSuccessful()) {
					// Preparation was successful, generated new event with time
					// till doAction of activity has to be called.
					newEvent = createEvent(result.getTimeTillDoAction(), activityWorkWith, true);
				} else {
					// Preparation was not successful, using the
					// EventListService to look for Blocking elements.
					ITime timeTillOccurenceOfBlockingElement = eventListService
							.getTimeTillOccurenceFor(result.getResponsibleElement(), clock);

					if (timeTillOccurenceOfBlockingElement == null) {
						String error = "Activity " + activityWorkWith + " blocked by " + result.getResponsibleElement()
								+ ", but no regarding time given by " + eventListService.getClass().getSimpleName()
								+ ".";
						logger.error(error);
						throw new ErrorDuringEventProcessingException(error);
					}

					newEvent = createEvent(timeTillOccurenceOfBlockingElement, activityWorkWith, false);
					logger.info(
							"Prepare action of activity {} failed bbecause it is blocked by {}. New event created at time {}.",
							activityWorkWith,
							result.getResponsibleElement().getVRPSimulationModelElementParameters().getId(),
							timeTillOccurenceOfBlockingElement.toString());
				}

			} catch (VRPArithmeticException | NetworkException | BehaviourException e) {
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

				if (activityWorkWith.getSuccessor() == null) {
					this.costList.add(new Double(this.currentTourCosts));
					this.currentTourCosts = 0.0;

					release();

				} else {
					this.currentTourCosts += result.getDoActionCosts();
					newEvent = createEvent(clock.getCurrentSimulationTime().createTimeFrom(0.0),
							activityWorkWith.getSuccessor(), false);
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

	private IEvent createEvent(ITime timeTillOccurence, IActivity activityToExecute, boolean doAction) {
		return new TourEvent(this.eventTypes.get(0), this, timeTillOccurence, activityToExecute, doAction);
	}

	@Override
	public void allocatedElementStateChanged(IVRPSimulationModelElement element) throws BehaviourException {
		if (this.areRessoucresAllocatedAlready) {
			throw new BehaviourException(
					"The state of vehicle or driver (e.g. breakdown) changed during tour execution. Tour implementation can not handle this yet.");
		}
	}

	private void allocate() {
		this.areRessoucresAllocatedAlready = true;
		this.context.getVehicle().allocateBy(this);
		this.context.getDriver().allocateBy(this);
	}

	private void release() {
		this.context.getVehicle().releaseFrom(this);
		this.context.getDriver().releaseFrom(this);
		this.areRessoucresAllocatedAlready = false;
	}

	@Override
	public IActivity getStartActivity() {
		return this.startActivity;
	}

	@Override
	public TourContext getTourContext() {
		return this.context;
	}

}
