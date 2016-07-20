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

import vrpsim.core.model.network.Location;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorageMovable;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.util.storage.StorableParameters;
import vrpsim.core.model.util.functions.IDistanceFunction;

/**
 * Provides information necessary to calculate a service or travel time. Note,
 * that parameters can be null.
 * 
 * @author mayert
 */
public class TimeCalculationInformationContainer {

	private final IVRPSimulationModelStructureElementWithStorageMovable vehicle;
	private final IDriver driver;
	private final IVRPSimulationModelStructureElementWithStorage vrpSimulationModelStructureElementWithStorage;
	private final StorableParameters storableParameters;
	private final Integer amount;

	private final Location source;
	private final Location target;
	private final IDistanceFunction distanceFunction;
	private final Double maxWaySpeed;

	/**
	 * Constructor typically used to crate a container to calculate a service
	 * time.
	 * 
	 * @param vehicle
	 * @param driver
	 * @param vrpSimulationModelStructureElementWithStorage
	 * @param storableParameters
	 * @param amount
	 */
	public TimeCalculationInformationContainer(
			final IVRPSimulationModelStructureElementWithStorageMovable vehicle,
			final IDriver driver,
			final IVRPSimulationModelStructureElementWithStorage vrpSimulationModelStructureElementWithStorage,
			final StorableParameters storableParameters, final Integer amount) {
		this(vehicle, driver, vrpSimulationModelStructureElementWithStorage, storableParameters, amount, null, null,
				null, null);
	}

	/**
	 * Constructor typically used to create a travel time.
	 * 
	 * @param vehicle
	 * @param source
	 * @param target
	 * @param distanceFunction
	 * @param maxWaySpeed
	 */
	public TimeCalculationInformationContainer(IVRPSimulationModelStructureElementWithStorageMovable vehicle,
			Location source, Location target, IDistanceFunction distanceFunction, Double maxWaySpeed) {
		this(vehicle, null, vehicle, null, null, source, target, distanceFunction, maxWaySpeed);
	}

	private TimeCalculationInformationContainer(IVRPSimulationModelStructureElementWithStorageMovable vehicle,
			IDriver driver,
			IVRPSimulationModelStructureElementWithStorage vrpSimulationModelStructureElementWithStorage,
			StorableParameters storableParameters, Integer amount, Location source, Location target,
			IDistanceFunction distanceFunction, Double maxWaySpeed) {
		this.vehicle = vehicle;
		this.driver = driver;
		this.vrpSimulationModelStructureElementWithStorage = vrpSimulationModelStructureElementWithStorage;
		this.storableParameters = storableParameters;
		this.amount = amount;
		this.source = source;
		this.target = target;
		this.distanceFunction = distanceFunction;
		this.maxWaySpeed = maxWaySpeed;
	}

	public IVRPSimulationModelStructureElementWithStorageMovable getVehicle() {
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

	public Location getSource() {
		return source;
	}

	public Location getTarget() {
		return target;
	}

	public IDistanceFunction getDistanceFunction() {
		return distanceFunction;
	}

	public Double getMaxWaySpeed() {
		return maxWaySpeed;
	}

}
