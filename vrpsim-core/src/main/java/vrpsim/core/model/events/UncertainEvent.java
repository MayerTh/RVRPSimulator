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
package vrpsim.core.model.events;

import vrpsim.core.model.util.uncertainty.UncertainParameterContainer;
import vrpsim.core.simulator.ITime;

/**
 * @date 18.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class UncertainEvent implements IEvent {

	private final IEventOwner owner;
	private final IEventType eventType;
	private final ITime timeTillOccurrence;
	private ITime simulationTimeOfOccurrence;
	private final UncertainParameterContainer container;

	public UncertainEvent(final IEventOwner owner, final IEventType eventType, final ITime timeTillOccurrence,
			final UncertainParameterContainer container) {
		this.owner = owner;
		this.eventType = eventType;
		this.timeTillOccurrence = timeTillOccurrence;
		this.container = container;
	}

	public UncertainParameterContainer getContainer() {
		return container;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.events.IEvent#getTimeTillOccurrence()
	 */
	public ITime getTimeTillOccurrence() {
		return this.timeTillOccurrence;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.events.IEvent#getPriority()
	 */
	public Integer getPriority() {
		return new Integer(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.events.IEvent#getType()
	 */
	public IEventType getType() {
		return this.eventType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.events.IEvent#getOwner()
	 */
	public IEventOwner getOwner() {
		return this.owner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.events.IEvent#setSimulationTimeOfOccurrence(vrpsim.
	 * core.simulator.ITime)
	 */
	public void setSimulationTimeOfOccurrence(ITime time) {
		this.simulationTimeOfOccurrence = time;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.events.IEvent#getSimulationTimeOfOccurence()
	 */
	public ITime getSimulationTimeOfOccurence() {
		return this.simulationTimeOfOccurrence;
	}
	
}
