/**
 * Copyright © 2016 Thomas Mayer (thomas.mayer@unibw.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vrpsim.core.model.behaviour.tour;

import java.util.ArrayList;
import java.util.List;

import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.behaviour.activities.IActivity;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorageMovable;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.simulator.ITime;

/**
 * @date 23.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class TourContext {

	private final ITime activityTimeTillStart;

	private final IVRPSimulationModelStructureElementWithStorageMovable vehicle;
	private final IDriver driver;
	private List<IVRPSimulationModelElement> elementsUpdated;
	
	private List<IVRPSimulationModelNetworkElement> placeHistory;

	private IActivity currentActivity;
	
	public ITime getActivityTimeTillStart() {
		return activityTimeTillStart;
	}

	public TourContext(ITime activityTimeTillStart, IVRPSimulationModelStructureElementWithStorageMovable vehicle, IDriver driver) {
		super();
		this.activityTimeTillStart = activityTimeTillStart;
		this.vehicle = vehicle;
		this.driver = driver;
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
		return this.vehicle.getCurrentPlace();
	}

	public IActivity getCurrentActivity() {
		return currentActivity;
	}
	
	public boolean isTourActive() {
		return this.currentActivity.getSuccessor() != null;
	}

	public void setCurrentActivity(IActivity currentActivity) {
		this.currentActivity = currentActivity;
	}

	public void setCurrentPlace(IVRPSimulationModelNetworkElement currentPlace) {
		this.placeHistory.add(this.vehicle.getCurrentPlace());
		this.vehicle.setCurrentPlace(currentPlace);
	}

	public IVRPSimulationModelStructureElementWithStorageMovable getVehicle() {
		return vehicle;
	}

	public IDriver getDriver() {
		return driver;
	}

	/**
	 * Returns the elements updated and deletes them.
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
