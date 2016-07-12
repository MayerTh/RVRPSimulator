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

import java.util.Collections;
import java.util.List;

import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.depot.IDepot;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.util.storage.StorableParameters;
import vrpsim.core.model.structure.vehicle.IVehicle;

/**
 * Service for the {@link Structure}. Provides all helper functions.
 * 
 * @author mayert
 *
 */
public class StructureService {

	private final Structure structure;

	public StructureService(Structure structure) {
		this.structure = structure;
	}
	
	/**
	 * Returns the {@link IVehicle} by given id.
	 * 
	 * @param id
	 * @return
	 */
	public IVehicle getVehicle(String id) {
		IVehicle result = null;
		for(IVehicle vehicle : this.structure.getVehicles()) {
			if(vehicle.getVRPSimulationModelElementParameters().getId().equals(id)) {
				result = vehicle;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Returns the {@link IDriver} by given id.
	 * 
	 * @param id
	 * @return
	 */
	public IDriver getDriver(String id) {
		IDriver result = null;
		for(IDriver driver : this.structure.getDrivers()) {
			if(driver.getVRPSimulationModelElementParameters().getId().equals(id)) {
				result = driver;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Returns the {@link IDepot} by given id.
	 * 
	 * @param id
	 * @return
	 */
	public IDepot getDepot(String id) {
		IDepot result = null;
		for(IDepot depot : this.structure.getDepots()) {
			if(depot.getVRPSimulationModelElementParameters().getId().equals(id)) {
				result = depot;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Returns the {@link ICustomer} by given id.
	 * 
	 * @param id
	 * @return
	 */
	public ICustomer getCustomer(String id) {
		ICustomer result = null;
		for(ICustomer customer : this.structure.getCustomers()) {
			if(customer.getVRPSimulationModelElementParameters().getId().equals(id)) {
				result = customer;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Returns the {@link Collections#unmodifiableList(@link IVehicle)}.
	 * 
	 * @param id
	 * @return
	 */
	public List<IVehicle> getVehicles() {
		return Collections.unmodifiableList(this.structure.getVehicles());
	}
	
	/**
	 * Returns the {@link Collections#unmodifiableList(@link IDriver)}.
	 * 
	 * @param id
	 * @return
	 */
	public List<IDriver> getDrivers() {
		return Collections.unmodifiableList(this.structure.getDrivers());
	}
	
	/**
	 * Returns the {@link Collections#unmodifiableList(@link IDepot)}.
	 * 
	 * @param id
	 * @return
	 */
	public List<IDepot> getDepots() {
		return Collections.unmodifiableList(this.structure.getDepots());
	}
	
	/**
	 * Returns the {@link Collections#unmodifiableList(@link ICustomer)}.
	 * 
	 * @param id
	 * @return
	 */
	public List<ICustomer> getCustomers() {
		return Collections.unmodifiableList(this.structure.getCustomers());
	}
	
	/**
	 * Returns the {@link Collections#unmodifiableList(@link StorableParameters)}.
	 * 
	 * @param id
	 * @return
	 */
	public List<StorableParameters> getStorableparameters() {
		return Collections.unmodifiableList(this.structure.getStorableParameters());
	}
	
}
