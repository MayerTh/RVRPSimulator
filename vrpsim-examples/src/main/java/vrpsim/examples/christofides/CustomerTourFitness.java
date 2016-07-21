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
/**
 * 
 */
package vrpsim.examples.christofides;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.xml.bind.JAXBException;

import de.terministic.serein.api.Individual;
import de.terministic.serein.core.fitness.AbstractFitnessFunction;
import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.behaviour.Behaviour;
import vrpsim.core.model.solution.SolutionManager;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.util.exceptions.BehaviourException;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.exceptions.InitializationException;
import vrpsim.core.model.util.exceptions.NetworkException;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.simulator.MainProgramm;
import vrpsim.examples.support.CustomerTour;
import vrpsim.examples.support.VRPREPImporter;

/**
 * @date 25.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class CustomerTourFitness extends AbstractFitnessFunction<CustomerTour> {

	// private static Logger logger =
	// LoggerFactory.getLogger(CustomerTourFitness.class);

	private final String storageType = "Storage Type A";
	private final String storableType = "Item Type A";
	private final String capacityType = "CAPACITY_TYPE_1";

	private final double consumptionCycleTime = 100.0;
	private final int maxCustomerStorageCapacity = 10000;
	private final int numberOfStorablesInDepot = 5000;
	private final int numberOfVehicles = 1;

	private VRPSimulationModel model;
	private MainProgramm mainProgramm;
	private CustomerTourSolutionTranslator customerTourTranslator;

	private final VRPREPImporter importer;
	private final Path path;

	public CustomerTourFitness(String modelFolder, String modelName) throws URISyntaxException {

		importer = new VRPREPImporter(storageType, storableType, capacityType, consumptionCycleTime,
				maxCustomerStorageCapacity, numberOfStorablesInDepot, numberOfVehicles);
		path = Paths.get(ClassLoader.class.getResource(modelFolder + "/" + modelName).toURI());
		this.setModel();
	}

	public CustomerTour getInitialTour() {
		List<String> customerIds = new ArrayList<String>();
		for (ICustomer customer : this.model.getStructure().getCustomers()) {
			customerIds.add(customer.getVRPSimulationModelElementParameters().getId());
		}
		return new CustomerTour(customerIds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.terministic.serein.core.fitness.AbstractFitnessComparator#
	 * calculateFitness(de.terministic.serein.api.Individual)
	 */
	@Override
	protected Double calculateFitness(Individual<CustomerTour, ?> individual) {

		this.customerTourTranslator = new CustomerTourSolutionTranslator();
		this.mainProgramm = new MainProgramm();

		try {

			CustomerTour customerTour = individual.getPhenotype();
			Behaviour behaviour = this.customerTourTranslator.translate(customerTour, this.model,
					this.mainProgramm.getSimulationClock());
			this.model.setSolutionManager(new SolutionManager((network, structure) -> behaviour));
			// this.model.setBehaviour(behaviour);

		} catch (NetworkException | BehaviourException | VRPArithmeticException e) {
			e.printStackTrace();
			throw new RuntimeErrorException(new Error(e.getMessage()));
		}

		try {
			this.mainProgramm.run(this.model, consumptionCycleTime + 1.0);
		} catch (EventException | InterruptedException | InitializationException e) {
			e.printStackTrace();
			throw new RuntimeErrorException(new Error(e.getMessage()));
		}

		double costs = this.model.getSolutionManager().getStaticBehaviourFromStaticBehaviourProvider().getTours().get(0)
				.getCurrentTourCosts();
		this.setModel();
		return costs;
	}

	public Behaviour translateToSolution(CustomerTour customerTour)
			throws NetworkException, BehaviourException, VRPArithmeticException {
		this.customerTourTranslator = new CustomerTourSolutionTranslator();
		this.mainProgramm = new MainProgramm();
		Behaviour solution = this.customerTourTranslator.translate(customerTour, this.model,
				this.mainProgramm.getSimulationClock());
		return solution;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.terministic.serein.api.FitnessFunction#isNaturalOrder()
	 */
	public boolean isNaturalOrder() {
		return false;
	}

	private void setModel() {
		try {
			this.model = importer.getSimulationModelWithoutSolutionFromVRPREPModel(path);
		} catch (JAXBException | VRPArithmeticException | StorageException e) {
			e.printStackTrace();
			throw new RuntimeException("Can not build the simultion model from optimization model.");
		}
	}
}
