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
package vrpsim.dynamicvrprep.model.impl.dynamicfeature.util;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.vrprep.model.instance.Instance.Network.Nodes.Node;
import org.vrprep.model.instance.Instance.Requests.Request;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;

public class DynamicModelDegreeCalculationHelper {

	private Map<BigInteger, Request> requests = new HashMap<>();
	private Map<BigInteger, Node> nodes = new HashMap<>();

	public DynamicModelDegreeCalculationHelper(DynamicVRPREPModel dynamicModel) {
		for (Node node : dynamicModel.getVRPREPInstance().getNetwork().getNodes().getNode()) {
			this.nodes.put(node.getId(), node);
		}
		for (Request request : dynamicModel.getVRPREPInstance().getRequests().getRequest()) {
			this.requests.put(request.getId(), request);
		}
	}

	public Node getNode(BigInteger nodeId) {
		return this.nodes.get(nodeId);
	}

	public Request getRequest(BigInteger requestId) {
		return this.requests.get(requestId);
	}

	public double getDistance(BigInteger requestId1, BigInteger requestId2) {
		return getDistance(requests.get(requestId1), requests.get(requestId2));
	}

	public double getDistanceNodeRequest(BigInteger nodeId, BigInteger requestId) {
		Node n1 = this.nodes.get(nodeId);
		Node n2 = this.nodes.get(this.requests.get(requestId).getNode());
		return calculateDistance(n1.getCx(), n1.getCy(), n2.getCx(), n2.getCy());
	}

	private double getDistance(Request r1, Request r2) {
		Node nr1 = this.nodes.get(r1.getNode());
		Node nr2 = this.nodes.get(r2.getNode());
		return calculateDistance(nr1.getCx(), nr1.getCy(), nr2.getCx(), nr2.getCy());
	}

	public static double[][] getNNearestNeighbors(double x1, double y1, double[][] points, int numberNN) {

		Arrays.sort(points, new Comparator<double[]>() {
			@Override
			public int compare(double[] o1, double[] o2) {
				double distanceO1 = calculateDistance(x1, y1, o1[0], o1[1]);
				double distanceO2 = calculateDistance(x1, y1, o2[0], o2[1]);
				return Double.compare(distanceO1, distanceO2);
			}
		});

		boolean takeFist = !(calculateDistance(x1, y1, points[0][0], points[0][1]) == 0);
		int bias = takeFist ? 0 : 1;
		double[][] result = new double[numberNN][];
		for (int n = 0; n < numberNN; n++) {
			result[n] = new double[2];
			result[n][0] = points[n + bias][0];
			result[n][1] = points[n + bias][1];
		}

		return result;
	}

	public static double[] getDistancesFromNNearestNeighbors(double x1, double y1, double[][] points, int numberNN) {
		double[][] nearestNeighbors = getNNearestNeighbors(x1, y1, points, numberNN);
		return getDistances(x1, y1, nearestNeighbors);
	}

	public static double[] getDistances(double x1, double y1, double[][] points) {
		double[] result = new double[points.length];
		for (int i = 0; i < points.length; i++) {
			double distance = calculateDistance(x1, y1, points[i][0], points[i][1]);
			result[i] = distance;
		}
		return result;
	}

	public static double[] getDistancesSorted(double x1, double y1, double[][] points) {
		double[] result = getDistances(x1, y1, points);
		Arrays.sort(result);
		return result;
	}

	public static double calculateDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	public double getDistance(Point p, Request r) {
		Node n = this.nodes.get(r.getNode());
		return calculateDistance(p.x, p.y, n.getCx(), n.getCy());
	}

	public Point calculateCentroid() {
		double minXStatic = Double.MAX_VALUE;
		double minYStatic = Double.MAX_VALUE;
		double maxXStatic = Double.MIN_VALUE;
		double maxYStatic = Double.MIN_VALUE;
		for (Request request : this.requests.values()) {
			BigInteger idR = request.getId();
			double x = this.getNode(this.getRequest(idR).getNode()).getCx();
			double y = this.getNode(this.getRequest(idR).getNode()).getCy();
			minXStatic = Double.min(minXStatic, x);
			maxXStatic = Double.max(maxXStatic, x);
			minYStatic = Double.min(minYStatic, y);
			maxYStatic = Double.max(maxYStatic, y);
		}

		double a = Math.abs(maxYStatic - minYStatic) / 2 + minYStatic;
		double b = Math.abs(maxXStatic - minXStatic) / 2 + minXStatic;

		Point result = new Point();
		result.x = a;
		result.y = b;
		return result;
	}

	public class Point {
		public double x;
		public double y;
	}

	public Point getPoint(double x, double y) {
		Point result = new Point();
		result.x = x;
		result.y = y;
		return result;
	}

	public double getDistance(Point p, BigInteger requestId) {
		Node n = this.nodes.get(this.requests.get(requestId).getNode());
		return calculateDistance(p.x, p.y, n.getCx(), n.getCy());
	}

	public static double calculateAngle(double ax, double ay, double x2, double y2, double x3, double y3) {
		// https://de.serlo.org/mathe/geometrie/analytische-geometrie/grundbegriffe-vektorrechnung/vektorbegriff/vektor-zwischen-zwei-punkten-berechnen
		// https://www.mathebibel.de/winkel-zwischen-zwei-vektoren
		double skalar = (ax - x2) * (ax - x3) + (ay - y2) * (ay - y3);
		double len1 = Math.sqrt(Math.pow(ax - x2, 2) + Math.pow(ay - y2, 2));
		double len2 = Math.sqrt(Math.pow(ax - x3, 2) + Math.pow(ay - y3, 2));
		return (360 / (2 * Math.PI)) * Math.acos(skalar / (len1 * len2));
	}

}
