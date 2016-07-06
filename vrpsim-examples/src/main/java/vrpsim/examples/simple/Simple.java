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
package vrpsim.examples.simple;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.behaviour.Behaviour;
import vrpsim.core.model.solution.SolutionManager;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.exceptions.BehaviourException;
import vrpsim.core.model.util.exceptions.NetworkException;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;
import vrpsim.core.simulator.MainProgramm;
import vrpsim.visualization.Visualisation;

public class Simple extends Visualisation {

	private static int seed = 4321;
	
	public static void main(String[] args) throws JAXBException, VRPArithmeticException, StorageException,
			NetworkException, BehaviourException, EventException, FileNotFoundException {

		SimpleModelGenerator simpleModelGenerator = new SimpleModelGenerator();
		
		MainProgramm mainProgramm = new MainProgramm();
		IClock clock = mainProgramm.getSimulationClock();
		ITime simulationEndTime = clock.getCurrentSimulationTime().createTimeFrom(100.0);
		
		VRPSimulationModel model = simpleModelGenerator.generateSimpleModel(seed);

		SimpleBehaviorGenerator simpleBehaviorGenerator = new SimpleBehaviorGenerator();
		Behaviour behaviour = simpleBehaviorGenerator.createRandomBehaviour(model, clock);
		model.setSolutionManager(new SolutionManager((structure, network) -> behaviour));

		init(mainProgramm, model, simulationEndTime);
		launch(args);
	}

}
