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
/**
 * 
 */
package vrpsim.visualization.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.behaviour.ITour;
import vrpsim.core.model.behaviour.TourContext;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.OrderEvent;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElement;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.simulator.Clock;
import vrpsim.core.simulator.ITime;
import vrpsim.visualization.components.NetworkNodeVisualization;
import vrpsim.visualization.components.OrderBoardVisualizationPopup;
import vrpsim.visualization.components.TourVisualizationPopup;
import vrpsim.visualization.support.MathUtil;
import vrpsim.visualization.support.OriginLocation;
import vrpsim.visualization.support.TransformationManager;
import vrpsim.visualization.support.VRPVisualizationModel;

/**
 * @date 18.03.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class ModelViewController implements Observer {

	private static Logger logger = LoggerFactory.getLogger(ModelViewController.class);

	@FXML
	private Pane pane;

	private OrderBoardVisualizationPopup orderBoardVisualizationPopup;

	private HashMap<TourContext, TourVisualizationPopup> tourContexts = new HashMap<>();

	// Transformation
	private TransformationManager transformationManager;

	// Shape connecting to network element and vice versa.
	private HashMap<Node, OriginLocation> fxNodesOriginLocation = new HashMap<>();
	private HashMap<IVehicle, IVRPSimulationModelNetworkElement> vehiclesToNetworkElement = new HashMap<>();
	private HashMap<IVRPSimulationModelNetworkElement, NetworkNodeVisualization> networkElementToVisualisation = new HashMap<>();
	private HashMap<IVRPSimulationModelStructureElement, NetworkNodeVisualization> structuralElementToVisualisation = new HashMap<>();

	// Visualization
	private VRPVisualizationModel visualisationModel;

	public void init(final VRPSimulationModel simulationModel) {

		this.visualisationModel = new VRPVisualizationModel(simulationModel);
		this.transformationManager = new TransformationManager();
		this.vehiclesToNetworkElement = new HashMap<>();
		for (IVehicle vehicle : this.visualisationModel.getVehicles()) {
			this.vehiclesToNetworkElement.put(vehicle,
					vehicle.getVRPSimulationModelStructureElementParameters().getHome());
		}

		this.pane.setOnScroll((event) -> {
			transformationManager.mouseWheelMoved(event);
			drawSimulationModel();
		});

		this.pane.setOnMousePressed((event) -> {
			transformationManager.mousePressed(event);
			drawSimulationModel();
		});

		this.pane.setOnMouseReleased((event) -> {
			transformationManager.mouseReleased(event);
			drawSimulationModel();
		});

		this.pane.setOnMouseDragged((event) -> {
			transformationManager.mouseDragged(event);
			drawSimulationModel();
		});

		this.pane.widthProperty().addListener(event -> {
			drawSimulationModel();
		});

		this.pane.heightProperty().addListener(event -> {
			drawSimulationModel();
		});

		drawSimulationModel();
		logger.debug("ModelViewController initialized.");

	}

	private void drawSimulationModel() {

		double paneWidthX = this.pane.widthProperty().getValue().doubleValue();
		double paneHeightY = this.pane.heightProperty().getValue().doubleValue();

		if (this.pane.getChildren().isEmpty()) {
			List<Node> newNodes = new ArrayList<>();
			for (INode node : this.visualisationModel.getNetwork().keySet()) {

				NetworkNodeVisualization nnv = new NetworkNodeVisualization(node,
						this.visualisationModel.getNetwork().get(node), new Clock.Time(0.0));
				this.networkElementToVisualisation.put(node, nnv);
				newNodes.add(nnv);
				this.fxNodesOriginLocation.put(nnv,
						new OriginLocation(node.getLocation().getX(), node.getLocation().getY()));
				for (IVRPSimulationModelStructureElement smse : this.visualisationModel.getNetwork().get(node)) {
					this.structuralElementToVisualisation.put(smse, nnv);
				}
			}

			for (IVehicle vehicle : this.vehiclesToNetworkElement.keySet()) {
				this.networkElementToVisualisation.get(this.vehiclesToNetworkElement.get(vehicle))
						.addSimulationModelStructureElement(vehicle, new Clock.Time(0.0));
			}

			this.pane.getChildren().addAll(newNodes);
		}

		// Locate all nodes regarding to current transformation.
		List<Node> nodesToRemove = new ArrayList<>();
		for (Node n : this.pane.getChildren()) {
			if (n instanceof Line || n instanceof Polygon) {
				nodesToRemove.add(n);
			} else {
				n.relocate(
						transformationManager.translateX(this.fxNodesOriginLocation.get(n).getX(), paneWidthX,
								this.visualisationModel.getMinXLon(), this.visualisationModel.getMaxXLon()),
						transformationManager.translateY(this.fxNodesOriginLocation.get(n).getY(), paneHeightY,
								this.visualisationModel.getMinYLat(), this.visualisationModel.getMaxYLat()));
			}
		}

		// Remove nodes to remove.
		for (Node n : nodesToRemove) {
			this.pane.getChildren().remove(n);
		}

		// Draw lines dependent on the current context.
		if (!this.tourContexts.isEmpty()) {
			for (TourContext context : this.tourContexts.keySet()) {
				int lineCounter = 0;
				for (int i = context.getPlaceHistory().size()-1; i >= 0; i--) {
					NetworkNodeVisualization startNNV = this.networkElementToVisualisation
							.get(context.getPlaceHistory().get(i));
					double startX = startNNV.getLayoutX() + (startNNV.getBoundsInLocal().getWidth() / 2);
					double startY = startNNV.getLayoutY() + (startNNV.getBoundsInLocal().getHeight() / 2);

					double endX = 0;
					double endY = 0;
					NetworkNodeVisualization endNNV = null;
					if (i < context.getPlaceHistory().size()-1) {
						endNNV = this.networkElementToVisualisation.get(context.getPlaceHistory().get(i + 1));
					} else {
						endNNV = this.networkElementToVisualisation
								.get(this.vehiclesToNetworkElement.get(context.getCurrentVehicle()));
					}
					endX = endNNV.getLayoutX() + (endNNV.getBoundsInLocal().getWidth() / 2);
					endY = endNNV.getLayoutY() + (endNNV.getBoundsInLocal().getHeight() / 2);

					Line line = new Line(startX, startY, endX, endY);
					line.setStroke(lineCounter < 3 ? Color.GRAY : Color.LIGHTGRAY);
					this.pane.getChildren().add(line);

					Vector<Double> solutionVector = MathUtil.calculateArrowVector(startX, startY, endX, endY, 30);
					Polygon polygon = new Polygon(endX, endY, solutionVector.get(0), solutionVector.get(1), solutionVector.get(2), solutionVector.get(3));
					this.pane.getChildren().add(polygon);
					
					lineCounter++;
					if(lineCounter >= 7) break;
				}
			}
		}

	}

	@Override
	public void update(Observable o, Object arg) {

		if (arg instanceof IEvent) {

			IEvent event = (IEvent) arg;
			logger.info("Visualisation update triggered from " + event.getOwner().getClass().getSimpleName() + ".");
			ITime simulationTimeOfLastEventOccurence = event.getSimulationTimeOfOccurence();

			if (event instanceof OrderEvent) {
				if (this.orderBoardVisualizationPopup == null) {
					this.orderBoardVisualizationPopup = new OrderBoardVisualizationPopup();
				}
				this.orderBoardVisualizationPopup.addOrder(((OrderEvent) event).getOrder());

				if (!this.orderBoardVisualizationPopup.isShowing()) {
					this.orderBoardVisualizationPopup.show(this.pane,
							this.pane.localToScreen(this.pane.getBoundsInLocal()).getMinX(),
							this.pane.localToScreen(this.pane.getBoundsInLocal()).getMinY());
				}
			}

			if (event.getOwner() instanceof ITour) {

				// get tour context and add if missing to considered contexts
				// generate tour popup and show or update tour and show.
				TourContext context = ((ITour) event.getOwner()).getTourContext();
				if (!this.tourContexts.containsKey(context)) {
					TourVisualizationPopup tourPopup;
					try {
						tourPopup = new TourVisualizationPopup((ITour) event.getOwner(),
								event.getSimulationTimeOfOccurence());
						tourPopup.show(this.pane, this.pane.localToScreen(this.pane.getBoundsInLocal()).getMinX(),
								this.pane.localToScreen(this.pane.getBoundsInLocal()).getMinY());
						this.tourContexts.put(context, tourPopup);
					} catch (VRPArithmeticException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						this.tourContexts.get(context).update(((ITour) event.getOwner()),
								event.getSimulationTimeOfOccurence());
					} catch (VRPArithmeticException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (!this.tourContexts.get(context).isShowing()) {
						this.tourContexts.get(context).show(this.pane,
								this.pane.localToScreen(this.pane.getBoundsInLocal()).getMinX(),
								this.pane.localToScreen(this.pane.getBoundsInLocal()).getMinY());
					}
				}

				// 1. Remove the vehicle from network element visualization.
				// 2. Add vehicle to new network element.
				// Update lists.
				IVRPSimulationModelNetworkElement currentNetworkElementFromVehicleInTour = this.vehiclesToNetworkElement
						.get(context.getCurrentVehicle());
				this.networkElementToVisualisation.get(currentNetworkElementFromVehicleInTour)
						.removeSimulationModelStructureElement(context.getCurrentVehicle(),
								simulationTimeOfLastEventOccurence);
				IVRPSimulationModelNetworkElement newNetworkElementFromVehicleInTour = context.getCurrentPlace();
				this.vehiclesToNetworkElement.put(context.getCurrentVehicle(), newNetworkElementFromVehicleInTour);
				this.networkElementToVisualisation.get(newNetworkElementFromVehicleInTour)
						.addSimulationModelStructureElement(context.getCurrentVehicle(),
								simulationTimeOfLastEventOccurence);

				// Update the elements changed during the tour execution (e.g.
				// exchange).
				for (IVRPSimulationModelElement simulationModelElement : context.consumeElementsUpdated()) {
					if (simulationModelElement instanceof IVRPSimulationModelStructureElement) {
						IVRPSimulationModelStructureElement simulationModelStructureElement = (IVRPSimulationModelStructureElement) simulationModelElement;
						if (this.structuralElementToVisualisation.containsKey(simulationModelStructureElement)) {
							this.structuralElementToVisualisation.get(simulationModelStructureElement)
									.onSimulationModelStructureElementChanged(simulationModelStructureElement,
											simulationTimeOfLastEventOccurence);
						}
					}
				}

			} else if (event.getOwner() instanceof IVRPSimulationModelStructureElement) {

				// Update the elements changed (e.g. consumption).
				IVRPSimulationModelStructureElement simulationModelStructureElement = (IVRPSimulationModelStructureElement) event
						.getOwner();
				if (this.structuralElementToVisualisation.containsKey(simulationModelStructureElement)) {
					this.structuralElementToVisualisation.get(simulationModelStructureElement)
							.onSimulationModelStructureElementChanged(simulationModelStructureElement,
									simulationTimeOfLastEventOccurence);
				}
			}

		}
		drawSimulationModel();
	}
}
