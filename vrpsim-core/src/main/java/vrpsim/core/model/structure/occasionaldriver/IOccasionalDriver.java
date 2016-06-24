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
package vrpsim.core.model.structure.occasionaldriver;

import java.util.Observer;

import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.events.IEventOwner;
import vrpsim.core.model.network.NetworkService;
import vrpsim.core.model.solution.Order;
import vrpsim.core.model.solution.OrderBord;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorageMovable;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.simulator.EventList;
import vrpsim.core.simulator.EventListService;

public interface IOccasionalDriver extends IEventOwner, IVRPSimulationModelStructureElementWithStorageMovable, Observer {

	/**
	 * At the {@link OrderBord}, all {@link Order}s are published.
	 * 
	 * @param orderBorad
	 */
	public void registerToObserve(OrderBord orderBorad);

	/**
	 * The {@link EventListService} allows the manipulation of the
	 * {@link EventList}. {@link NetworkService} provides information about the
	 * infrastructure and {@link StructureService} delivers information about
	 * the problem-structure.
	 * 
	 * @param eventListService
	 * @param network
	 * @param structure
	 */
	public void registerServices(EventListService eventListService, StructureService structureService,
			NetworkService networkService);

	/**
	 * Returns the daily behaviour represented as {@link ITour};
	 * 
	 * @return
	 */
	public ITour getMyDailyBehaviour();

}
