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
package vrpsim.visualization.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.Location;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElement;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.occasionaldriver.IOccasionalDriver;
import vrpsim.core.model.structure.vehicle.IVehicle;

public class VRPVisualizationModel {

	// Simulation Model
	private final VRPSimulationModel simulationModel;

	// To draw.
	private HashMap<INode, HashSet<IVRPSimulationModelStructureElement>> network;
	
	private HashSet<IVehicle> vehicles = new HashSet<>();
	private HashSet<IDriver> drivers = new HashSet<>();
	private HashSet<IOccasionalDriver> occassionalDrivers = new HashSet<>();

	// Max/Min Lat/Lon
	private double minXLon = Double.MAX_VALUE;
	private double maxXLon = Double.MIN_VALUE;
	private double minYLat = Double.MAX_VALUE;
	private double maxYLat = Double.MIN_VALUE;

	public VRPVisualizationModel(final VRPSimulationModel simulationModel) {
		this.simulationModel = simulationModel;
		this.processSimulationModel();
	}

	public double getMinXLon() {
		return minXLon;
	}

	public double getMaxXLon() {
		return maxXLon;
	}

	public double getMinYLat() {
		return minYLat;
	}

	public double getMaxYLat() {
		return maxYLat;
	}

	public HashSet<IVehicle> getVehicles() {
		return vehicles;
	}

	public HashSet<IDriver> getDrivers() {
		return drivers;
	}
	
	public HashSet<IOccasionalDriver> getOccassionalDrivers() {
		return occassionalDrivers;
	}
	
	public HashMap<INode, HashSet<IVRPSimulationModelStructureElement>> getNetwork() {
		return network;
	}

	private void processSimulationModel() {

		this.network = new HashMap<>();

		if (this.simulationModel != null) {

			List<IVRPSimulationModelStructureElement> structureElements = new ArrayList<>();
			structureElements.addAll(this.simulationModel.getStructure().getCustomers());
			structureElements.addAll(this.simulationModel.getStructure().getDepots());
			structureElements.addAll(this.simulationModel.getStructure().getVehicles());
			structureElements.addAll(this.simulationModel.getStructure().getDrivers());
			structureElements.addAll(this.simulationModel.getStructure().getOccasionalDrivers());

			for (INode node : this.simulationModel.getNetwork().getNodes()) {
				Location location = node.getLocation();
				if (minXLon > location.getX()) {
					minXLon = location.getX();
				}
				if (maxXLon < location.getX()) {
					maxXLon = location.getX();
				}
				if (minYLat > location.getY()) {
					minYLat = location.getY();
				}
				if (maxYLat < location.getY()) {
					maxYLat = location.getY();
				}

				HashSet<IVRPSimulationModelStructureElement> structure = new HashSet<>();
				for (IVRPSimulationModelStructureElement se : structureElements) {
					if (se.getVRPSimulationModelStructureElementParameters().getHome().equals(node)
							&& !(se instanceof IVehicle) && !(se instanceof IDriver) && !(se instanceof IOccasionalDriver)) {
						structure.add(se);
					} else if (se instanceof IVehicle) {
						vehicles.add((IVehicle)se);
					} else if (se instanceof IDriver) {
						drivers.add((IDriver)se);
					} else if (se instanceof IOccasionalDriver) {
						occassionalDrivers.add((IOccasionalDriver)se);
					}
				}
				this.network.put(node, structure);
			}
		}
	}

}
