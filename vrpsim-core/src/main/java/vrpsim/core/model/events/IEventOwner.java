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

import java.util.List;

import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;

/**
 * An {@link IEventOwner} is the creator and owner of several {@link IEvent}.
 * The {@link IEventOwner} is obliged to provide information about the
 * {@link IEventType} of the {@link IEvent} he is creating. If an created
 * {@link IEvent} is fired, the {@link IEventOwner} is obligated to process
 * the event.
 * 
 * @date 01.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public interface IEventOwner {

	/**
	 * Returns all {@link IEventType} generated by the {@link IEventOwner}.
	 * 
	 * @return
	 */
	public List<IEventType> getAllEventTypes();

	/**
	 * Returns all first {@link IEvent} the {@link IEventOwner} creates.
	 * 
	 * @return
	 */
	public List<IEvent> getInitialEvents(IClock clock);

	/**
	 * Fires the event and returns a list of new events.
	 * 
	 * @return
	 */
	public List<IEvent> processEvent(IEvent event, IClock clock, EventListService eventListAnalyzer) throws EventException;

}
