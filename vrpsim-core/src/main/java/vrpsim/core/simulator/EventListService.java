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
package vrpsim.core.simulator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventOwner;

/**
 * @date 23.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class EventListService {

	private static Logger logger = LoggerFactory.getLogger(EventListService.class);

	private final EventList eventList;

	public EventListService() {
		this.eventList = new EventList();
	}

	/**
	 * Introduces an {@link ITour} into the simulation process. Asks after initial
	 * events {@link IEventOwner#getInitialEvents(IClock)} and add them to the event
	 * list {@link EventListService#addEvents(IClock, List)}.
	 * 
	 * @param clock
	 * @param tour
	 */
	public void introduceTourToEventList(IClock clock, ITour tour) {
		List<IEvent> events = tour.getInitialEvents(clock);
		this.addEvents(clock, events);
	}

	/**
	 * Adds an {@link IEvent} to the {@link EventList}, sets the
	 * {@link IEvent#getSimulationTimeOfOccurence()} property if not set as
	 * calculation of {@link IEvent#getRelativeTimeTillOccurrence()} +
	 * {@link IClock#getCurrentSimulationTime()}.
	 * 
	 * @param clock
	 * @param event
	 */
	public void addEvent(IClock clock, IEvent event) {
		if (event.getSimulationTimeOfOccurence() == null) {
			event.setSimulationTimeOfOccurrence(clock);
		}
		this.eventList.getEventList().add(event);
	}

	/**
	 * Adds all given {@link IEvent} to the {@link EventList}, sets the
	 * {@link IEvent#getSimulationTimeOfOccurence()} property if not set as
	 * calculation of {@link IEvent#getRelativeTimeTillOccurrence()} +
	 * {@link IClock#getCurrentSimulationTime()} of each {@link IEvent}.
	 * 
	 * @param clock
	 * @param events
	 */
	public void addEvents(IClock clock, List<IEvent> events) {
		for (IEvent event : events) {
			logger.debug("Added event from owner: {}", event.getOwner().getClass().getSimpleName());
			this.addEvent(clock, event);
		}
	}

	/**
	 * Returns the size of the {@link EventList}. See {@link List#size()}.
	 * 
	 * @return
	 */
	public int getEventListSize() {
		return this.eventList.getEventList().size();
	}

	/**
	 * Returns the {@link ITime} of the occurrence of the next {@link IEvent} in
	 * {@link EventList} of given owner. The {@link ITime} is as following
	 * calculated: {@link IEvent#getSimulationTimeOfOccurence()} -
	 * {@link IClock#getCurrentSimulationTime()}
	 * 
	 * 
	 * @param element
	 * @param clock
	 * @return
	 */
	public Double getRelativeTimeTillOccurenceFor(IVRPSimulationModelElement element, IClock clock) {
		Double timeTillOccurrence = -1D;
		this.eventList.sort();
		for (IEvent event : this.eventList.getEventList()) {
			if (element.equals(event.getOwner())) {
				timeTillOccurrence = Math.abs(clock.getCurrentSimulationTime() - event.getSimulationTimeOfOccurence());
				break;
			}
		}

		if (timeTillOccurrence < 0L) {
			IEvent event = this.eventList.getEventList().get(0);
			timeTillOccurrence = Math.abs(clock.getCurrentSimulationTime() - event.getSimulationTimeOfOccurence());
		}

		return timeTillOccurrence;
	}

	/**
	 * Sorts the {@link EventList} according implementation in
	 * {@link EventList#sort()}, returns and removes the next {@link IEvent} in
	 * list.
	 * 
	 * @return
	 */
	public IEvent getAndRemoveNextEvent() {
		this.eventList.sort();
		return this.eventList.getEventList().remove(0);
	}

	/**
	 * Sorts the {@link EventList} according implementation in
	 * {@link EventList#sort()}, returns the next {@link IEvent} in list without
	 * removing.
	 * 
	 * @return
	 */
	public IEvent getNextEvent() {
		this.eventList.sort();
		return this.eventList.getEventList().get(0);
	}

	public Double getNextOccurenceByType(String activityEvent, IClock clock) {
		Double result = null;
		for (IEvent event : this.eventList.getEventList()) {
			if (event.getType().getType().equals(activityEvent)) {
				result = event.getSimulationTimeOfOccurence() - clock.getCurrentSimulationTime();
			}
		}
		return result;
	}

}
