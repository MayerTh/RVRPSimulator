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
package vrpsim.examples.dynamiccustomer;

import java.util.Random;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.VRPSimulationModelParameters;
import vrpsim.core.model.network.Network;
import vrpsim.core.model.structure.Structure;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;
import vrpsim.core.simulator.MainProgramm;
import vrpsim.util.model.generator.GeneratorConfigurationInitializationException;
import vrpsim.util.model.generator.network.RandomNetworkGenerator;
import vrpsim.util.model.generator.network.RandomNetworkGeneratorConfiguration;
import vrpsim.util.model.generator.structure.RandomStructureGenerator;
import vrpsim.util.model.generator.structure.RandomStructureGeneratorConfiguration;
import vrpsim.visualization.Visualisation;

public class DynamicCustomer extends Visualisation {

	public static void main(String[] args)
			throws GeneratorConfigurationInitializationException, VRPArithmeticException, StorageException {

		Random random = new Random(1111);

		RandomNetworkGeneratorConfiguration networkConfig = new RandomNetworkGeneratorConfiguration();
		networkConfig.setNumberNodes(10);
		Network network = new RandomNetworkGenerator().generateRandomNetwork(random, networkConfig);

		RandomStructureGeneratorConfiguration structureConfig = new RandomStructureGeneratorConfiguration();
		structureConfig.setNetwork(network.getNetworkService()).setNumberOfStaticCustomers(0)
				.setNumberOfDynamicCustomers(5).setNumberOfVehicles(5).setNumberOfSourceDepot(1).setNumberOfDriver(5)
				.setNumberOfDefaultDepot(0);

		Structure structure = new RandomStructureGenerator().generatreRandomStructure(random, structureConfig);
		VRPSimulationModelParameters vrpSimulationModelParameters = new VRPSimulationModelParameters("DynamicCustomer",
				"thomas.mayer@unibw.de");

		MainProgramm mainProgramm = new MainProgramm();
		IClock clock = mainProgramm.getSimulationClock();
		ITime simulationEndTime = clock.getCurrentSimulationTime().createTimeFrom(432000.0); // 300 days
		
		VRPSimulationModel model = new VRPSimulationModel(vrpSimulationModelParameters, structure, network);

		init(mainProgramm, model,simulationEndTime);
		launch(args);

	}

}
