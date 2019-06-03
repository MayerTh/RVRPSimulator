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

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import vrpsim.visualization.view.InfoPaneController;

public class VisualizationPopup extends Pane implements Comparable<VisualizationPopup> {// Popup {

//	private static Logger logger = LoggerFactory.getLogger(Visualisation.class);

	protected VBox dataBox;

	private double width = 250;
	private double height = 150;
	private final Color backgroundColorHeader = Color.SILVER;
	private final Color backgroundColorData = Color.WHITE;

	private Label titleLable;
	private InfoPaneController controller;

	private Pane mainPane;
	private ImageView close;
	private Line line;
	private Pane pane2;
	
	protected ScrollPane scrollPane2;
	protected Pane headerPane;

	private double closeImagaeBias;
	private boolean hasClose = true;

	private long addedAt = 0L;

	public long getAddedAt() {
		return addedAt;
	}

	public void setAddedAt(long addedAt) {
		this.addedAt = addedAt;
	}

	public VisualizationPopup(InfoPaneController controller, double closeImagaeBias) {
		this.controller = controller;
		this.closeImagaeBias = closeImagaeBias; 
	}

	public void buildPopup(String title) {
		buildPopup(title, height, width, true);
	}

	public void buildPopup(String title, double width) {
		buildPopup(title, height, width, true);
	}

	public void buildPopup(String title, double width, double height, boolean hasScrollpane) {
		buildPopup(title, width, height, hasScrollpane, true);
	}

	public void adjustWidth(double width) {
		if (this.hasClose) {
			this.close.relocate(width - 20, 8);
		}
		this.line.setEndX(width - 10);
	}

	public void buildPopup(String title, double width, double height, boolean hasScrollpane, boolean hasClose) {
		this.hasClose = hasClose;

		this.width = width;
		this.height = height;
		this.setMinHeight(height);
		this.setWidth(width);

		mainPane = new Pane();
		headerPane = new Pane();
		mainPane.prefWidthProperty().bind(this.controller.getOrientationWidthProperty(this));
		this.controller.getOrientationWidthProperty(this)
				.addListener(e -> adjustWidth(this.controller.getOrientationWidthProperty(this).getValue().doubleValue() - closeImagaeBias));
		headerPane.prefWidthProperty().bind(this.controller.getOrientationWidthProperty(this));

		headerPane.setBackground(new Background(new BackgroundFill(backgroundColorHeader, null, null)));
		headerPane.setMinWidth(width);

		if (this.hasClose) {
			close = new ImageView("close2.png");
			close.setOnMouseClicked(event -> {
				controller.remove(this);
			});
			close.relocate(width - 20, 8);
			headerPane.getChildren().add(close);
		};

		line = new Line(10, 30, width - 10, 30);
		line.setFill(Color.LIGHTGREY);

		titleLable = new Label(title);
		titleLable.relocate(8, 8);
		headerPane.getChildren().addAll(titleLable, line);

		// headerPane.setOnMouseDragged(event -> {
		// this.setAnchorX(event.getScreenX());
		// this.setAnchorY(event.getScreenY());
		// });

		// this.getContent().add(mainPane);
		this.getChildren().add(mainPane);
		this.dataBox = new VBox();
		this.dataBox.setBackground(new Background(new BackgroundFill(backgroundColorData, null, null)));

		if (hasScrollpane) {
			scrollPane2 = new ScrollPane();
			scrollPane2.prefWidthProperty().bind(this.controller.getOrientationWidthProperty(this));
			scrollPane2.setBackground(new Background(new BackgroundFill(backgroundColorData, null, null)));
			scrollPane2.setPrefHeight(height);
//			scrollPane2.setMaxHeight(height);
			scrollPane2.relocate(0, 30);
			scrollPane2.autosize();
			mainPane.getChildren().addAll(headerPane, scrollPane2);
			scrollPane2.setContent(this.dataBox);
			this.dataBox.prefHeightProperty().bind(scrollPane2.heightProperty());
			this.dataBox.prefWidthProperty().bind(scrollPane2.widthProperty());
		} else {
			pane2 = new Pane();
			pane2.prefWidthProperty().bind(this.controller.getOrientationWidthProperty(this));
			pane2.setBackground(new Background(new BackgroundFill(backgroundColorData, null, null)));
			pane2.setPrefHeight(height);
			pane2.setMaxHeight(height);
			pane2.relocate(0, 30);
			pane2.autosize();
			mainPane.getChildren().addAll(headerPane, pane2);
			pane2.getChildren().add(this.dataBox);
			this.dataBox.prefHeightProperty().bind(pane2.heightProperty());
			this.dataBox.prefWidthProperty().bind(pane2.widthProperty());
		}
	}

	protected void changeTitel(String newTitle) {
		this.titleLable.setText(newTitle);
	}

	@Override
	public int compareTo(VisualizationPopup o) {
		return Long.compare(o.getAddedAt(), this.addedAt) * -1;
	}

}
