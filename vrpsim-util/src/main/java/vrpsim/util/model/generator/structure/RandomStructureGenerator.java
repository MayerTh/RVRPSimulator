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
package vrpsim.util.model.generator.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.structure.Structure;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.customer.DynamicCustomer;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.customer.StaticCustomerWithConsumption;
import vrpsim.core.model.structure.depot.DefaultDepot;
import vrpsim.core.model.structure.depot.IDepot;
import vrpsim.core.model.structure.depot.SourceDepot;
import vrpsim.core.model.structure.driver.DefaultDriver;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.occasionaldriver.IOccasionalDriver;
import vrpsim.core.model.structure.util.storage.CanStoreParameters;
import vrpsim.core.model.structure.util.storage.Capacity;
import vrpsim.core.model.structure.util.storage.Compartment;
import vrpsim.core.model.structure.util.storage.DefaultStorage;
import vrpsim.core.model.structure.util.storage.DefaultStorageManager;
import vrpsim.core.model.structure.util.storage.ICanStore;
import vrpsim.core.model.structure.util.storage.IStorable;
import vrpsim.core.model.structure.util.storage.IStorableGenerator;
import vrpsim.core.model.structure.util.storage.StorableGenerator;
import vrpsim.core.model.structure.util.storage.StorableParameters;
import vrpsim.core.model.structure.vehicle.DefaultVehicle;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.functions.DeterministicTimeFunction;
import vrpsim.core.model.util.policies.LIFOLoadingPolicy;
import vrpsim.core.model.util.uncertainty.DeterministicDistributionFunction;
import vrpsim.core.model.util.uncertainty.UncertainParamters;
import vrpsim.core.model.util.uncertainty.UncertainParamters.UncertainParameterContainer;
import vrpsim.util.model.generator.GeneratorConfigurationInitializationException;

public class RandomStructureGenerator {

	/**
	 * <ul>
	 * 
	 * <li>Default- and SourceDepots</li>
	 * <ul>
	 * <li>Homogeneous (storage, like configured)</li>
	 * <li>No automatic refill</li>
	 * <li>Home location = for each *Depot, a random home location is generated
	 * and stored in homeLocations</li>
	 * <li>Loading policy: LIFO</li>
	 * </ul>
	 * 
	 * <li>Fleet/Vehicles</li>
	 * <ul>
	 * <li>Homogeneous (storage, speed)</li>
	 * <li>No breakdowns</li>
	 * <li>Home location = homeLocations.get(i % homeLocations.size())</li>
	 * <li>Loading policy: LIFO</li>
	 * </ul>
	 * 
	 * <li>Drivers</li>
	 * <ul>
	 * <li>Homogeneous (home location like vehicles)</li>
	 * <li>No sick leaves</li>
	 * <li>Home location = homeLocations.get(i % homeLocations.size())</li>
	 * </ul>
	 * 
	 * <li>Static- and DynamicCustomers</li>
	 * <ul>
	 * <li>Homogeneous (storage)</li>
	 * <li></li>
	 * <li></li>
	 * </ul>
	 * </ul>
	 * 
	 * @param random
	 * @param configuration
	 * @return
	 * @throws GeneratorConfigurationInitializationException
	 * @throws StorageException
	 * @throws VRPArithmeticException
	 */
	public Structure generatreRandomStructure(Random random, RandomStructureGeneratorConfiguration configuration)
			throws GeneratorConfigurationInitializationException, VRPArithmeticException, StorageException {

		configuration.initialize();

		List<INode> homeLocations = new ArrayList<>();
		for (int i = 0; i < configuration.getNumberOfSourceDepot() + configuration.getNumberOfDefaultDepot(); i++) {
			homeLocations.add(configuration.getNetworkService().getRandomINode(random));
		}

		StorableParameters storableParameters = configuration.getStorableParameters();
		List<IVehicle> vehicles = this.getVehicles(random, homeLocations, configuration);
		List<IDriver> drivers = this.getDrivers(random, homeLocations, configuration);
		List<IDepot> depots = this.getDepots(random, homeLocations, configuration);

		List<ICustomer> customers = this.getCustomers(random, configuration);
		List<IOccasionalDriver> occasionalDrivers = this.getOccasionalDrivers(random, configuration);

		Structure strucutre = new Structure(storableParameters, depots, customers, vehicles, drivers,
				occasionalDrivers);

		return strucutre;
	}

