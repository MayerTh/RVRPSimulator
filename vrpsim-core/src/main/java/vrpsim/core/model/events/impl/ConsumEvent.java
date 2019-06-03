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
package vrpsim.core.model.events.impl;

import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventOwner;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.model.events.strategies.impl.util.Consum;
import vrpsim.core.simulator.IClock;

public class ConsumEvent implements IEvent {

	private final IEventOwner owner;
	private final IEventType type;
	private final Integer priority;
	private final Double relativeTimeTillOccurence;

	private Consum consum;
	private Double simulationtimeTillOccurence;

	public ConsumEvent(IEventOwner owner, IEventType type, Integer priority, Double relativeTimeTillOccurence, Consum consum) {
		this.owner = owner;
		this.type = type;
		this.priority = priority;
		this.relativeTimeTillOccurence = relativeTimeTillOccurence;
		this.consum = consum;
	}

	@Override
	public Double getRelativeTimeTillOccurrence() {
		return this.relativeTimeTillOccurence;
	}

	@Override
	public void setSimulationTimeOfOccurrence(IClock clock) {
		this.simulationtimeTillOccurence = clock.getCurrentSimulationTime() + this.relativeTimeTillOccurence;
	}

	@Override
	public Double getSimulationTimeOfOccurence() {
		return this.simulationtimeTillOccurence;
	}

	@Override
	public Integer getPriority() {
		return this.priority;
	}

	@Override
	public IEventType getType() {
		return this.type;
	}

	@Override
	public IEventOwner getOwner() {
		return this.owner;
	}

	public Consum getConsum() {
		return this.consum;
	}

}
