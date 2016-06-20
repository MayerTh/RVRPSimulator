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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.VRPSimulationModelElementParameters;
import vrpsim.core.model.VRPSimulationModelParameters;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IWay;
import vrpsim.core.model.network.Location;
import vrpsim.core.model.network.Network;
import vrpsim.core.model.network.StaticDefaultNode;
import vrpsim.core.model.network.DefaultWay;
import vrpsim.core.model.structure.Structure;
import vrpsim.core.model.structure.VRPSimulationModelStructureElementParameters;
import vrpsim.core.model.structure.customer.DefaultNonDynamicCustomer;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.depot.DefaultDepot;
import vrpsim.core.model.structure.depot.IDepot;
import vrpsim.core.model.structure.driver.DefaultDriver;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.occasionaldriver.IOccasionalDriver;
import vrpsim.core.model.structure.util.storage.CanStoreParameters;
import vrpsim.core.model.structure.util.storage.CanStoreType;
import vrpsim.core.model.structure.util.storage.Capacity;
import vrpsim.core.model.structure.util.storage.Compartment;
import vrpsim.core.model.structure.util.storage.DefaultStorage;
import vrpsim.core.model.structure.util.storage.ICanStore;
import vrpsim.core.model.structure.util.storage.IStorable;
import vrpsim.core.model.structure.util.storage.IStorableGenerator;
import vrpsim.core.model.structure.util.storage.StorableGenerator;
import vrpsim.core.model.structure.util.storage.StorableParameters;
import vrpsim.core.model.structure.util.storage.StorableType;
import vrpsim.core.model.structure.vehicle.DefaultVehicle;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.distances.Euclidean2DDistanceFunction;
import vrpsim.core.model.util.distances.ZeroTravelTimeFunction;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.policies.LIFOLoadingPolicy;
import vrpsim.core.model.util.uncertainty.DeterministicDistributionFunction;
import vrpsim.core.model.util.uncertainty.UncertainParamters;

public class SimpleModelGenerator {

	private final String capacityUnit = "piece";
	private final CanStoreType canStoreType = new CanStoreType("carton");
	private final StorableType storableType = new StorableType("pizza", canStoreType);

	private final Capacity singleCapacity = new Capacity(capacityUnit, 1.0);
	private final StorableParameters storableParameters = new StorableParameters(1, singleCapacity, storableType);

	public VRPSimulationModel generateSimpleModel(int seed) throws VRPArithmeticException, StorageException {

		Random random = new Random(seed);
		Network network = generateSimpleRandomNetwork(100, random);
		Structure structure = generateSimpleStructure(network, random);
		VRPSimulationModelParameters vrpSimulationModelParameters = new VRPSimulationModelParameters(
				"Generated by SimpleModelGenerator with random seed " + seed + ".", "thomas.mayer@unibw.de");
		VRPSimulationModel model = new VRPSimulationModel(vrpSimulationModelParameters, structure, network);
		return model;
	}

	private Structure generateSimpleStructure(Network network, Random random)
			throws VRPArithmeticException, StorageException {
		INode home = network.getNodes().get(random.nextInt(network.getNodes().size() - 1));
		List<IDepot> depots = getDepots(1, 1, 10000, 10000, home);
		List<IVehicle> vehicles = getVehicles(2, 1, 40, home);
		List<IDriver> drivers = getDrivers(3, 1, home);
		List<ICustomer> customers = getCustomers(28, 1, 100, 0, network.getNodes(), random);
		List<IOccasionalDriver> occasionalDrivers = new ArrayList<>();
		return new Structure(storableParameters, depots, customers, vehicles, drivers, occasionalDrivers);
	}

	private List<ICustomer> getCustomers(int numberOfCustomers, int startIndex, double customerCapacity,
			int numberItemsInsideCustomer, List<INode> homes, Random random)
			throws VRPArithmeticException, StorageException {
		List<ICustomer> customers = new ArrayList<>();
		for (int i = startIndex; i <= numberOfCustomers + startIndex; i++) {

			INode home = homes.get(random.nextInt(homes.size() - 1));

			VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(
					"customer" + String.valueOf(i), 0);
			VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters = new VRPSimulationModelStructureElementParameters(
					home);

			CanStoreParameters compartmentParameters = new CanStoreParameters(canStoreType,
					new Capacity(capacityUnit, customerCapacity), new LIFOLoadingPolicy(), new StorableGenerator(this.storableParameters));
			ICanStore compartment = new Compartment(compartmentParameters);
			DefaultStorage storage = new DefaultStorage(canStoreType, compartment);

			IStorableGenerator storableGenerator = new StorableGenerator(this.storableParameters);
			ICustomer customer = new DefaultNonDynamicCustomer(vrpSimulationModelElementParameters,
					vrpSimulationModelStructureElementParameters,
					new UncertainParamters(new UncertainParamters.UncertainParameterContainer(storableParameters,
							new DeterministicDistributionFunction(10.0), new DeterministicDistributionFunction(100.0))),
					storage);
			for (int s = 1; s <= numberItemsInsideCustomer; s++) {
				IStorable storable = storableGenerator.generateStorable(this.storableParameters);
				compartment.load(storable);
			}
			customers.add(customer);
		}
		return customers;
	}

