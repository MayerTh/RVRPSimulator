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
package vrpsim.simulationmodel.initialbehaviour.generator.api;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.network.Network;
import vrpsim.core.model.solution.IInitialBehaviourProvider;
import vrpsim.core.model.structure.Structure;

public interface IInitialBehaviourProviderGenerator {

	/**
	 * First called writing logs will be disabled.
	 */
	public void toggleCreateStatistics();
	
	/**
	 * Generates {@link IInitialBehaviourProvider} based on the given simulation
	 * model {@link Structure} and {@link Network}.
	 * 
	 * @param dynamicModel
	 * @return
	 */
	public IInitialBehaviourProvider generateInitialBehaviourProvider(String instanceName, VRPSimulationModel model, String statisticsOutpurFolder);

}
