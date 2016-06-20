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
package vrpsim.util.model.generator.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.network.DefaultWay;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IWay;
import vrpsim.core.model.network.Location;
import vrpsim.core.model.network.Network;
import vrpsim.core.model.network.StaticDefaultNode;
import vrpsim.util.model.generator.GeneratorConfigurationInitializationException;

public class RandomNetworkGenerator {

	/**
	 * Returns a random {@link Network} where all {@link INode}s are connected
	 * with each other.
	 * 
	 * @param numberNodes
	 * @param random
	 * @param distanceFunction
	 * @param timeFunction
	 * @param maxWaySpeed
	 * @return
	 * @throws GeneratorConfigurationInitializationException
	 */
	public Network generateRandomNetwork(Random random, RandomNetworkGeneratorConfiguration configuration)
			throws GeneratorConfigurationInitializationException {

		configuration.initialize();

		List<INode> nodes = new ArrayList<>();
		List<IWay> allWays = new ArrayList<>();

		for (int i = 0; i <= configuration.getNumberNodes(); i++) {
			VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(
					"node" + String.valueOf(i), 0);
			Location location = new Location(random.nextInt(configuration.getNumberNodes()*4),
					random.nextInt(configuration.getNumberNodes()*4), 0);
			INode node = new StaticDefaultNode(vrpSimulationModelElementParameters, location);
			nodes.add(node);
		}

		for (INode node1 : nodes) {
			List<IWay> ways = new ArrayList<>();
			for (INode node2 : nodes) {
				VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(
						node1.getVRPSimulationModelElementParameters().getId() + "-"
								+ node2.getVRPSimulationModelElementParameters().getId(),
						0);

				IWay way = new DefaultWay(vrpSimulationModelElementParameters, configuration.getTimeFunction(),
						node1, node2, configuration.getDistanceFunction(), configuration.getMaxWaySpeed());
				ways.add(way);
			}
			node1.setWays(ways);
			allWays.addAll(ways);
		}

		return new Network(nodes, allWays);
	}

}