	private List<IDepot> getDepots(int numberOfDepots, int startIndex, double depotCapacity, int numberItemsInsideDepot,
			INode home) throws VRPArithmeticException, StorageException {
		List<IDepot> depots = new ArrayList<>();
		for (int i = startIndex; i <= numberOfDepots + startIndex; i++) {
			VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(
					"depot" + String.valueOf(i), 0);
			VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters = new VRPSimulationModelStructureElementParameters(
					home);

			CanStoreParameters compartmentParameters = new CanStoreParameters(canStoreType,
					new Capacity(capacityUnit, depotCapacity), new LIFOLoadingPolicy(), new StorableGenerator(this.storableParameters));
			ICanStore compartment = new Compartment(compartmentParameters);
			DefaultStorage storage = new DefaultStorage(canStoreType, compartment);

			IStorableGenerator storableGenerator = new StorableGenerator(this.storableParameters);
			IDepot depot = new DefaultDepot(vrpSimulationModelElementParameters,
					vrpSimulationModelStructureElementParameters, new UncertainParamters(), storage);

			for (int s = 1; s <= numberItemsInsideDepot; s++) {
				IStorable storable = storableGenerator.generateStorable(this.storableParameters);
				compartment.load(storable);
			}

			depots.add(depot);
		}
		return depots;

	}

	private List<IVehicle> getVehicles(int numberVehicles, int startIndex, double vehicleCapacity, INode home) {
		List<IVehicle> vehicles = new ArrayList<>();
		for (int i = startIndex; i <= numberVehicles + startIndex; i++) {
			VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(
					"vehicle" + String.valueOf(i), 0);
			VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters = new VRPSimulationModelStructureElementParameters(
					home);

			CanStoreParameters compartmentParameters = new CanStoreParameters(canStoreType,
					new Capacity(capacityUnit, vehicleCapacity), new LIFOLoadingPolicy(), new StorableGenerator(this.storableParameters));
			ICanStore compartment = new Compartment(compartmentParameters);
			DefaultStorage storage = new DefaultStorage(canStoreType, compartment);

			IVehicle vehicle = new DefaultVehicle(vrpSimulationModelElementParameters,
					vrpSimulationModelStructureElementParameters, new UncertainParamters(), storage, 80.0);
			vehicles.add(vehicle);
		}
		return vehicles;
	}

	private List<IDriver> getDrivers(int numberDrivers, int startIndex, INode home) {
		List<IDriver> drivers = new ArrayList<>();
		for (int i = startIndex; i <= numberDrivers + startIndex; i++) {
			VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(
					"driver" + String.valueOf(i), 0);
			VRPSimulationModelStructureElementParameters vrpSimulationModelStructureElementParameters = new VRPSimulationModelStructureElementParameters(
					home);

			IDriver driver = new DefaultDriver(vrpSimulationModelElementParameters,
					vrpSimulationModelStructureElementParameters, new UncertainParamters());
			drivers.add(driver);
		}

		return drivers;
	}

	private Network generateSimpleRandomNetwork(int numberNodes, Random random) {

		List<INode> nodes = new ArrayList<>();
		List<IWay> allWays = new ArrayList<>();

		for (int i = 1; i <= numberNodes; i++) {
			VRPSimulationModelElementParameters vrpSimulationModelElementParameters = new VRPSimulationModelElementParameters(
					"node" + String.valueOf(i), 0);
			Location location = new Location(random.nextInt(numberNodes), random.nextInt(numberNodes), 0);
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

				IWay way = new DefaultWay(vrpSimulationModelElementParameters, new ZeroTravelTimeFunction(),
						node1, node2, new Euclidean2DDistanceFunction(), 100.0);
				ways.add(way);
			}
			node1.setWays(ways);
			allWays.addAll(ways);
		}

		return new Network(nodes, allWays);
	}

}
