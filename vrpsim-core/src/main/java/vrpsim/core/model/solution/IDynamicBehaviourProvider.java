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
package vrpsim.core.model.solution;

import vrpsim.core.model.behaviour.BehaviourService;
import vrpsim.core.model.events.IForeignEventListener;
import vrpsim.core.model.network.NetworkService;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.model.structure.occasionaldriver.IOccasionalDriver;
import vrpsim.core.simulator.EventListService;

public interface IDynamicBehaviourProvider extends IVRPSimulationSolutionElement, IForeignEventListener {

	/**
	 * Initialize the {@link IDynamicBehaviourProvider}.
	 * 
	 * @param eventListService
	 * @param structure
	 * @param network
	 */
	public void initialize(EventListService eventListService, StructureService structureService, NetworkService networkService,
			BehaviourService behaviourService);

	/**
	 * The {@link IDynamicBehaviourProvider} is the owner of the {@link PublicOrderPlatform}, the
	 * representation of the platform where the {@link Order}s are published.
	 * 
	 * @param orderBord
	 */
	public PublicOrderPlatform getOrderBord();

	/**
	 * If an {@link IDynamicBehaviourProvider} publishes order on the
	 * {@link PublicOrderPlatform#publishOrder(Order)} and the published {@link Order} is
	 * not served by an {@link IOccasionalDriver}. The {@link IDynamicBehaviourProvider} get
	 * informed with the help of this method.
	 * 
	 * @param order
	 */
	public void handleNotTakenOrder(Order order);

	/**
	 * If a published {@link Order} via {@link PublicOrderPlatform#publishOrder(Order)},
	 * will be served by an {@link IOccasionalDriver}, the {@link IDynamicBehaviourProvider}
	 * is informed about the {@link IOccasionalDriver}.
	 * 
	 * @param order
	 * @param occasionalDriver
	 */
	public void handleTakenOrder(Order order, IOccasionalDriver occasionalDriver);

}
