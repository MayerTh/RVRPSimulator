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

import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventOwner;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.simulator.IClock;

public class TourEvent implements IEvent {

	private Double simulationTimeOfOccurrence;
	private final IEventType type;
	private final IEventOwner owner;
	private final Double reletiveTimeTillOccurence;

	private final ACTION action;

	public TourEvent(IEventType type, IEventOwner owner, Double timeTillOccurence, ACTION doAction) {
		this.owner = owner;
		this.type = type;
		this.reletiveTimeTillOccurence = timeTillOccurence;
		this.action = doAction;
	}

		public ACTION getAction() {
		return this.action;
	}

	@Override
	public Double getRelativeTimeTillOccurrence() {
		return this.reletiveTimeTillOccurence;
	}

	@Override
	public void setSimulationTimeOfOccurrence(IClock clock) {
		this.simulationTimeOfOccurrence = clock.getCurrentSimulationTime() + this.reletiveTimeTillOccurence;
	}

	@Override
	public Double getSimulationTimeOfOccurence() {
		return this.simulationTimeOfOccurrence;
	}

	@Override
	public Integer getPriority() {
		return 0;
	}

	@Override
	public IEventType getType() {
		return this.type;
	}

	@Override
	public IEventOwner getOwner() {
		return this.owner;
	}

	public enum ACTION {
		EVENT_TO_TRIGGER_DO_ACTION, EVENT_TO_TRIGGER_PREPARE_ACTION;
	}

}
