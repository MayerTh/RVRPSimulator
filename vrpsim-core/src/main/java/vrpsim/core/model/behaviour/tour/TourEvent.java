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

import vrpsim.core.model.behaviour.activities.IActivity;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.IEventOwner;
import vrpsim.core.model.events.IEventType;
import vrpsim.core.simulator.ITime;

public class TourEvent implements IEvent {

	private ITime simulationTimeOfOccurrence;
	private final IEventType type;
	private final IEventOwner owner;
	private final ITime timeTillOccurence;

	private final IActivity activityToExecute;
	private final boolean doAction;

	public TourEvent(IEventType type, IEventOwner owner, ITime timeTillOccurence, IActivity activityToExecute,
			boolean doAction) {
		this.owner = owner;
		this.type = type;
		this.timeTillOccurence = timeTillOccurence;
		this.activityToExecute = activityToExecute;
		this.doAction = doAction;
	}

	public IActivity getActivityToExecute() {
		return activityToExecute;
	}

	public boolean isDoAction() {
		return this.doAction;
	}

	@Override
	public ITime getTimeTillOccurrence() {
		return this.timeTillOccurence;
	}

	@Override
	public void setSimulationTimeOfOccurrence(ITime time) {
		this.simulationTimeOfOccurrence = time;
	}

	@Override
	public ITime getSimulationTimeOfOccurence() {
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

}
