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

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElement;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.util.storage.CanStoreType;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.model.util.uncertainty.UncertainParameterContainer;
import vrpsim.core.simulator.ITime;
import vrpsim.visualization.util.AlphanumericSorting;

public class NetworkNodeVisualizationPopup extends VisualizationPopup {

	// private HashMap<String, HashMap<String, List<SimpleEntry<ITime,
	// Double>>>> chartData = new HashMap<>();

	public NetworkNodeVisualizationPopup(IVRPSimulationModelNetworkElement networkElement) {
		if (networkElement instanceof INode) {
			INode node = (INode) networkElement;
			String title = node.getVRPSimulationModelElementParameters().getId() + " at " + node.getLocation().toString();
			buildPopup(title, 410);
		}
	}

	public void update(List<IVRPSimulationModelStructureElement> elements, ITime simulationTimeOfLastEventOccurence) throws VRPArithmeticException {
		this.dataBox.getChildren().clear();

		Collections.sort(elements, new AlphanumericSorting());

		for (IVRPSimulationModelStructureElement element : elements) {
			String id = element.getVRPSimulationModelElementParameters().getId();
			Text header = new Text(id);
			header.setFont(Font.font("Arial", FontWeight.BOLD, 12));
			VBox.setMargin(header, new Insets(4, 0, 0, 2));
			this.dataBox.getChildren().add(header);

			if (element instanceof IVRPSimulationModelStructureElementWithStorage) {
				IVRPSimulationModelStructureElementWithStorage sm = (IVRPSimulationModelStructureElementWithStorage) element;
				for (CanStoreType type : sm.getAllCanStoreTypes()) {

					String currentCapa = sm.getCurrentCapacity(type).getValue() >= Double.MAX_VALUE ? "~" : sm.getCurrentCapacity(type).getValue().toString();
					String maxCapa = sm.getCurrentCapacity(type).getValue() + sm.getFreeCapacity(type).getValue() >= Double.MAX_VALUE ? "~"
							: (sm.getCurrentCapacity(type).getValue() + sm.getFreeCapacity(type).getValue()) + "";

					Text text = new Text(type.getId() + " " + currentCapa + "/" + maxCapa);
					VBox.setMargin(text, new Insets(0, 0, 0, 12));
					this.dataBox.getChildren().add(text);
				}

				if (element instanceof ICustomer) {
					ICustomer customer = (ICustomer) element;
					int containerIndex = 1;
					for (UncertainParameterContainer container : customer.getUncertainParameters().getParameter()) {

						String str = MessageFormat.format("UContainer-{0}: start={1};number={2};type={6};edd={3};ldd={4};cyclic={5}", containerIndex++, container.getNewRealizationFromStartDistributionFunction(),
								container.getNewRealizationFromNumberDistributionFunction(), container.getNewRealizationFromEarliestDueDateDistributionFunction(), container.getNewRealizationFromLatestDueDateDistributionFunction(), container.isCyclic(),
								container.getStorableParameters().getStorableType().toString());

						Text text = new Text(str);
						VBox.setMargin(text, new Insets(0, 0, 0, 12));
						this.dataBox.getChildren().add(text);
					}
				}
			}
		}

		// // defining the axes
		// final NumberAxis xAxis = new NumberAxis();
		// final NumberAxis yAxis = new NumberAxis();
		// xAxis.setLabel("Number of Month");
		// // creating the chart
		// final LineChart<Number, Number> lineChart = new LineChart<Number,
		// Number>(xAxis, yAxis);
		//
		// lineChart.setTitle("Stock Monitoring, 2010");
		// // defining a series
		// XYChart.Series series = new XYChart.Series();
		// series.setName("My portfolio");
		// // populating the series with data
		// series.getData().add(new XYChart.Data(1, 23));
		// series.getData().add(new XYChart.Data(2, 14));
		// series.getData().add(new XYChart.Data(3, 15));
		// series.getData().add(new XYChart.Data(4, 24));
		// series.getData().add(new XYChart.Data(5, 34));
		// series.getData().add(new XYChart.Data(6, 36));
		// series.getData().add(new XYChart.Data(7, 22));
		// series.getData().add(new XYChart.Data(8, 45));
		// series.getData().add(new XYChart.Data(9, 43));
		// series.getData().add(new XYChart.Data(10, 17));
		// series.getData().add(new XYChart.Data(11, 29));
		// series.getData().add(new XYChart.Data(12, 25));
		// lineChart.getData().add(series);
		//
		// this.dataPane.getChildren().add(lineChart);
		// this.dataPane.getChildren().add(b);
		//
		// this.dataPane.getChildren().add(new Label(elements.toString()));
	}

}
