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
package vrpsim.core.model.behaviour;

import java.util.ArrayList;
import java.util.List;

import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.simulator.ITime;

/**
 * @date 23.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class TourContext {

	private final ITime activityStart;

	private IVehicle currentVehicle;
	private IDriver currentDriver;
	private IVRPSimulationModelNetworkElement currentPlace;
	private List<IVRPSimulationModelElement> elementsUpdated;

	private List<IVRPSimulationModelNetworkElement> placeHistory;

	public ITime getActivityStart() {
		return activityStart;
	}

	public TourContext(ITime activityStart, IVehicle vehicle, IDriver driver) {
		super();
		this.activityStart = activityStart;
		this.currentVehicle = vehicle;
		this.currentDriver = driver;
		this.currentPlace = this.currentVehicle.getVRPSimulationModelStructureElementParameters().getHome();
		this.placeHistory = new ArrayList<>();
		this.elementsUpdated = new ArrayList<>();
	}

	public void addElementsUpdated(IVRPSimulationModelElement element) {
		this.elementsUpdated.add(element);
	}

	@Deprecated
	public void clearElementsUpdated() {
		this.elementsUpdated.clear();
	}

	public IVRPSimulationModelNetworkElement getCurrentPlace() {
		return currentPlace;
	}

	public void setCurrentPlace(IVRPSimulationModelNetworkElement currentPlace) {
		this.placeHistory.add(this.currentPlace);
		this.currentPlace = currentPlace;
	}

	public IVehicle getCurrentVehicle() {
		return currentVehicle;
	}

	public void setCurrentVehicle(IVehicle currentVehicle) {
		this.currentVehicle = currentVehicle;
	}

	public IDriver getCurrentDriver() {
		return currentDriver;
	}

	public void setCurrentDriver(IDriver currentDriver) {
		this.currentDriver = currentDriver;
	}

	/**
	 * Returns the elements updated and delets them.
	 * 
	 * @return
	 */
	public List<IVRPSimulationModelElement> consumeElementsUpdated() {
		List<IVRPSimulationModelElement> result = new ArrayList<>(this.elementsUpdated);
		elementsUpdated.clear();
		return result;
	}

	public List<IVRPSimulationModelNetworkElement> getPlaceHistory() {
		return placeHistory;
	}

}
