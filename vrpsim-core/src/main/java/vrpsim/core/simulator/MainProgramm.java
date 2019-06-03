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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.IVRPSimulationElement;
import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventOwner;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.events.IForeignEventListener;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.exceptions.InitializationException;

/**
 * @date 03.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class MainProgramm extends Observable {

	private static Logger logger = LoggerFactory.getLogger(MainProgramm.class);

	private final IClock clock;
	private final EventListService eventListInterface;
	private final HashMap<String, List<IForeignEventListener>> foreignEventListeners;

	private VRPSimulationModel model;

	private Double simulationEndTime;

	public MainProgramm() {
		this.clock = new Clock();
		this.eventListInterface = new EventListService();
		this.foreignEventListeners = new HashMap<String, List<IForeignEventListener>>();
	}

	public IClock getSimulationClock() {
		return this.clock;
	}

	public void init(final VRPSimulationModel model, final Double simulationEndTime) throws InitializationException {

		this.model = model;
		this.simulationEndTime = simulationEndTime;

		logger.info("Start initializing simulation model and collecting all existing IVRPSimualtionElement from model.");
		List<IVRPSimulationElement> allElements = this.model.initalizeAndReturnSimulationElements(this.eventListInterface);

//		logger.trace("IVRPSimualtionElement from model: {}", allElements);

		logger.info("Add initial events to eventlist.");
		Set<IEventType> types = new HashSet<IEventType>();
		for (IVRPSimulationElement element : allElements) {
			if (element instanceof IEventOwner) {
				IEventOwner eventOwnerEntity = (IEventOwner) element;
				this.eventListInterface.addEvents(this.clock, eventOwnerEntity.getInitialEvents(this.clock));
				types.addAll(eventOwnerEntity.getAllEventTypes());
			}
		}

		logger.info("Number of initial events generated: {}.", this.eventListInterface.getEventListSize());

		logger.info("Add foreign event listener.");
		for (IVRPSimulationElement element : allElements) {
			if (element instanceof IForeignEventListener) {
				IForeignEventListener foreignEventListener = (IForeignEventListener) element;
				for (IEventType type : foreignEventListener.registerForEventTypes(types)) {
					List<IForeignEventListener> fel = new ArrayList<IForeignEventListener>();
					if (foreignEventListeners.containsKey(type.getType())) {
						fel = foreignEventListeners.get(type.getType());
					}
					fel.add(foreignEventListener);
					foreignEventListeners.put(type.getType(), fel);
					logger.info("Foreign event listener added class={} for type={} ", element.getClass().getName(), type.getType());
				}
			}
		}
		
	}

	public void run(final VRPSimulationModel model) throws EventException, InterruptedException, InitializationException {
		this.run(model, Double.MAX_VALUE);
	}

	public void run(final VRPSimulationModel model, final Double simulationEndTime)
			throws EventException, InterruptedException, InitializationException {

		this.init(model, simulationEndTime);
		while (0 >= this.clock.getCurrentSimulationTime().compareTo(this.simulationEndTime)
				&& this.eventListInterface.getEventListSize() > 0) {
			notifyObservers();
			runStep();
		}

	}

	public IEvent runStep() throws EventException {

		IEvent currentEvent = this.eventListInterface.getAndRemoveNextEvent();
		this.clock.setSimulationTime(currentEvent.getSimulationTimeOfOccurence());
		logger.info("Current simulation time: {}, triggered from {}", this.clock.getCurrentSimulationTime(),
				currentEvent.getOwner().getClass().getSimpleName());

		logger.debug("Event (type=" + currentEvent.getType().getType() + ") from " + currentEvent.getOwner().getClass().getSimpleName());
		logger.debug("Before processing: Lenght of event list: {}", this.eventListInterface.getEventListSize());

		// Inform all foreign event listeners.
		if (this.foreignEventListeners.containsKey(currentEvent.getType().getType())) {
			logger.debug("For the event type {}, {} IForeignEventListener is/are informed", currentEvent.getType().getType(),
					this.foreignEventListeners.get(currentEvent.getType().getType()).size());
			for (IForeignEventListener fel : this.foreignEventListeners.get(currentEvent.getType().getType())) {
				logger.trace("Inform {}", fel);
				fel.notify(currentEvent, this.clock);
			}
		}

		// Can return null, if no more activity is executed with in the tour.
		List<IEvent> newEvents = currentEvent.getOwner().processEvent(currentEvent, this.clock, this.eventListInterface);

		if (newEvents != null) {
			for (IEvent newEvent : newEvents) {
				// Add new event on event list.
				if (newEvent != null) {
					this.eventListInterface.addEvent(this.clock, newEvent);
					logger.debug("Added new event (type={}) from {} with relative time to occurens {} to event list.", newEvent.getType().getType(),
							newEvent.getOwner().getClass().getSimpleName(), newEvent.getRelativeTimeTillOccurrence());
				}
			}
		}

		logger.debug("After processing: Lenght of event list: {}", this.eventListInterface.getEventListSize());
		return currentEvent;
	}

	/**
	 * Returns true if simulation end time is reached, or no event to process exists.
	 * 
	 * @return
	 */
	public boolean isSimulaationFinsihed() {
		if (this.eventListInterface.getEventListSize() <= 0) {
			logger.info("Simulation ends, no event in eventlist anymore.");
			return true;
		}

		if (0 < this.eventListInterface.getNextEvent().getSimulationTimeOfOccurence().compareTo(this.simulationEndTime)) {
			logger.info("Simulation ends, current time = {}, simulation end time = {}.",
					this.eventListInterface.getNextEvent().getSimulationTimeOfOccurence(), this.simulationEndTime);
			return true;
		}
		return false;
	}

}
