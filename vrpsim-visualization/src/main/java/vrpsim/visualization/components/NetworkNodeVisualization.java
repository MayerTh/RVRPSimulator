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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElement;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.depot.IDepot;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.visualization.view.InfoPaneController;

public class NetworkNodeVisualization extends Parent {

	private final int placingDepot = 1;
	private final String iconDepot = "depot2.png";
	private final int placingCustomer = 2;
	private final String iconCustomer = "customer2.png";
	private final int placingDynamicCustomer = 3;
	private final String iconDynamicCustomer = "dynamicCustomer2.png";
	private final int placingVehicle = 4;
	private final String iconVehicle = "vehicle2.png";
	private final int placingDriver = 0;
	private final String iconDriver = "driver2.png";

	private final Color circleColorHighlighted = Color.RED;
	private final Color lineColor = Color.SILVER;
	private final Color middlePointColor = Color.GREY;

	private final double iconRadius = 11;
	private final double radius = 30;
	private final double numberToPlace = 5;
	private final double delta = 8;

	private final IVRPSimulationModelNetworkElement networkElement;
	private final InfoPaneController spc;

	private double angle;
	private HashSet<IVRPSimulationModelStructureElement> structuralElements = new HashSet<>();

	public NetworkNodeVisualization(IVRPSimulationModelNetworkElement networkElement,
			HashSet<IVRPSimulationModelStructureElement> structuralElements, Double simulationTimeOfLastEventOccurence,
			InfoPaneController spc) {
		this.networkElement = networkElement;
		// this.popup = new NetworkNodeVisualizationPopup(networkElement);
		this.structuralElements = structuralElements;
		this.spc = spc;
		angle = 2 * Math.PI / numberToPlace;
		update(simulationTimeOfLastEventOccurence);
	}

	private void update(Double simulationTimeOfLastEventOccurence) {

		this.getChildren().clear();
		List<IVRPSimulationModelStructureElement> depots = new ArrayList<>();
		List<IVRPSimulationModelStructureElement> customers = new ArrayList<>();
		List<IVRPSimulationModelStructureElement> vehicles = new ArrayList<>();
		List<IVRPSimulationModelStructureElement> dynamicCustomers = new ArrayList<>();
		List<IVRPSimulationModelStructureElement> drivers = new ArrayList<>();

		for (IVRPSimulationModelStructureElement element : structuralElements) {
			if (element instanceof IDepot) {
				depots.add(element);
			}
			if (element instanceof ICustomer) {
				ICustomer customer = (ICustomer) element;
				if (customer.isHasDynamicEvents()) {
					dynamicCustomers.add(element);
				} else {
					customers.add(element);
				}
			}
			if (element instanceof IVehicle) {
				vehicles.add(element);
			}
			if (element instanceof IDriver) {
				drivers.add(element);
			}
		}

		// Draw middle point.
		Circle middle = new Circle(4);
		middle.setCenterX(radius);
		middle.setCenterY(radius);
		middle.setFill(middlePointColor);
		middle.setSmooth(true);
		Text text = new Text(this.networkElement.getVRPSimulationModelElementParameters().getId());
		text.relocate(radius, radius);
		this.getChildren().add(text);

		this.setOnMouseEntered(event -> {
			middle.setStroke(circleColorHighlighted);
		});
		this.setOnMouseExited(event -> {
			middle.setStroke(Color.TRANSPARENT);
		});

		this.setOnMousePressed((EventHandler<? super MouseEvent>) event -> {

			try {
				spc.updateNetworkNodeVisualizationPopup(this.networkElement,
						new ArrayList<IVRPSimulationModelStructureElement>(this.structuralElements), simulationTimeOfLastEventOccurence,
						false);
			} catch (Exception e) {
				e.printStackTrace();
			}

		});

		this.getChildren().add(middle);

		if (!depots.isEmpty()) {
			draw(iconDepot, placingDepot, depots, false, simulationTimeOfLastEventOccurence);
		} else {
			draw(null, placingDepot, null, true, simulationTimeOfLastEventOccurence);
		}

		if (!customers.isEmpty()) {
			draw(iconCustomer, placingCustomer, customers, false, simulationTimeOfLastEventOccurence);
		} else {
			draw(null, placingCustomer, null, true, simulationTimeOfLastEventOccurence);
		}

		if (!vehicles.isEmpty()) {
			draw(iconVehicle, placingVehicle, vehicles, false, simulationTimeOfLastEventOccurence);
		} else {
			draw(null, placingVehicle, null, true, simulationTimeOfLastEventOccurence);
		}

		if (!dynamicCustomers.isEmpty()) {
			draw(iconDynamicCustomer, placingDynamicCustomer, dynamicCustomers, false, simulationTimeOfLastEventOccurence);
		} else {
			draw(null, placingDynamicCustomer, null, true, simulationTimeOfLastEventOccurence);
		}

		if (!drivers.isEmpty()) {
			draw(iconDriver, placingDriver, drivers, false, simulationTimeOfLastEventOccurence);
		} else {
			draw(null, placingDriver, null, true, simulationTimeOfLastEventOccurence);
		}

	}

