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

import java.util.ArrayList;
import java.util.List;

import vrpsim.core.model.IVRPSimulationElement;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IWay;

/**
 * @date 23.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class Network {

	private final List<INode> nodes;
	private final List<IWay> ways;
	
	private NetworkService networkService;

	public Network(final List<INode> nodes, final List<IWay> ways) {
		this.nodes = nodes;
		this.ways = ways;
	}

	public void reset() {
		this.nodes.stream().forEach(e -> e.reset());
		this.ways.stream().forEach(e -> e.reset());
	}

	public List<INode> getNodes() {
		return this.nodes;
	}

	public List<IWay> getWays() {
		return this.ways;
	}

	/**
	 * Returns all existing {@link IVRPSimulationElement}.
	 * 
	 * @return
	 */
	public List<IVRPSimulationElement> getAllSimulationElements() {
		List<IVRPSimulationElement> allElements = new ArrayList<>();
		allElements.addAll(this.getNodes());
		allElements.addAll(this.getWays());
		return allElements;
	}

	/**
	 * Returns the {@link NetworkService} which should be used to get
	 * information and services for the {@link Network}.
	 * 
	 * @return
	 */
	public NetworkService getNetworkService() {
		if (this.networkService == null) {
			this.networkService = new NetworkService(this);
		}
		return this.networkService;
	}
}
