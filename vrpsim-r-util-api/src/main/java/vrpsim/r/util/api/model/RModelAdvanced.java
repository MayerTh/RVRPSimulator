package vrpsim.r.util.api.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RModelAdvanced {

	private final List<List<RPoint>> connectedPoints;
	private final List<RPoint> unconnectedPoints;
	private final RRaster raster;
	private RPoint max;
	private RPoint min;

	public RModelAdvanced(List<List<RPoint>> connectedPoints, List<RPoint> unconnectedPoints, RRaster raster) {
		this.connectedPoints = connectedPoints;
		this.unconnectedPoints = unconnectedPoints;
		this.raster = raster;
		determineMinMax(connectedPoints, unconnectedPoints);
	}

	public void determineMinMax(List<List<RPoint>> connectedPoints, List<RPoint> unconnectedPoints) {
		List<RPoint> allPoints = new ArrayList<>();

		if (unconnectedPoints != null) {
			allPoints.addAll(unconnectedPoints);
		}

		if (connectedPoints != null) {
			for (List<RPoint> points : connectedPoints) {
				allPoints.addAll(points);
			}
		}

		List<Double> allX = new LinkedList<>();
		List<Double> allY = new LinkedList<>();

		for (RPoint point : allPoints) {
			allX.add(point.getX());
			allY.add(point.getY());
		}

		double maxX = Collections.max(allX);
		double minX = Collections.min(allX);
		double maxY = Collections.max(allY);
		double minY = Collections.min(allY);

		this.max = new RPoint(maxX, maxY, false, false);
		this.min = new RPoint(minX, minY, false, false);

	}

	public List<List<RPoint>> getConnectedPoints() {
		return connectedPoints;
	}

	public List<RPoint> getUnconnectedPoints() {
		return unconnectedPoints;
	}

	public RRaster getRaster() {
		return raster;
	}

	public double getMaxX() {
		return this.max.getX();
	}

	public double getMaxY() {
		return this.max.getY();
	}

	public double getMinX() {
		return this.min.getX();
	}

	public double getMinY() {
		return this.min.getY();
	}

}