	private List<IOccasionalDriver> getOccasionalDrivers(Random random,
			RandomStructureGeneratorConfiguration randomStructureGeneratorConfiguration) {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	private List<IDriver> getDrivers(Random random, List<INode> homeLocations,
			RandomStructureGeneratorConfiguration randomStructureGeneratorConfiguration) {
		List<IDriver> drivers = new ArrayList<>();
		for (int i = 0; i < randomStructureGeneratorConfiguration.getNumberOfDriver(); i++) {
			VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(
					"driver:" + String.valueOf(i), 0);
			VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters = new VRPSimulationModelStructureElementParameters(
					homeLocations.get(i % homeLocations.size()));

			IDriver driver = new DefaultDriver(vrpSimulationModelElementParameters,
					vrpSimulationModelStructureElementParameters, new UncertainParamters());
			drivers.add(driver);
		}
		return drivers;
	}

	private List<IVehicle> getVehicles(Random random, List<INode> homeLocations,
			RandomStructureGeneratorConfiguration randomStructureGeneratorConfiguration) {

		List<IVehicle> vehicles = new ArrayList<>();
		for (int i = 0; i < randomStructureGeneratorConfiguration.getNumberOfVehicles(); i++) {
			VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(
					"vehicle:" + String.valueOf(i), 0);
			VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters = new VRPSimulationModelStructureElementParameters(
					homeLocations.get(i % homeLocations.size()));

			CanStoreParameters compartmentParameters = new CanStoreParameters(
					randomStructureGeneratorConfiguration.getCanStoreType(),
					new Capacity(randomStructureGeneratorConfiguration.getCapacityUnit(),
							randomStructureGeneratorConfiguration.getCapacityOfVehicles()),
					new LIFOLoadingPolicy(),
					new StorableGenerator(randomStructureGeneratorConfiguration.getStorableParameters()));

			ICanStore compartment = new Compartment(compartmentParameters);
			DefaultStorageManager storageManager = new DefaultStorageManager(new DefaultStorage(compartment));

			// new UncertainParamters() -> means empty/no breakdowns.
			IVehicle vehicle = new DefaultVehicle(vrpSimulationModelElementParameters,
					vrpSimulationModelStructureElementParameters, new UncertainParamters(), storageManager,
					randomStructureGeneratorConfiguration.getSpeedOfVehicles());
			vehicles.add(vehicle);
		}
		return vehicles;
	}

	private List<IDepot> getDepots(Random random, List<INode> homeLocations,
			RandomStructureGeneratorConfiguration randomStructureGeneratorConfiguration)
			throws VRPArithmeticException, StorageException {

		List<IDepot> depots = new ArrayList<>();
		for (int i = 0; i < randomStructureGeneratorConfiguration.getNumberOfSourceDepot()
				+ randomStructureGeneratorConfiguration.getNumberOfDefaultDepot(); i++) {

			IDepot depot = null;
			if (i < randomStructureGeneratorConfiguration.getNumberOfSourceDepot()) {
				// Source Depot
				VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(
						"source_depot:" + String.valueOf(i), 0);
				VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters = new VRPSimulationModelStructureElementParameters(
						homeLocations.get(i % homeLocations.size()));
				CanStoreParameters compartmentParameters = new CanStoreParameters(
						randomStructureGeneratorConfiguration.getCanStoreType(),
						new Capacity(randomStructureGeneratorConfiguration.getCapacityUnit(),
								randomStructureGeneratorConfiguration.getCapacityOfSourceDepot()),
						new LIFOLoadingPolicy(),
						new StorableGenerator(randomStructureGeneratorConfiguration.getStorableParameters()));

				ICanStore compartment = new Compartment(compartmentParameters);
				DefaultStorageManager storageManager = new DefaultStorageManager(new DefaultStorage(compartment));

				depot = new SourceDepot(vrpSimulationModelElementParameters,
						vrpSimulationModelStructureElementParameters, new UncertainParamters(), storageManager, new DeterministicTimeFunction(0.0));

			} else {
				// Default Depot
				VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(
						"default_depot:" + String.valueOf(i), 0);
				VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters = new VRPSimulationModelStructureElementParameters(
						homeLocations.get(i % homeLocations.size()));
				CanStoreParameters compartmentParameters = new CanStoreParameters(
						randomStructureGeneratorConfiguration.getCanStoreType(),
						new Capacity(randomStructureGeneratorConfiguration.getCapacityUnit(),
								randomStructureGeneratorConfiguration.getCapacityOfDefaultDepot()),
						new LIFOLoadingPolicy(),
						new StorableGenerator(randomStructureGeneratorConfiguration.getStorableParameters()));

				ICanStore compartment = new Compartment(compartmentParameters);
				DefaultStorageManager storageManager = new DefaultStorageManager(new DefaultStorage(compartment));

				depot = new DefaultDepot(vrpSimulationModelElementParameters,
						vrpSimulationModelStructureElementParameters, new UncertainParamters(), storageManager, new DeterministicTimeFunction(0.0));
				IStorableGenerator storableGenerator = new StorableGenerator(
						randomStructureGeneratorConfiguration.getStorableParameters());
				for (int s = 1; s <= randomStructureGeneratorConfiguration.getNumberStorablesInDefaultDepot(); s++) {
					IStorable storable = storableGenerator.generateDefaultStorable();
					compartment.load(storable);
				}
			}
			depots.add(depot);

		}
		return depots;
	}

	private List<ICustomer> getCustomers(Random random,
			RandomStructureGeneratorConfiguration randomStructureGeneratorConfiguration) {
		List<ICustomer> customers = new ArrayList<>();
		for (int i = 0; i < randomStructureGeneratorConfiguration.getNumberOfStaticCustomers(); i++) {

			// Static customers
			VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(
					"default_customer:" + String.valueOf(i), 0);
			VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters = new VRPSimulationModelStructureElementParameters(
					randomStructureGeneratorConfiguration.getNetworkService().getRandomINode(random));

			CanStoreParameters compartmentParameters = new CanStoreParameters(
					randomStructureGeneratorConfiguration.getCanStoreType(),
					new Capacity(randomStructureGeneratorConfiguration.getCapacityUnit(),
							randomStructureGeneratorConfiguration.getCapacityOfStaticCustomers()),
					new LIFOLoadingPolicy(),
					new StorableGenerator(randomStructureGeneratorConfiguration.getStorableParameters()));
			ICanStore compartment = new Compartment(compartmentParameters);
			DefaultStorageManager storageManager = new DefaultStorageManager(new DefaultStorage(compartment));

			Double minAmount = randomStructureGeneratorConfiguration.getMinConsumptionAmountOfStaticCustomer();
			Double maxAmount = randomStructureGeneratorConfiguration.getMaxConsumptionAmountOfStaticCustomer();
			Double minCycle = randomStructureGeneratorConfiguration.getMinConsumptionCycleOfStaticCustomer();
			Double maxCycle = randomStructureGeneratorConfiguration.getMaxConsumptionCycleOfStaticCustomer();

			UncertainParameterContainer consumparameterContainer = new UncertainParamters.UncertainParameterContainer(
					randomStructureGeneratorConfiguration.getStorableParameters(),
					new DeterministicDistributionFunction(
							random.nextInt(maxAmount.intValue() - minAmount.intValue()) + minAmount),
					new DeterministicDistributionFunction(0.0), new DeterministicDistributionFunction(
							random.nextInt(maxCycle.intValue() - minCycle.intValue()) + minCycle),
					false, true);
			UncertainParamters consumptionParameters = new UncertainParamters(consumparameterContainer);

			ICustomer customer = new StaticCustomerWithConsumption(vrpSimulationModelElementParameters,
					vrpSimulationModelStructureElementParameters, consumptionParameters, storageManager,
					new DeterministicTimeFunction(0.0));

			customers.add(customer);
		}

		for (int i = 0; i < randomStructureGeneratorConfiguration.getNumberOfDynamicCustomers(); i++) {

			// Dynamic customers
			VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(
					"dynamic_customer:" + String.valueOf(i), 0);
			VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters = new VRPSimulationModelStructureElementParameters(
					randomStructureGeneratorConfiguration.getNetworkService().getRandomINode(random));

			CanStoreParameters compartmentParameters = new CanStoreParameters(
					randomStructureGeneratorConfiguration.getCanStoreType(),
					new Capacity(randomStructureGeneratorConfiguration.getCapacityUnit(),
							randomStructureGeneratorConfiguration.getCapacityOfDynamicCustomers()),
					new LIFOLoadingPolicy(),
					new StorableGenerator(randomStructureGeneratorConfiguration.getStorableParameters()));
			ICanStore compartment = new Compartment(compartmentParameters);
			DefaultStorageManager storageManager = new DefaultStorageManager(new DefaultStorage(compartment));

			Double minAmount = randomStructureGeneratorConfiguration.getMinOrderAmountOfDynamicCustomer();
			Double maxAmount = randomStructureGeneratorConfiguration.getMaxOrderAmountOfDynamicCustomer();
			Double minCycle = randomStructureGeneratorConfiguration.getMinOrderCycleOfDynamicCustomer();
			Double maxCycle = randomStructureGeneratorConfiguration.getMaxOrderCycleOfDynamicCustomer();

			UncertainParamters orderParameters = new UncertainParamters(
					new UncertainParamters.UncertainParameterContainer(
							randomStructureGeneratorConfiguration.getStorableParameters(),
							new DeterministicDistributionFunction(
									random.nextInt(maxAmount.intValue() - minAmount.intValue()) + minAmount),
							new DeterministicDistributionFunction(
									random.nextInt(maxCycle.intValue() - minCycle.intValue()) + minCycle),
							new DeterministicDistributionFunction(
									random.nextInt(maxCycle.intValue() - minCycle.intValue()) + minCycle)));

			ICustomer customer = new DynamicCustomer(vrpSimulationModelElementParameters,
					vrpSimulationModelStructureElementParameters, storageManager, orderParameters,
					new DeterministicTimeFunction(0.0));

			customers.add(customer);
		}

		return customers;
	}
}
