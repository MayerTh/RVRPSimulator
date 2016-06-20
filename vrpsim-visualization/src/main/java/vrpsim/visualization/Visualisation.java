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
package vrpsim.visualization;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.util.exceptions.InitializationException;
import vrpsim.core.simulator.MainProgramm;
import vrpsim.visualization.view.ModelViewController;
import vrpsim.visualization.view.RootLayoutController;

public class Visualisation extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	private RootLayoutController rootLayoutController;
	
	private static VRPSimulationModel model;
	private static MainProgramm mainProgram;
	private static double simulationEndTime;

	public static void init(MainProgramm mainProgram, VRPSimulationModel simulationModel, double simulationEndTime) {
		Visualisation.mainProgram = mainProgram;
		Visualisation.model = simulationModel;
		Visualisation.simulationEndTime = simulationEndTime;
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("vrpsim - visualization");

		this.initRootLayout();
		this.showModelView();
	}

	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Visualisation.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			this.rootLayoutController = loader.getController();
			this.rootLayoutController.init(mainProgram, model, simulationEndTime);
			
			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InitializationException e) {
			e.printStackTrace();
		}
	}

	public void showModelView() {
		try {
			// Load model view.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Visualisation.class.getResource("view/ModelView.fxml"));
			AnchorPane modelView = (AnchorPane) loader.load();

			// Set model view into the center of root layout.
			rootLayout.setCenter(modelView);

			// Give the controller access to the main app.
			ModelViewController controller = loader.getController();
			controller.init(model);

			// Set ModelViewController as observer to RootLayout.
			this.rootLayoutController.addObserver(controller);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
