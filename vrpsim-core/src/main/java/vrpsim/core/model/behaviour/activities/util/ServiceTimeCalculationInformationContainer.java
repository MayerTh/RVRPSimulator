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
package vrpsim.core.model.behaviour.activities.util;

import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.util.storage.StorableParameters;
import vrpsim.core.model.structure.vehicle.IVehicle;

/**
 * Provides information necessary to calculate a service time.
 * 
 * @author mayert
 */
public class ServiceTimeCalculationInformationContainer {

	private final IVehicle vehicle;
	private final IDriver driver;
	private final IVRPSimulationModelStructureElementWithStorage vrpSimulationModelStructureElementWithStorage;
	private final StorableParameters storableParameters;
	private final Integer amount;

	public ServiceTimeCalculationInformationContainer(IVehicle vehicle, IDriver driver,
			IVRPSimulationModelStructureElementWithStorage vrpSimulationModelStructureElementWithStorage,
			StorableParameters storableParameters, Integer amount) {
		super();
		this.vehicle = vehicle;
		this.driver = driver;
		this.vrpSimulationModelStructureElementWithStorage = vrpSimulationModelStructureElementWithStorage;
		this.storableParameters = storableParameters;
		this.amount = amount;
	}

	public IVehicle getVehicle() {
		return vehicle;
	}

	public IDriver getDriver() {
		return driver;
	}

	public IVRPSimulationModelStructureElementWithStorage getVrpSimulationModelStructureElementWithStorage() {
		return vrpSimulationModelStructureElementWithStorage;
	}

	public StorableParameters getStorableParameters() {
		return storableParameters;
	}

	public Integer getAmount() {
		return amount;
	}

}
