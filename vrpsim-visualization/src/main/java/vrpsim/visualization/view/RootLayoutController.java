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
package vrpsim.visualization.view;

import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.events.IEvent;
import vrpsim.core.model.events.impl.OrderEvent;
import vrpsim.core.model.util.exceptions.EventException;
import vrpsim.core.model.util.exceptions.InitializationException;
import vrpsim.core.simulator.MainProgramm;

public class RootLayoutController extends Observable {

	private static Logger logger = LoggerFactory.getLogger(RootLayoutController.class);

	@FXML
	private MenuItem startSimulation;

	@FXML
	private MenuItem pauseSimulation;

	@FXML
	private MenuItem stepSimulation;

	@FXML
	private MenuItem speed1;

	@FXML
	private MenuItem speed250;

	@FXML
	private MenuItem speed500;

	@FXML
	private VBox infoPane;

	@FXML
	private AnchorPane leftPane;

	@FXML
	private ScrollPane scrollPane;

	@FXML
	private BorderPane rootPane;

	@FXML
	private AnchorPane orderPane;
	
	@FXML
	private AnchorPane modelPane;
	
	@FXML
	private SplitPane centerPane;

	InfoPaneController infoPaneControler;
	
	private MainProgramm mainProgram;
	private boolean simulationPaused;
	private boolean performStep;
	private long speed = 250;

	public Pane getOrientation() {
		return this.rootPane;
	}

	private void managesSizes() {
		
		double width = this.rootPane.getWidth();
		double height = this.rootPane.getHeight();
		
		this.orderPane.setPrefHeight(height * 0.25);
		this.orderPane.setMinHeight(height * 0.25);
		this.orderPane.setMaxHeight(height * 0.25);
		this.orderPane.setPrefWidth(width * 0.75);
		
		this.leftPane.setPrefHeight(height);
		this.leftPane.setPrefWidth(width * 0.25);
		this.scrollPane.setPrefHeight(height);
		this.scrollPane.setPrefWidth(width * 0.25);
		this.infoPane.setPrefHeight(height);
		this.infoPane.setPrefWidth(width * 0.25);
		this.centerPane.setPrefHeight(height);
	}
	
	public void init(MainProgramm mainProgram, VRPSimulationModel model, double simulationEndTime) throws InitializationException {

		this.rootPane.heightProperty().addListener(event -> managesSizes());
		this.rootPane.widthProperty().addListener(event -> managesSizes());
		managesSizes();

		this.mainProgram = mainProgram;
		this.mainProgram.init(model, simulationEndTime);
		pauseSimulation.setDisable(true);
		speed1.setDisable(false);
		speed250.setDisable(true);
		speed500.setDisable(false);

		startSimulation.setOnAction(event -> {
			startSimulation.setDisable(true);
			pauseSimulation.setDisable(false);
			simulationPaused = false;
			runNextSimulationStep();
		});

		pauseSimulation.setOnAction(event -> {
			startSimulation.setDisable(false);
			pauseSimulation.setDisable(true);
			simulationPaused = true;
		});

		stepSimulation.setOnAction(event -> {
			performStep = true;
			runNextSimulationStep();
		});

		speed1.setOnAction(event -> {
			speed = 1;
			speed1.setDisable(true);
			speed250.setDisable(false);
			speed500.setDisable(false);
		});

		speed250.setOnAction(event -> {
			speed = 250;
			speed1.setDisable(false);
			speed250.setDisable(true);
			speed500.setDisable(false);
		});

		speed500.setOnAction(event -> {
			speed = 500;
			speed1.setDisable(false);
			speed250.setDisable(false);
			speed500.setDisable(true);
		});

	}

	private void runNextSimulationStep() {

		if (!mainProgram.isSimulaationFinsihed()) {

			if (!simulationPaused || performStep) {

				Task<IEvent> task = new Task<IEvent>() {
					@Override
					public IEvent call() throws InterruptedException {
						try {
							IEvent event = mainProgram.runStep();
							Thread.sleep(speed);
							return event;
						} catch (EventException e) {
							e.printStackTrace();
							throw new RuntimeException(e.getMessage());
						}
					}
				};
				new Thread(task).start();
				task.setOnSucceeded(stateEvent -> {
					try {
						setChanged();
						IEvent got = task.get();
						if(got instanceof OrderEvent) {
							// Pause if new order arrives.
//							pauseSimulation.fire();
						}
						notifyObservers(got);
						if (!performStep) {
							runNextSimulationStep();
						} else {
							performStep = false;
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e.getMessage());
					}
				});
				task.setOnFailed(stateEvent -> {
					Throwable e = task.getException();
					logger.error("Run step execution failed due to not catched exception: {}.", e.getClass());
					String stackTrace = "";
					for (StackTraceElement ste : e.getStackTrace()) {
						stackTrace += "\t" + ste.getFileName() + "." + ste.getClassName() + "." + ste.getMethodName() + ":"
								+ ste.getLineNumber() + "\n";
					}
					logger.error("Stacktrace: \n {}", stackTrace);
					e.printStackTrace();
					throw new RuntimeException(
							"Following error happened during run step execution: " + task.getException() + " \nStacktrace:\n" + stackTrace);
				});

			}
		} else {
			startSimulation.setDisable(true);
			stepSimulation.setDisable(true);
			pauseSimulation.setDisable(true);
		}

		// Simulation ends.
	}

	public VBox getInfoPane() {
		return this.infoPane;
	}
	
	public AnchorPane getOrderPane() {
		return this.orderPane;
	}

}
