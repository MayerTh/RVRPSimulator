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
package vrpsim.visualization.support;

import java.awt.Point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class TransformationManager {

	@SuppressWarnings(value = "unused")
	private static Logger logger = LoggerFactory.getLogger(TransformationManager.class);

	private double zoom = 4;
	private double zoomFactor = 0.2;
	private Point delta = new Point(0, 0);
	private Point deltaStart = new Point(0, 0);
	private boolean isInMotion = false;

	public double translateX(double lon, double maxX, double minLon, double maxLon) {
		double gz = zoom * zoomFactor;
		double deltaX = deltaStart.getX() - delta.getX();

		double perc = ((lon - minLon) / ((maxLon - minLon) / 100));
		double dResult = (maxX / 100) * perc;
		return (dResult * gz - deltaX);
	}

	public double translateY(double lat, double maxY, double minLat, double maxLat) {
		double gz = zoom * zoomFactor;
		double deltaY = deltaStart.getY() - delta.getY();

		double perc = ((lat - minLat) / ((maxLat - minLat) / 100));
		double dResult = (maxY / 100) * perc;
		return (((maxY - dResult) * gz) - deltaY);
	}

	private void setDeltaStart(Point p) {
		double deltaY = deltaStart.getY() - delta.getY();
		double deltaX = deltaStart.getX() - delta.getX();
		deltaStart.x = p.x + (int) deltaX;
		deltaStart.y = p.y + (int) deltaY;
	}

	public void mousePressed(MouseEvent event) {
		if (event.getButton().equals(MouseButton.PRIMARY)) {
			isInMotion = true;
			setDeltaStart(new Point((int) event.getX(), (int) event.getY()));
			delta = new Point((int) event.getX(), (int) event.getY());
		}
	}

	public void mouseReleased(MouseEvent event) {
		if (event.getButton().equals(MouseButton.PRIMARY)) {
			isInMotion = false;
		}
	}

	public void mouseDragged(MouseEvent event) {
		if (isInMotion) {
			delta = new Point((int) event.getX(), (int) event.getY());
		}

	}

	public void mouseWheelMoved(ScrollEvent e) {

		double scaledDeltaY = e.getDeltaY() > 0 ? -1 : 1;
		double formerZoom = zoom;
		zoom -= scaledDeltaY;
		zoom = zoom <= 1 ? 1 : zoom;

		double deltaX = deltaStart.getX() - delta.getX();
		double deltaY = deltaStart.getY() - delta.getY();

		double projX = (e.getX() + deltaX) / (formerZoom * zoomFactor);
		double projY = (e.getY() + deltaY) / (formerZoom * zoomFactor);

		double newPointX = (projX * zoom * zoomFactor) - deltaX;
		double newPointY = (projY * zoom * zoomFactor) - deltaY;

		setDeltaStart(new Point((int) newPointX, (int) newPointY));
		delta = new Point((int) e.getX(), (int) e.getY());
	}

	public int scalePictureStartX() {
		return (int) (0 - (deltaStart.getX() - delta.getX()));
	}

	public int scalePictureStartY() {
		return (int) (0 - (deltaStart.getY() - delta.getY()));
	}

	public int scalePicture(double max) {
		return (int) (max * zoom * zoomFactor);
	}
}
