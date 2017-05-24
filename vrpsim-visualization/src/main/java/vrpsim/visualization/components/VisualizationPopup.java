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
import javafx.stage.Popup;

public class VisualizationPopup extends Popup {

	private double width = 250;
	private double height = 150;
	private final Color backgroundColorHeader = Color.SILVER;
	private final Color backgroundColorData = Color.WHITE;

	protected VBox dataBox;
	private Label titleLable;

	public void buildPopup(String title) {
		buildPopup(title, height, width, true);
	}

	public void buildPopup(String title, double width) {
		buildPopup(title, height, width, true);
	}

	public void buildPopup(String title, double height, double width, boolean hasScrollpane) {
		this.width = width;
		this.height = height;

		Pane mainPane = new Pane();
		Pane headerPane = new Pane();
		headerPane.setBackground(new Background(new BackgroundFill(backgroundColorHeader, null, null)));
		headerPane.setMinWidth(width);
		ImageView close = new ImageView("close.png");
		close.setOnMouseClicked(event -> {
			this.hide();
		});
		close.relocate(width - 20, 8);
		Line line = new Line(10, 30, width - 10, 30);
		line.setFill(Color.LIGHTGREY);

		titleLable = new Label(title);
		titleLable.relocate(8, 8);
		headerPane.getChildren().addAll(titleLable, close, line);

		headerPane.setOnMouseDragged(event -> {
			this.setAnchorX(event.getScreenX());
			this.setAnchorY(event.getScreenY());
		});

		this.getContent().add(mainPane);
		this.dataBox = new VBox();
		this.dataBox.setBackground(new Background(new BackgroundFill(backgroundColorData, null, null)));

		if (hasScrollpane) {
			ScrollPane scrollPane = new ScrollPane();
			scrollPane = new ScrollPane();
			scrollPane.setBackground(new Background(new BackgroundFill(backgroundColorData, null, null)));
			scrollPane.setPrefSize(width, height);
			scrollPane.setMaxSize(width, height);
			scrollPane.relocate(0, 30);
			scrollPane.autosize();
			mainPane.getChildren().addAll(headerPane, scrollPane);
			scrollPane.setContent(this.dataBox);
			this.dataBox.prefHeightProperty().bind(scrollPane.heightProperty());
			this.dataBox.prefWidthProperty().bind(scrollPane.widthProperty());
		} else {
			Pane pane = new Pane();
			pane.setBackground(new Background(new BackgroundFill(backgroundColorData, null, null)));
			pane.setPrefSize(width, height);
			pane.setMaxSize(width, height);
			pane.relocate(0, 30);
			pane.autosize();
			mainPane.getChildren().addAll(headerPane, pane);
			pane.getChildren().add(this.dataBox);
			this.dataBox.prefHeightProperty().bind(pane.heightProperty());
			this.dataBox.prefWidthProperty().bind(pane.widthProperty());
		}
	}

	protected void changeTitel(String newTitle) {
		this.titleLable.setText(newTitle);
	}

}
