package vrpsim.feature.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.math3.ml.distance.DistanceMeasure;

@Deprecated
public class CalculationHelper {

	private DistanceMeasure distanceFunction;

	public CalculationHelper(DistanceMeasure distanceFunction) {
		this.distanceFunction = distanceFunction;
	}
	
	public void updateDistanceFunction(DistanceMeasure distanceFunction) {
		this.distanceFunction = distanceFunction;
	}

	public double[] calculateDistanceMatrixDistinctWithoutZeroDistance(double[][] data) {
		Set<String> evaluated = new HashSet<>();
		double[] distancesArr = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data.length; j++) {
				String ev1 = data[i].toString() + data[j].toString();
				String ev2 = data[j].toString() + data[i].toString();
				if (!Arrays.equals(data[i], data[j]) && (!evaluated.contains(ev1) || !evaluated.contains(ev2))) {
					distancesArr[i] = this.distanceFunction.compute(data[i], data[j]);
					evaluated.add(ev1);
					evaluated.add(ev2);
				}
			}

		}
		return distancesArr;
	}

	public double[] calculateDistanceMatrixWithoutZeroDistance(double[][] data) {
		double[] distancesArr = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data.length; j++) {
				if (!Arrays.equals(data[i], data[j])) {
					distancesArr[i] = this.distanceFunction.compute(data[i], data[j]);
				}
			}

		}
		return distancesArr;
	}

	public double calculateDistance(double[] d1, double[] d2) {
		return this.distanceFunction.compute(d1, d2);
	}

	public double calculateDistance(Vector<Double> v1, Vector<Double> v2) {
		return calculateDistance(toDoubleArray(v1), toDoubleArray(v2));
	}

	public double[] valuesIntoArray(double... values) {
		return values;
	}

	public double[] getDisticntValues(double[] values) {
		Set<Double> distinct = new HashSet<>();
		for (int i = 0; i < values.length; i++) {
			if (!distinct.contains(new Double(values[i]))) {
				distinct.add(values[i]);
			}
		}
		return distinct.stream().mapToDouble(Double::doubleValue).toArray();
	}

	public double round(double value, int decimalPlaces) {
		BigDecimal b = new BigDecimal(value);
		return b.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double[] round(double[] values, int decimalPlaces) {
		double[] result = new double[values.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = round(values[i], decimalPlaces);
		}
		return result;
	}

	public double[] calculateNearestNeighborsArray(double[][] data) {
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

	public double[] normalize(double[] values) {
		double sum = 0.0;
		for (int i = 0; i < values.length; i++) {
			sum += Math.pow(values[i], 2);
		}
		double[] normalized = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			normalized[i] = values[i] / sum;
		}
		return normalized;
	}

	public List<Vector<Double>> transform(double[][] vectors) {
		List<Vector<Double>> data = new ArrayList<>();
		for (int i = 0; i < vectors.length; i++) {
			Vector<Double> v = new Vector<>();
			for (int j = 0; j < vectors[i].length; j++) {
				v.add(vectors[i][j]);
			}
			data.add(v);
		}
		return data;
	}

	private double[] toDoubleArray(Vector<Double> v) {
		double[] result = new double[v.size()];
		for (int i = 0; i < v.size(); i++) {
			result[i] = v.get(i);
		}
		return result;
	}

}
