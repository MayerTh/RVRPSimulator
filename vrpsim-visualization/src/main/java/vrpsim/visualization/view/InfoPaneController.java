package vrpsim.visualization.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ObservableValue;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.events.strategies.impl.util.Order;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElement;
import vrpsim.visualization.components.NetworkNodeVisualizationPopup;
import vrpsim.visualization.components.OrderBoardVisualizationPopup;
import vrpsim.visualization.components.TourVisualizationPopup;
import vrpsim.visualization.components.VisualizationPopup;

public class InfoPaneController {

	// private static Logger logger =
	// LoggerFactory.getLogger(InfoPaneController.class);

	private double bias = 15d;
	private double hightNetworkPopup = 120d;
	private double hightTourPopup = 250d;

	private VBox infoBox;
	private AnchorPane orderPane;
	private Map<IVRPSimulationModelNetworkElement, NetworkNodeVisualizationPopup> networkElementToPopup;
	private Map<ITour, TourVisualizationPopup> tourToPopup;
	private OrderBoardVisualizationPopup orderPopup;
	
	public InfoPaneController(VBox infoBox, AnchorPane orderPane) {
		this.infoBox = infoBox;
		this.orderPane = orderPane;
		this.networkElementToPopup = new HashMap<>();
		this.tourToPopup = new HashMap<>();
		this.orderPopup = new OrderBoardVisualizationPopup(this, bias, orderPane);
		this.orderPane.getChildren().add(this.orderPopup);
	}

	public void updateTourVisualizationPopUp(ITour tour, double simulationTimeOfLastEventOccurence) {
		if (this.tourToPopup.containsKey(tour)) {
			this.tourToPopup.get(tour).update(tour, simulationTimeOfLastEventOccurence);
		} else {
			TourVisualizationPopup popup = new TourVisualizationPopup(tour, simulationTimeOfLastEventOccurence,
					this.orderPane.getWidth() - bias, hightTourPopup, this, bias);
			popup.setAddedAt(System.currentTimeMillis());
			popup.update(tour, simulationTimeOfLastEventOccurence);
			this.tourToPopup.put(tour, popup);
		}
		drawAll();
	}

	public void updateNetworkNodeVisualizationPopup(IVRPSimulationModelNetworkElement networkElement,
			ArrayList<IVRPSimulationModelStructureElement> elements, Double simulationTimeOfLastEventOccurence, boolean onlyUpdate) {
		if (this.networkElementToPopup.containsKey(networkElement)) {
			this.networkElementToPopup.get(networkElement).update(elements, simulationTimeOfLastEventOccurence);
		} else {
			if (!onlyUpdate) {
				NetworkNodeVisualizationPopup popup = new NetworkNodeVisualizationPopup(networkElement, infoBox.getWidth() - bias,
						hightNetworkPopup, this, bias);
				popup.setAddedAt(System.currentTimeMillis());
				popup.update(elements, simulationTimeOfLastEventOccurence);
				this.networkElementToPopup.put(networkElement, popup);
			}
		}
		drawAll();
	}

	public void drawAll() {
		this.infoBox.getChildren().clear();

		List<TourVisualizationPopup> tourPopups = new ArrayList<>(this.tourToPopup.values());
		Collections.sort(tourPopups);
		for (TourVisualizationPopup popup : tourPopups) {
			this.infoBox.getChildren().add(popup);
		}

		List<NetworkNodeVisualizationPopup> networkNodePopup = new ArrayList<>(this.networkElementToPopup.values());
		Collections.sort(networkNodePopup);
		for (NetworkNodeVisualizationPopup popup : networkNodePopup) {
			this.infoBox.getChildren().add(popup);
		}

	}

	public void remove(VisualizationPopup visualizationPopup) {
		if (visualizationPopup instanceof NetworkNodeVisualizationPopup) {
			NetworkNodeVisualizationPopup networkPopup = (NetworkNodeVisualizationPopup) visualizationPopup;
			this.networkElementToPopup.remove(networkPopup.getNetworkElement());
			drawAll();
		}
	}

	public ObservableValue<? extends Number> getOrientationWidthProperty(VisualizationPopup popup) {
		if (!(popup instanceof OrderBoardVisualizationPopup)) {
			return this.infoBox.prefWidthProperty();
		} else {
			return this.orderPane.prefWidthProperty();
		}
	}

	public void updateOrderPane(Order order, double simulationTimeOfLastEventOccurence) {
		this.orderPopup.addOrder(order, simulationTimeOfLastEventOccurence);
	}

}
