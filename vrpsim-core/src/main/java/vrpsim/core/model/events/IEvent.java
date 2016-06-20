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

import vrpsim.core.simulator.ITime;

/**
 * 
 * @date 29.01.2016
 * @author thomas.mayer@unibw.de
 *
 */
public interface IEvent {

	/**
	 * Returns the time till the event has to occur.
	 * 
	 * @return
	 */
	public ITime getTimeTillOccurrence();

	/**
	 * Setter for the simulation time of occurrence. Called from the simulation
	 * engine and calculated with the help of the {@code IClock}.
	 * 
	 * @param time
	 */
	public void setSimulationTimeOfOccurrence(ITime time);

	/**
	 * Returns the simultaion time if occurrence.
	 * 
	 * @return
	 */
	public ITime getSimulationTimeOfOccurence();

	/**
	 * Returns the priority of the event.
	 * 
	 * @return
	 */
	public Integer getPriority();

	/**
	 * Return the type of the event. If an event owner has different events, the
	 * owner can distinguish between the events with the help of the event type.
	 * 
	 * @return
	 */
	public IEventType getType();

	/**
	 * The owner and creator of the event.
	 * 
	 * @return
	 */
	public IEventOwner getOwner();

}
