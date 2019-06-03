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
package vrpsim.visualization.components;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.storage.impl.CanStoreType;
import vrpsim.visualization.view.InfoPaneController;

public class TourVisualizationPopup extends VisualizationPopup {
	
	public TourVisualizationPopup(ITour tour, Double simulationTimeOfLastEventOccurence, double width, double hight, InfoPaneController controller, double closeImagaeBias) {
		super(controller, closeImagaeBias);
		String title = "Tour from vehicle "
				+ tour.getTourContext().getVehicle().getVRPSimulationModelElementParameters().getId();
		buildPopup(title, width, hight, true, false);
		update(tour, simulationTimeOfLastEventOccurence);
	}

	public void update(ITour tour, Double simulationTimeOfLastEventOccurence) {
		this.dataBox.getChildren().clear();
		String title = "Tour from vehicle "
				+ tour.getTourContext().getVehicle().getVRPSimulationModelElementParameters().getId();
		changeTitel(title);

		// Simulation time
		Text simulationTime = new Text("Simulation time for tour");
		simulationTime.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		VBox.setMargin(simulationTime, new Insets(4, 0, 0, 2));
		this.dataBox.getChildren().add(simulationTime);

		Text timeValue = new Text(simulationTimeOfLastEventOccurence.toString());
		VBox.setMargin(timeValue, new Insets(0, 0, 0, 12));
		this.dataBox.getChildren().add(timeValue);

		// Tour costs
		Text tourCosts = new Text("Current tour costs");
		tourCosts.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		VBox.setMargin(tourCosts, new Insets(4, 0, 0, 2));
		this.dataBox.getChildren().add(tourCosts);

		Text costValue = new Text(String.valueOf(tour.getTourContext().getCurrentTourCost().toString()));
		VBox.setMargin(costValue, new Insets(0, 0, 0, 12));
		this.dataBox.getChildren().add(costValue);

		// vehicle
		Text vehicle = new Text(
				tour.getTourContext().getVehicle().getVRPSimulationModelElementParameters().getId());
		vehicle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		VBox.setMargin(vehicle, new Insets(4, 0, 0, 2));
		this.dataBox.getChildren().add(vehicle);

		if (tour.getTourContext().getVehicle() instanceof IVRPSimulationModelStructureElementWithStorage) {
			IVRPSimulationModelStructureElementWithStorage sm = (IVRPSimulationModelStructureElementWithStorage) tour.getTourContext().getVehicle();
			for (CanStoreType type : sm.getCanStoreManager().getAllCanStoreTypes()) {
				Text text = new Text(type.getId() + " " + sm.getCanStoreManager().getCurrentCapacity(type) + "/"
						+ (sm.getCanStoreManager().getCurrentCapacity(type) + sm.getCanStoreManager().getFreeCapacity(type)));
				VBox.setMargin(text, new Insets(0, 0, 0, 12));
				this.dataBox.getChildren().add(text);
			}
		}

		// location
		Text location = new Text("Tour location: "
				+ tour.getTourContext().getCurrentPlace().getVRPSimulationModelElementParameters().getId());
		location.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		VBox.setMargin(location, new Insets(4, 0, 0, 2));
		this.dataBox.getChildren().add(location);

		if (tour.getTourContext().getVehicle().getCurrentPlace() instanceof INode) {
			Text locationValue = new Text(((INode) tour.getTourContext().getVehicle().getCurrentPlace()).getLocation().toString());
			VBox.setMargin(locationValue, new Insets(0, 0, 0, 12));
			this.dataBox.getChildren().add(locationValue);
		}

		// driver
		Text driver = new Text("Current driver");
		driver.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		VBox.setMargin(driver, new Insets(4, 0, 0, 2));
		this.dataBox.getChildren().add(driver);

		Text driverValue = new Text(
				tour.getTourContext().getDriver().getVRPSimulationModelElementParameters().getId());
		VBox.setMargin(driverValue, new Insets(0, 0, 0, 12));
		this.dataBox.getChildren().add(driverValue);

		// driver
		Text history = new Text("Tour History");
		history.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		VBox.setMargin(history, new Insets(4, 0, 0, 2));
		this.dataBox.getChildren().add(history);

		for (IVRPSimulationModelNetworkElement place : tour.getTourContext().getPlaceHistory()) {
			Text placeValue = new Text(place.getVRPSimulationModelElementParameters().getId());
			VBox.setMargin(placeValue, new Insets(0, 0, 0, 12));
			this.dataBox.getChildren().add(placeValue);
		}

	}

}
