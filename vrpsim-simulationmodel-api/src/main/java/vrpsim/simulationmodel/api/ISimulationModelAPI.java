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
package vrpsim.simulationmodel.api;

import org.vrprep.model.instance.Instance;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.simulator.MainProgramm;
import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;

public interface ISimulationModelAPI {

	/**
	 * Generates and returns an {@link VRPSimulationModel}, which can be
	 * simulated with the help of {@link MainProgramm}, uses a default
	 * implementation of the {@link ITransformationConfiguration}
	 * 
	 * @param dynamicModel
	 * @return
	 */
	public VRPSimulationModel generateSimulationModel(DynamicVRPREPModel dynamicModel);

	/**
	 * Generates and returns an {@link VRPSimulationModel}, which can be
	 * simulated with the help of {@link MainProgramm}.
	 * 
	 * @param dynamicModel
	 * @param transformationConfiguration
	 * @return
	 */
	public VRPSimulationModel generateSimulationModel(DynamicVRPREPModel dynamicModel, ITransformationConfiguration transformationConfiguration);

	/**
	 * Generates and returns an {@link VRPSimulationModel}, which can be
	 * simulated with the help of {@link MainProgramm}.
	 * 
	 * @param staticModel
	 * @return
	 */
	public VRPSimulationModel generateSimulationModel(Instance staticModel);

}
