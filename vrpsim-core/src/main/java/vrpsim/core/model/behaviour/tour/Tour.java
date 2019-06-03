/**
 * Copyright © 2016 Thomas Mayer (thomas.mayer@unibw.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import vrpsim.core.model.behaviour.activities.impl.ActivityPrepareActionResult;
import vrpsim.core.model.behaviour.activities.impl.UnloadActivity;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.util.exceptions.BehaviourException;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.detail.ErrorDuringEventProcessingException;
import vrpsim.core.model.util.exceptions.detail.NoRoutingPossibleException;
import vrpsim.core.model.util.exceptions.detail.RejectEventException;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;

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

	private List<IActivity> orignialActivityList;

	public Tour(TourContext context, IActivity startActivity) {
		this.context = context;
		this.startActivity = startActivity;
		this.context.setCurrentActivity(startActivity);

		this.eventTypes = new ArrayList<IEventType>();
		this.eventTypes.add(new IEventType() {
			@Override
			public String getType() {
				return IEventType.ACTIVITY_EVENT;
			}
		});
	}

	public void init() {
		this.orignialActivityList = new ArrayList<>();
		IActivity workWith = this.startActivity.getSuccessor();
		while (workWith != null) {
			this.orignialActivityList.add(workWith);
			workWith = workWith.getSuccessor();
		}
	}

	public void reset() {
		this.context.reset();
		this.context.setCurrentActivity(this.startActivity);
		this.eventTypes = new ArrayList<IEventType>();
		this.eventTypes.add(new IEventType() {
			@Override
			public String getType() {
				return IEventType.ACTIVITY_EVENT;
			}
		});
		IActivity workWith = this.startActivity;
		while (workWith != null) {
			workWith.reset();
			workWith = workWith.getSuccessor();
		}
		IActivity toSet = this.startActivity;
		if (this.orignialActivityList != null) {
			for (IActivity ac : this.orignialActivityList) {
				toSet.setSuccessor(ac);
				toSet = ac;
			}
		}
	}

	@Override
	public List<IEventType> getAllEventTypes() {
		return this.eventTypes;
	}

	@Override
	public List<IEvent> getInitialEvents(IClock clock) {
		return createEvent(this.context.getTourStartTime(), TourEvent.ACTION.EVENT_TO_TRIGGER_PREPARE_ACTION);
	}

	private List<IEvent> prepareAction(IEvent event, IClock clock, EventListService eventListService)
			throws BehaviourException, NoRoutingPossibleException, ErrorDuringEventProcessingException {

		this.context.getCurrentActivity().setPrepared(true);
		List<IVRPSimulationModelElement> toAllocate = this.context.getCurrentActivity().getToAllocate();
		toAllocate.add(context.getDriver());
		toAllocate.add(context.getVehicle());

		// Handle allocation.
		for (IVRPSimulationModelElement element : toAllocate) {
			if (!element.isAvailableForAllocation(clock)) {

				Double timeTillOccurenceOfBlockingElement = eventListService.getRelativeTimeTillOccurenceFor(element, clock);
				if (timeTillOccurenceOfBlockingElement == null) {
					String error = "Activity " + context.getCurrentActivity() + " blocked by " + element
							+ ", but no regarding time given by " + eventListService.getClass().getSimpleName() + ".";
					logger.error(error);
					throw new ErrorDuringEventProcessingException(error);
				}
				List<IEvent> events = createEvent(timeTillOccurenceOfBlockingElement, TourEvent.ACTION.EVENT_TO_TRIGGER_PREPARE_ACTION);
				return events;
			}
		}
		// Allocate
		toAllocate.parallelStream().forEach(e -> e.allocateBy(context.getCurrentActivity()));

		ActivityPrepareActionResult prepareActionResult = context.getCurrentActivity().prepareAction(clock, context);
		if (prepareActionResult.isPrepareActionSuccessful()) {
			List<IEvent> events = createEvent(prepareActionResult.getReletaiveTimeTillDoAction(),
					TourEvent.ACTION.EVENT_TO_TRIGGER_DO_ACTION);
			return events;
		} else {
			logger.warn("ActivityPrepareActionResult not successful from {}", context.getCurrentActivity().getClass().getSimpleName());
			Double timeTillOccurenceOfBlockingElement = eventListService
					.getRelativeTimeTillOccurenceFor(prepareActionResult.getResponsibleElement(), clock);
			if (timeTillOccurenceOfBlockingElement == null) {
				String error = "Activity " + context.getCurrentActivity() + " blocked by " + prepareActionResult.getResponsibleElement()
						+ ", but no regarding time given by " + eventListService.getClass().getSimpleName() + ".";
				logger.error(error);
				throw new ErrorDuringEventProcessingException(error);
			}
			List<IEvent> events = createEvent(timeTillOccurenceOfBlockingElement, TourEvent.ACTION.EVENT_TO_TRIGGER_PREPARE_ACTION);
			Cost waitingCosts = new Cost(0, timeTillOccurenceOfBlockingElement, 0, 0);
			this.getTourContext().updateTourContextCosts(waitingCosts);
			// Release
			toAllocate.parallelStream().forEach(e -> e.releaseFrom(context.getCurrentActivity()));
			return events;
		}

	}

	private List<IEvent> doAction(IClock clock) throws StorageException {

		// Do action and calculate costs.
		logger.debug("Make do action for {}.", context.getCurrentActivity().toString());
		context.getCurrentActivity().doAction(clock, context);
		context.consumeElementsUpdated();

		// Release
		List<IVRPSimulationModelElement> toAllocate = context.getCurrentActivity().getToAllocate();
		toAllocate.add(context.getDriver());
		toAllocate.add(context.getVehicle());
		toAllocate.parallelStream().forEach(e -> e.releaseFrom(context.getCurrentActivity()));

		List<IEvent> result = new ArrayList<>();
		if (context.getCurrentActivity().getSuccessor() != null) {
			context.setCurrentActivity(context.getCurrentActivity().getSuccessor());
			context.getCurrentActivity().setPrepared(false);
			logger.debug("Set current activity to {}.", context.getCurrentActivity().toString());
			result = createEvent(0D, TourEvent.ACTION.EVENT_TO_TRIGGER_PREPARE_ACTION);
		}

		return result;
	}

	@Override
	public List<IEvent> processEvent(IEvent event, IClock clock, EventListService eventListService) throws EventException {

		if (!(event instanceof TourEvent) || !event.getType().equals(this.eventTypes.get(0))) {
			throw new RejectEventException("Can not process event " + event);
		}

		List<IEvent> result = null;
		TourEvent tourEvent = (TourEvent) event;
		if (tourEvent.getAction().equals(TourEvent.ACTION.EVENT_TO_TRIGGER_PREPARE_ACTION)) {
			try {
				result = this.prepareAction(event, clock, eventListService);
			} catch (NoRoutingPossibleException | BehaviourException e) {
				e.printStackTrace();
				throw new ErrorDuringEventProcessingException("Can not process event cause of " + e.getClass().getSimpleName()
						+ " within prepareAction of activity " + context.getCurrentActivity() + ". Orginial message:" + e.getMessage());
			}
		}

		if (tourEvent.getAction().equals(TourEvent.ACTION.EVENT_TO_TRIGGER_DO_ACTION)) {
			try {
				result = this.doAction(clock);
			} catch (StorageException e) {
				e.printStackTrace();
				throw new ErrorDuringEventProcessingException("Can not process event cause of " + e.getClass().getSimpleName()
						+ " within doAction of activity " + context.getCurrentActivity() + ". Orginial message:" + e.getMessage());
			}
		}

		return result;
	}

	private List<IEvent> createEvent(Double relativeTimeTillOccurence, TourEvent.ACTION action) {
		List<IEvent> events = new ArrayList<IEvent>();
		events.add(new TourEvent(this.eventTypes.get(0), this, relativeTimeTillOccurence, action));
		return events;
	}

	@Override
	public void allocatedElementStateChanged(IVRPSimulationModelElement element) throws BehaviourException {
	}

	@Override
	public TourContext getTourContext() {
		return this.context;
	}

	@Override
	public IActivity getStartActivity() {
		return this.startActivity;
	}

	@Override
	public int getNumberToUnload() {
		int counter = 0;
		IActivity ac = this.startActivity;
		while (ac != null) {
			if (ac instanceof UnloadActivity) {
				counter++;
			}
			ac = ac.getSuccessor();
		}
		return counter;
	}

}