	private void draw(String png, int placing, List<IVRPSimulationModelStructureElement> elements, boolean dummy,
			Double simulationTimeOfLastEventOccurence) {
		double x = (Math.cos(placing * angle) * (radius - delta)) + radius;
		double y = (Math.sin(placing * angle) * (radius - delta)) + radius;

//		Circle circle = new Circle(this.iconRadius);
		Rectangle circle = new Rectangle(this.iconRadius*2, this.iconRadius*2);
		circle.setFill(Color.TRANSPARENT);
		

		if (!dummy) {
			Line line = new Line(radius, radius, x, y);
			line.setSmooth(true);
			line.setStroke(lineColor);
			line.setFill(lineColor);
			this.getChildren().add(line);

			circle.setOnMouseEntered(event -> {
				circle.setStroke(circleColorHighlighted);
			});
			circle.setOnMouseExited(event -> {
				circle.setStroke(Color.TRANSPARENT);
			});

			circle.setOnMousePressed((EventHandler<? super MouseEvent>) event -> {
				spc.updateNetworkNodeVisualizationPopup(this.networkElement,
						new ArrayList<IVRPSimulationModelStructureElement>(this.structuralElements), simulationTimeOfLastEventOccurence,
						false);
				event.consume();
			});

			circle.setFill(new ImagePattern(new Image(png)));
		}
		circle.setSmooth(true);
		circle.setX(x - this.iconRadius);
		circle.setY(y - this.iconRadius);
//		circle.setCenterX(x);
//		circle.setCenterY(y);
		circle.setStroke(Color.TRANSPARENT);
		this.getChildren().add(circle);
	}

	public void removeSimulationModelStructureElement(IVRPSimulationModelStructureElement element,
			Double simulationTimeOfLastEventOccurence) {
		if (this.structuralElements.contains(element)) {
			this.structuralElements.remove(element);
			this.update(simulationTimeOfLastEventOccurence);
			this.spc.updateNetworkNodeVisualizationPopup(networkElement, new ArrayList<>(this.structuralElements),
					simulationTimeOfLastEventOccurence, true);
		}
	}

	public void addSimulationModelStructureElement(IVRPSimulationModelStructureElement element, Double simulationTimeOfLastEventOccurence) {
		this.structuralElements.add(element);
		this.update(simulationTimeOfLastEventOccurence);
		this.spc.updateNetworkNodeVisualizationPopup(networkElement, new ArrayList<>(this.structuralElements),
				simulationTimeOfLastEventOccurence, true);
	}

	public void onSimulationModelStructureElementChanged(IVRPSimulationModelStructureElement element,
			Double simulationTimeOfLastEventOccurence) {
		this.update(simulationTimeOfLastEventOccurence);
		this.spc.updateNetworkNodeVisualizationPopup(networkElement, new ArrayList<>(this.structuralElements),
				simulationTimeOfLastEventOccurence, true);
	}
}
