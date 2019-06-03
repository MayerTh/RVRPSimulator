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
package vrpsim.core.model.structure.customer;

import java.util.List;

import vrpsim.core.model.events.IEventOwner;
import vrpsim.core.model.events.strategies.IOrderStrategy;
import vrpsim.core.model.events.strategies.impl.util.Order;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage;

public interface ICustomer extends IEventOwner, IVRPSimulationModelStructureElementWithStorage {

	/**
	 * Returns all initial static orders.
	 * 
	 * @return
	 */
	public List<Order> getStaticOrdersBeforeEventGeneration();
	
	/**
	 * Returns true if there are dynamic events.
	 * 
	 * @return
	 */
	public boolean isHasDynamicEvents();
	
	public IOrderStrategy getOrderStrategy();
	
}
