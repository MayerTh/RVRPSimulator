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
package vrpsim.core.model.structure;

import java.util.ArrayList;
import java.util.List;

import vrpsim.core.model.IVRPSimulationElement;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.depot.IDepot;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.occasionaldriver.IOccasionalDriver;
import vrpsim.core.model.structure.util.storage.StorableParameters;
import vrpsim.core.model.structure.vehicle.IVehicle;

/**
 * @date 24.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class Structure {

	private final List<IDepot> depots;
	private final List<ICustomer> customers;
	private final List<IVehicle> vehicles;
	private final List<IDriver> drivers;
	private final List<IOccasionalDriver> occasionalDrivers;

	private final List<StorableParameters> storableParameters;
	
	private StructureService structureService;

	/**
	 * @param storableParameters - list of available goods
	 * @param depots - available depots
	 * @param customers - available customer
	 * @param vehicles - available vehicle
	 * @param drivers - available driver
	 * @param occasionalDrivers - available occasional driver
	 */
	public Structure(List<StorableParameters> storableParameters, List<IDepot> depots, List<ICustomer> customers,
			List<IVehicle> vehicles, List<IDriver> drivers, List<IOccasionalDriver> occasionalDrivers) {
		this.storableParameters = storableParameters;
		this.depots = depots == null ? new ArrayList<>() : depots;
		this.customers = customers == null ? new ArrayList<>() : customers;
		this.vehicles = vehicles == null ? new ArrayList<>() : vehicles;
		this.drivers = drivers == null ? new ArrayList<>() : drivers;
		this.occasionalDrivers = occasionalDrivers == null ? new ArrayList<>() : occasionalDrivers;
	}
	
	/**
	 * @param storableParameters - available goods
	 * @param depots - available depots
	 * @param customers - available customer
	 * @param vehicles - available vehicle
	 * @param drivers - available driver
	 * @param occasionalDrivers - available occasional driver
	 */
	public Structure(StorableParameters storableParameters, List<IDepot> depots, List<ICustomer> customers,
			List<IVehicle> vehicles, List<IDriver> drivers, List<IOccasionalDriver> occasionalDrivers) {
		this.depots = depots == null ? new ArrayList<>() : depots;
		this.customers = customers == null ? new ArrayList<>() : customers;
		this.vehicles = vehicles == null ? new ArrayList<>() : vehicles;
		this.drivers = drivers == null ? new ArrayList<>() : drivers;
		this.occasionalDrivers = occasionalDrivers == null ? new ArrayList<>() : occasionalDrivers;

		this.storableParameters = new ArrayList<>();
		this.storableParameters.add(storableParameters);
	}

	/**
	 * Returns the available {@link StorableParameters}s within the system to
	 * simulate.
	 * 
	 * @return
	 */
	public List<StorableParameters> getStorableParameters() {
		return storableParameters;
	}

	/**
	 * Returns the available {@link IDepot}s within the system to simulate.
	 * 
	 * @return
	 */
	public List<IDepot> getDepots() {
		return depots;
	}

	/**
	 * Returns the available {@link ICustomer}s within the system to simulate.
	 * 
	 * @return
	 */
	public List<ICustomer> getCustomers() {
		return customers;
	}

	/**
	 * Returns the available {@link IVehicle}s within the system to simulate.
	 * 
	 * @return
	 */
	public List<IVehicle> getVehicles() {
		return vehicles;
	}

	/**
	 * Returns the available {@link IDriver }s within the system to simulate.
	 * 
	 * @return
	 */
	public List<IDriver> getDrivers() {
		return drivers;
	}

	/**
	 * Returns the available {@link IOccasionalDriver}s within the system to simulate.
	 * 
	 * @return
	 */
	public List<IOccasionalDriver> getOccasionalDrivers() {
		return this.occasionalDrivers;
	}

	/**
	 * Returns all existing {@link IVRPSimulationElement}.
	 * 
	 * @return
	 */
	public List<IVRPSimulationElement> getAllSimulationElements() {
		List<IVRPSimulationElement> allElements = new ArrayList<>();
		allElements.addAll(this.customers);
		allElements.addAll(this.depots);
		allElements.addAll(this.drivers);
		allElements.addAll(this.vehicles);
		allElements.addAll(this.occasionalDrivers);
		return allElements;
	}
	
	/**
	 * Returns the {@link StructureService} which should be used to get
	 * information and services for the {@link Structure}.
	 * 
	 * @return
	 */
	public StructureService getStructureService() {
		if(this.structureService == null) {
			this.structureService = new StructureService(this);
		}
		return this.structureService;
	}

}
