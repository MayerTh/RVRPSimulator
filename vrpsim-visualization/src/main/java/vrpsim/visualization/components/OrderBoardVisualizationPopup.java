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
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import vrpsim.core.model.solution.Order;

public class OrderBoardVisualizationPopup extends VisualizationPopup implements Observer {

	private List<Order> orders = new ArrayList<>();
	private TableView<OrderEntry> table;

	public OrderBoardVisualizationPopup() {
		buildPopup("Order Borad", 300, 800, false);
		this.table = buildTable();
	}

	@Override
	public void update(Observable o, Object arg) {
		update();
	}

	public void addOrder(Order order) {
		order.addObserver(this);
		this.orders.add(order);
		update();
	}

	private void update() {
		this.dataBox.getChildren().clear();

		ObservableList<OrderEntry> data = FXCollections.observableArrayList();
		for (Order order : this.getOrderedList()) {
			OrderEntry orderEntry = new OrderEntry(order);
			data.add(orderEntry);
		}

		table.setItems(data);
		this.dataBox.getChildren().add(table);
	}

	private List<Order> getOrderedList() {
		List<Order> newList = new ArrayList<>(this.orders);
		Collections.reverse(newList);
		return newList;
	}

	@SuppressWarnings("unchecked")
	private TableView<OrderEntry> buildTable() {
		TableView<OrderEntry> table = new TableView<>();
		TableColumn<OrderEntry, String> id = new TableColumn<OrderEntry, String>("Order Id");
		id.setCellValueFactory(new PropertyValueFactory<OrderEntry, String>("id"));
		TableColumn<OrderEntry, String> state = new TableColumn<OrderEntry, String>("Order State");
		state.setCellValueFactory(new PropertyValueFactory<OrderEntry, String>("state"));
		TableColumn<OrderEntry, String> owner = new TableColumn<OrderEntry, String>("Owner");
		owner.setCellValueFactory(new PropertyValueFactory<OrderEntry, String>("owner"));
		TableColumn<OrderEntry, String> provider = new TableColumn<OrderEntry, String>("Provider");
		provider.setCellValueFactory(new PropertyValueFactory<OrderEntry, String>("provider"));
		TableColumn<OrderEntry, String> transporter = new TableColumn<OrderEntry, String>("Transporter");
		transporter.setCellValueFactory(new PropertyValueFactory<OrderEntry, String>("transporter"));
		TableColumn<OrderEntry, String> amount_Type = new TableColumn<OrderEntry, String>("Amount");
		amount_Type.setCellValueFactory(new PropertyValueFactory<OrderEntry, String>("amount_Type"));
		TableColumn<OrderEntry, String> edd = new TableColumn<OrderEntry, String>("EDD");
		edd.setCellValueFactory(new PropertyValueFactory<OrderEntry, String>("edd"));
		TableColumn<OrderEntry, String> ldd = new TableColumn<OrderEntry, String>("LDD");
		ldd.setCellValueFactory(new PropertyValueFactory<OrderEntry, String>("ldd"));
		TableColumn<OrderEntry, String> initCosts = new TableColumn<OrderEntry, String>("Initial costs");
		initCosts.setCellValueFactory(new PropertyValueFactory<OrderEntry, String>("initCosts"));
		TableColumn<OrderEntry, String> addCosts = new TableColumn<OrderEntry, String>("Additional costs");
		addCosts.setCellValueFactory(new PropertyValueFactory<OrderEntry, String>("addCosts"));
		table.getColumns().addAll(id, state, owner, provider, transporter, amount_Type, edd, ldd, initCosts, addCosts);
		return table;
	}

	public class OrderEntry {
		private final SimpleStringProperty id;
		private final SimpleStringProperty state;
		private final SimpleStringProperty owner;
		private final SimpleStringProperty provider;
		private final SimpleStringProperty transporter;
		private final SimpleStringProperty amount_Type;
		private final SimpleStringProperty edd;
		private final SimpleStringProperty ldd;
		private final SimpleStringProperty initCosts;
		private final SimpleStringProperty addCosts;

		public OrderEntry(Order order) {
			id = new SimpleStringProperty(order.getId());
			state = new SimpleStringProperty(order.getOrderState().name());
			owner = new SimpleStringProperty(order.getOwner().getVRPSimulationModelElementParameters().getId());
			provider = new SimpleStringProperty((order.getProvider() != null
					? order.getProvider().getVRPSimulationModelElementParameters().getId() : "-"));
			transporter = new SimpleStringProperty(order.getServicedBy() != null
					? order.getServicedBy().getVRPSimulationModelElementParameters().getId() : "-");
			amount_Type = new SimpleStringProperty(order.getAmount() + " (" + order.getStorableParameters() + ")");
			edd = new SimpleStringProperty(
					(order.getEarliestDueDate() != null ? order.getEarliestDueDate().getValue() : "-"));
			ldd = new SimpleStringProperty(
					(order.getLatestDueDate() != null ? order.getLatestDueDate().getValue() : "-"));
			initCosts = new SimpleStringProperty(order.getInitialCost() != null
					? order.getInitialCost().getValue() + " " + order.getInitialCost().getUnit() : "-");
			addCosts = new SimpleStringProperty(order.getAdditionalCost() != null
					? order.getAdditionalCost().getValue() + " " + order.getAdditionalCost().getUnit() : "-");
		}

		public String getTransporter() {
			return transporter.get();
		}

		public String getId() {
			return id.get();
		}

		public String getState() {
			return state.get();
		}

		public String getOwner() {
			return owner.get();
		}

		public String getProvider() {
			return provider.get();
		}

		public String getAmount_Type() {
			return amount_Type.get();
		}

		public String getEdd() {
			return edd.get();
		}

		public String getLdd() {
			return ldd.get();
		}

		public String getInitCosts() {
			return initCosts.get();
		}

		public String getAddCosts() {
			return addCosts.get();
		}

	}
}
