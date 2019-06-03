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
/**
 * 
 */
package vrpsim.visualization.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.behaviour.BehaviourService;
import vrpsim.core.model.behaviour.activities.IActivity;
import vrpsim.core.model.behaviour.activities.impl.TransportActivity;
import vrpsim.core.model.behaviour.tour.Cost;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.behaviour.tour.TourContext;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.impl.OrderEvent;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElement;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementMovable;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.visualization.components.NetworkNodeVisualization;
import vrpsim.visualization.support.MathUtil;
import vrpsim.visualization.support.OriginLocation;
import vrpsim.visualization.support.TransformationManager;
import vrpsim.visualization.support.VRPVisualizationModel;
import vrpsim.visualization.view.util.ColorManager;

/**
 * @date 18.03.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class ModelViewController implements Observer {

	private static Logger logger = LoggerFactory.getLogger(ModelViewController.class);

	@FXML
	private Pane pane;

	private Set<ITour> tours = new HashSet<>();

	// Transformation
	private TransformationManager transformationManager;

	// Shape connecting to network element and vice versa.
	private HashMap<Node, OriginLocation> fxNodesOriginLocation = new HashMap<>();
	private HashMap<IVRPSimulationModelStructureElementMovable, IVRPSimulationModelNetworkElement> vehiclesToNetworkElement = new HashMap<>();
	private HashMap<IDriver, IVRPSimulationModelNetworkElement> driversToNetworkElement = new HashMap<>();
	// private HashMap<IOccasionalDriver, IVRPSimulationModelNetworkElement>
	// occasionalDriversToNetworkElement = new HashMap<>();
	private HashMap<IVRPSimulationModelNetworkElement, NetworkNodeVisualization> networkElementToVisualisation = new HashMap<>();
	private HashMap<IVRPSimulationModelStructureElement, NetworkNodeVisualization> structuralElementToVisualisation = new HashMap<>();

	// Visualization
	private VRPVisualizationModel visualisationModel;
	private InfoPaneController infoPaneController;

	// Current Simulation time
	private String currentSimulationTime = "0";
	private Cost currentSimulationCosts = new Cost();
	private BehaviourService behaviourService;

	private Pane orientation;

	private void manageSize() {
		this.pane.setMaxWidth(this.orientation.getWidth() * 0.75);
		this.pane.setPrefWidth(this.orientation.getWidth() * 0.75);
		this.pane.setMaxHeight(this.orientation.getHeight() * 0.70);
		this.pane.setPrefHeight(this.orientation.getHeight() * 0.70);
		if (this.visualisationModel != null) {
			drawSimulationModel();
		}
	}

	private void initTours(VRPSimulationModel simulationModel) {
		for (ITour tour : simulationModel.getBehaviourProvider().getBehaviourFromInitialBehaviourProvider().getTours()) {
			this.infoPaneController.updateTourVisualizationPopUp(tour, 0L);
			this.tours.add(tour);
		}
		this.behaviourService = simulationModel.getBehaviourService();
		this.currentSimulationCosts = this.behaviourService.getTourCosts();

	}

	public void init(final VRPSimulationModel simulationModel, InfoPaneController spc, Pane orientation) {

		this.orientation = orientation;
		this.orientation.widthProperty().addListener(e -> manageSize());
		this.orientation.heightProperty().addListener(e -> manageSize());
		manageSize();

		this.infoPaneController = spc;
		this.visualisationModel = new VRPVisualizationModel(simulationModel);
		this.transformationManager = new TransformationManager();
		this.vehiclesToNetworkElement = new HashMap<>();

		for (IVehicle vehicle : this.visualisationModel.getVehicles()) {
			this.vehiclesToNetworkElement.put(vehicle, vehicle.getVRPSimulationModelStructureElementParameters().getHome());
		}
		for (IDriver driver : this.visualisationModel.getDrivers()) {
			this.driversToNetworkElement.put(driver, driver.getVRPSimulationModelStructureElementParameters().getHome());
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
			infoPaneController.drawAll();
			drawSimulationModel();
		});

		this.pane.heightProperty().addListener(event -> {
			infoPaneController.drawAll();
			drawSimulationModel();
		});

		this.initTours(simulationModel);
		this.infoPaneController.drawAll();
		drawSimulationModel();
		logger.debug("ModelViewController initialized.");

	}

	private void drawSimulationModel() {

		double paneWidthX = this.pane.widthProperty().getValue().doubleValue();
		double paneHeightY = this.pane.heightProperty().getValue().doubleValue();

		if (this.pane.getChildren().isEmpty()) {

			List<Node> newNodes = new ArrayList<>();
			for (INode node : this.visualisationModel.getNetwork().keySet()) {
				NetworkNodeVisualization nnv = new NetworkNodeVisualization(node, this.visualisationModel.getNetwork().get(node), 0D,
						infoPaneController);
				this.networkElementToVisualisation.put(node, nnv);
				newNodes.add(nnv);
				this.fxNodesOriginLocation.put(nnv, new OriginLocation(node.getLocation().getX(), node.getLocation().getY()));
				for (IVRPSimulationModelStructureElement smse : this.visualisationModel.getNetwork().get(node)) {
					this.structuralElementToVisualisation.put(smse, nnv);
				}
			}

			for (IVRPSimulationModelStructureElementMovable vehicle : this.vehiclesToNetworkElement.keySet()) {
				this.networkElementToVisualisation.get(this.vehiclesToNetworkElement.get(vehicle))
						.addSimulationModelStructureElement(vehicle, 0D);
			}
			for (IDriver driver : this.driversToNetworkElement.keySet()) {
				this.networkElementToVisualisation.get(this.driversToNetworkElement.get(driver)).addSimulationModelStructureElement(driver,
						0D);
			}
			// for (IOccasionalDriver oDriver :
			// this.occasionalDriversToNetworkElement.keySet()) {
			// this.networkElementToVisualisation.get(this.occasionalDriversToNetworkElement.get(oDriver))
			// .addSimulationModelStructureElement(oDriver, new Clock.Time(0.0));
			// }

			this.pane.getChildren().addAll(newNodes);
		}

		// Locate all nodes regarding to current transformation.
		List<Node> nodesToRemove = new ArrayList<>();
		for (Node n : this.pane.getChildren()) {
			if (n instanceof Line || n instanceof Polygon || n instanceof Text) {
				nodesToRemove.add(n);
			} else {
				n.relocate(
						transformationManager.translateX(this.fxNodesOriginLocation.get(n).getX(), paneWidthX,
								this.visualisationModel.getMinXLon(), this.visualisationModel.getMaxXLon()),
						transformationManager.translateY(this.fxNodesOriginLocation.get(n).getY(), paneHeightY,
								this.visualisationModel.getMinYLat(), this.visualisationModel.getMaxYLat()));
			}
		}

		// Remove nodes to remove, lines and polygons.
		for (Node n : nodesToRemove) {
			this.pane.getChildren().remove(n);
		}

		// Draw lines dependent on the current context.
		drawLines();

		Text simCosts = new Text("Simulation costs: " + currentSimulationCosts);
		simCosts.relocate(10, 10);
		this.pane.getChildren().add(simCosts);

		Text simTime = new Text("Simulation time: " + currentSimulationTime);
		simTime.relocate(10, 25);
		this.pane.getChildren().add(simTime);
	}

	private void drawLine(List<NetworkNodeVisualization> nnvs, Color color) {
		for (int i = 0; i < nnvs.size() - 1; i++) {
			double startX = nnvs.get(i).getLayoutX() + (nnvs.get(i).getBoundsInLocal().getWidth() / 2);
			double startY = nnvs.get(i).getLayoutY() + (nnvs.get(i).getBoundsInLocal().getHeight() / 2);
			double endX = nnvs.get(i + 1).getLayoutX() + (nnvs.get(i + 1).getBoundsInLocal().getWidth() / 2);
			double endY = nnvs.get(i + 1).getLayoutY() + (nnvs.get(i + 1).getBoundsInLocal().getHeight() / 2);
			Line line = new Line(startX, startY, endX, endY);
			line.setStroke(color);
			this.pane.getChildren().add(line);
			Vector<Double> solutionVector = MathUtil.calculateArrowVector(startX, startY, endX, endY, 30);
			Polygon polygon = new Polygon(endX, endY, solutionVector.get(0), solutionVector.get(1), solutionVector.get(2),
					solutionVector.get(3));
			this.pane.getChildren().add(polygon);
		}
	}

	private Map<ITour, List<NetworkNodeVisualization>> generatePlannedWayData() {
		// Create planned ways from activity
		Map<ITour, List<NetworkNodeVisualization>> transportWays = new HashMap<>();
		for (ITour tour : this.tours) {
			List<NetworkNodeVisualization> transportWay = new ArrayList<>();
			NetworkNodeVisualization start = this.networkElementToVisualisation
					.get(tour.getTourContext().getVehicle().getVRPSimulationModelStructureElementParameters().getHome());
			transportWay.add(start);

			IActivity activity = tour.getStartActivity();
			while (activity != null) {
				if (activity instanceof TransportActivity) {
					TransportActivity transportActivity = (TransportActivity) activity;
					NetworkNodeVisualization nnv = this.networkElementToVisualisation.get(transportActivity.getTransportTarget());
					transportWay.add(nnv);
				}
				activity = activity.getSuccessor();
			}
			transportWays.put(tour, transportWay);
		}
		return transportWays;
	}

	private Map<ITour, List<NetworkNodeVisualization>> generateDrivenWayData() {

		// Create driven ways from activity
		Map<ITour, List<NetworkNodeVisualization>> drivenWays = new HashMap<>();

		for (ITour tour : this.tours) {
			TourContext context = tour.getTourContext();
			List<NetworkNodeVisualization> drivenWay = new ArrayList<>();
			for (int i = 0; i < context.getPlaceHistory().size() - 1; i++) {
				drivenWay.add(this.networkElementToVisualisation.get(context.getPlaceHistory().get(i)));
				drivenWay.add(this.networkElementToVisualisation.get(context.getPlaceHistory().get(i + 1)));
			}

			if (context.getPlaceHistory().size() == 1) {
				drivenWay.add(this.networkElementToVisualisation.get(context.getPlaceHistory().get(0)));
			}

			drivenWay.add(this.networkElementToVisualisation.get(context.getCurrentPlace()));
			drivenWays.put(tour, drivenWay);
		}
		return drivenWays;
	}

	private void drawLines() {

		if (!this.tours.isEmpty()) {

			Map<ITour, List<NetworkNodeVisualization>> plannedWays = generatePlannedWayData();
			// Draw planned ways from activity
			for (ITour tour : plannedWays.keySet()) {
				this.drawLine(plannedWays.get(tour), ColorManager.get().getColorFor(tour));

				if (tour.getTourContext().getCurrentActivity() instanceof TransportActivity
						&& tour.getTourContext().getCurrentActivity().isPrepared()) {

					List<NetworkNodeVisualization> nns = new ArrayList<>();
					nns.add(this.networkElementToVisualisation.get(tour.getTourContext().getCurrentPlace()));
					nns.add(this.networkElementToVisualisation
							.get(((TransportActivity) tour.getTourContext().getCurrentActivity()).getTransportTarget()));
					this.drawLine(nns, Color.RED);
				}

			}

			Map<ITour, List<NetworkNodeVisualization>> drivenWays = generateDrivenWayData();
			// Create driven ways from placeHistory
			for (ITour tour : drivenWays.keySet()) {
				this.drawLine(drivenWays.get(tour), Color.BLACK);
			}

		}

	}

	@Override
	public void update(Observable o, Object arg) {

		if (arg instanceof IEvent) {

			IEvent event = (IEvent) arg;
			logger.info("Visualisation update triggered from " + event.getOwner().getClass().getSimpleName() + ".");
			Double simulationTimeOfLastEventOccurence = event.getSimulationTimeOfOccurence();
			this.currentSimulationTime = simulationTimeOfLastEventOccurence.toString();
			this.currentSimulationCosts = this.behaviourService.getTourCosts();

			if (event instanceof OrderEvent) {
				this.infoPaneController.updateOrderPane(((OrderEvent) event).getOrder(), simulationTimeOfLastEventOccurence);
			}

			if (event.getOwner() instanceof ITour) {

				// get tour context and add if missing to considered contexts
				// generate tour popup and show or update tour and show.
				ITour tour = ((ITour) event.getOwner());
				if (!this.tours.contains(tour)) {
					this.tours.add(tour);
				}
				TourContext context = tour.getTourContext();
				this.infoPaneController.updateTourVisualizationPopUp(tour, simulationTimeOfLastEventOccurence);

				// 1. Remove the vehicle from network element visualization.
				// 2. Add vehicle to new network element.
				// Update lists.
				IVRPSimulationModelNetworkElement currentNetworkElementFromVehicleInTour = this.vehiclesToNetworkElement
						.get(context.getVehicle());
				this.networkElementToVisualisation.get(currentNetworkElementFromVehicleInTour)
						.removeSimulationModelStructureElement(context.getVehicle(), simulationTimeOfLastEventOccurence);
				IVRPSimulationModelNetworkElement newNetworkElementFromVehicleInTour = context.getVehicle().getCurrentPlace();
				this.vehiclesToNetworkElement.put(context.getVehicle(), newNetworkElementFromVehicleInTour);
				this.networkElementToVisualisation.get(newNetworkElementFromVehicleInTour)
						.addSimulationModelStructureElement(context.getVehicle(), simulationTimeOfLastEventOccurence);

				// 1. Remove the driver from network element visualization.
				// 2. Add driver to new network element.
				// Update lists.
				IVRPSimulationModelNetworkElement currentNetworkElementFromDriverInTour = this.driversToNetworkElement
						.get(context.getDriver());
				this.networkElementToVisualisation.get(currentNetworkElementFromDriverInTour)
						.removeSimulationModelStructureElement(context.getDriver(), simulationTimeOfLastEventOccurence);
				this.driversToNetworkElement.put(context.getDriver(), newNetworkElementFromVehicleInTour);
				this.networkElementToVisualisation.get(newNetworkElementFromVehicleInTour)
						.addSimulationModelStructureElement(context.getDriver(), simulationTimeOfLastEventOccurence);

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
							.onSimulationModelStructureElementChanged(simulationModelStructureElement, simulationTimeOfLastEventOccurence);
				}
			}

		}

		this.infoPaneController.drawAll();
		drawSimulationModel();
	}
}
