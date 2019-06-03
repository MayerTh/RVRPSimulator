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
package vrpsim.simulationmodel.impl;

import org.vrprep.model.instance.Instance;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.simulationmodel.api.ISimulationModelAPI;
import vrpsim.simulationmodel.api.ITransformationConfiguration;
import vrpsim.simulationmodel.impl.config.DefaultTransformationConfiguration;
import vrpsim.simulationmodel.impl.util.DynamicToSimulationModelTransformer;

public class SimulationModelAPI implements ISimulationModelAPI {

	@Override
	public VRPSimulationModel generateSimulationModel(DynamicVRPREPModel dynamicModel) {
		return this.generateSimulationModel(dynamicModel, new DefaultTransformationConfiguration());
	}
	
	@Override
	public VRPSimulationModel generateSimulationModel(DynamicVRPREPModel dynamicModel, ITransformationConfiguration transformationConfiguration) {
		DynamicToSimulationModelTransformer dynamicToSimulationModelTransformer = new DynamicToSimulationModelTransformer(dynamicModel, transformationConfiguration);
		return dynamicToSimulationModelTransformer.transformTo();
	}

	@Override
	public VRPSimulationModel generateSimulationModel(Instance staticModel) {
		throw new RuntimeException("Not jet implemenetd.");
	}

}
