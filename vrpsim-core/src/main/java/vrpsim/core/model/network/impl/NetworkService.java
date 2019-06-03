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
package vrpsim.core.model.network.impl;

import java.util.Random;

import vrpsim.core.model.IVRPSimulationElement;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.depot.IDepot;

/**
 * Service for the {@link Network}. Provides all helper functions, like shortest
 * way from node to node.
 * 
 * @author mayert
 *
 */
public class NetworkService {

	private final Network network;

	public NetworkService(Network network) {
		this.network = network;
	}

	public Location getLocationByCustomer(ICustomer customer) {
		return ((INode) customer.getVRPSimulationModelStructureElementParameters().getHome()).getLocation();
	}
	
	public Location getLocationByDepot(IDepot depot) {
		return ((INode)depot.getVRPSimulationModelStructureElementParameters().getHome()).getLocation();
	}

	public INode getNodeByLoaction(Location loaction) {
		INode result = null;
		for (INode node : this.network.getNodes()) {
			if (node.getLocation().equals(loaction)) {
				result = node;
			}
		}
		return result;
	}

	/**
	 * Returns a random {@link INode} of the {@link Network}.
	 * 
	 * @param random
	 * @return
	 */
	public INode getRandomINode(Random random) {
		return this.network.getNodes().get(random.nextInt(this.network.getNodes().size()));
	}

	/**
	 * Returns an IVRPSimulationModelNetworkElement by given Id or null if an
	 * element with this ides does not exists.
	 * 
	 * @param id
	 * @return
	 */
	public IVRPSimulationModelNetworkElement getNetworkElement(String id) {
		IVRPSimulationModelNetworkElement elementToReturn = null;
		for (IVRPSimulationElement element : this.network.getAllSimulationElements()) {
			if (element instanceof IVRPSimulationModelNetworkElement) {
				if (((IVRPSimulationModelNetworkElement) element).getVRPSimulationModelElementParameters().getId().equals(id)) {
					elementToReturn = (IVRPSimulationModelNetworkElement) element;
					break;
				}
			}
		}
		return elementToReturn;
	}

}
