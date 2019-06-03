package vrpsim.classificationadapter;

import java.util.Arrays;

import org.apache.commons.math3.ml.distance.DistanceMeasure;

/**
 * Parameter estimation like described in:
 * https://github.com/alitouka/spark_dbscan/wiki/Choosing-parameters-of-DBSCAN-algorithm
 * 
 * @author mayert
 *
 */
public class DBSCANParameterEstimator {

	private DistanceMeasure distanceFunction;

	public DBSCANParameterEstimator(DistanceMeasure distanceFunction) {
		this.distanceFunction = distanceFunction;
	}

	/**
	 * @param data
	 * @param percentagePointsIncluded
	 *            - between 0 and 1
	 * @return
	 */
	public double getEstimateForEpsilon(double[][] data, double percentagePointsIncluded) {
		double[] nnd = this.calculateNearestNeighborsArray(data);
		int index = new Double(nnd.length * percentagePointsIncluded).intValue();
		Arrays.sort(nnd);
		return nnd[index];
	}

	/**
	 * 
	 * @param data
	 * @param epsilon
	 * @param percentagePointsIncluded
	 *            between 0 and 1
	 * @return
	 */
	public int getEstimateForMinPoints(double[][] data, double epsilon, double percentagePointsIncluded) {
		int[] numberNeigbours = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			int counter = 0;
			for (int j = 0; j < data.length; j++) {
				if (i != j) {
					double dist = this.distanceFunction.compute(data[i], data[j]);
					if (dist <= epsilon) {
						counter++;
					}
				}
			}
			numberNeigbours[i] = counter;
		}

		Arrays.sort(numberNeigbours);
		int index = new Double(numberNeigbours.length * percentagePointsIncluded).intValue();
		return numberNeigbours[index];
	}

	/**
	 * Percentage of points included is 0.1%.
	 * 
	 * @param data
	 * @param epsilon
	 * @return
	 */
	public int getEstimateForMinPoints(double[][] data, double epsilon) {
		return getEstimateForMinPoints(data, epsilon, 0.1);
	}

	/**
	 * Percentage of points included is 98%.
	 * 
	 * @param data
	 * @return
	 */
	public double getEstimateForEpsilon(double[][] data) {
		return getEstimateForEpsilon(data, 0.98);
	}

	private double[] calculateNearestNeighborsArray(double[][] data) {
		double[] nearestNeighbors = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			nearestNeighbors[i] = calculateDistanceToNearestNeighbor(data[i], data);
		}
		return nearestNeighbors;
	}

	private double calculateDistanceToNearestNeighbor(double[] value, double[][] data) {
		double minDistance = Double.MAX_VALUE;
		for (int i = 0; i < data.length; i++) {
			if (!Arrays.equals(value, data[i])) {
				double distance = this.distanceFunction.compute(value, data[i]);
				if (distance < minDistance) {
					minDistance = distance;
				}
			}
		}
		return minDistance;
	}

}
