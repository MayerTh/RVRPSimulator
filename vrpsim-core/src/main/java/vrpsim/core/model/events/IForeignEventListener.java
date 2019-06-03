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
package vrpsim.core.model.events;

import java.util.Set;

import vrpsim.core.model.util.exceptions.detail.ErrorDuringEventProcessingException;
import vrpsim.core.model.util.exceptions.detail.RejectEventException;
import vrpsim.core.simulator.IClock;

/**
 * A class implementing the interface {@link IForeignEventListener} can register
 * for all available {@link IEventType}. And gets a notification if an event of
 * the registered type was fired.
 * 
 * @date 01.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public interface IForeignEventListener {

	/**
	 * Returns a list of {@link IEventType} the {@link IForeignEventListener} is
	 * interested in.
	 * 
	 * @param availableEventTypes
	 * @return
	 */
	public Set<IEventType> registerForEventTypes(Set<IEventType> availableEventTypes);

	/**
	 * An {@link IEvent} is fired, the {@link IForeignEventListener} is interested
	 * in. Note: the event is processed through its owner already. ent
	 * 
	 * @param event
	 * @throws RejectEventException
	 * @throws ErrorDuringEventProcessingException
	 */
	public void notify(IEvent event, IClock simulationClock) throws RejectEventException, ErrorDuringEventProcessingException;

}
