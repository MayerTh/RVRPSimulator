package vrpsim.simulationmodel.dynamicbehaviour.impl.usutil;

import static org.junit.Assert.fail;

import java.util.Arrays;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.junit.Test;

public class UtilTest {

	public IDistanceCalculator getDistancecalculator(double[][] tour) {
		return new IDistanceCalculator() {
			@Override
			public double getDistance(Integer i, Integer j) {
				EuclideanDistance dc = new EuclideanDistance();
				return dc.compute(tour[i], tour[j]);
			}
		};
	}

	private final double[][] getSimpleTour() {
		
		// 7--6--5--4
		// |        |
		// 0--1--2--3
		
		double[][] tour = new double[8][2];
		tour[0][0] = 1;
		tour[0][1] = 1;
		tour[1][0] = 2;
		tour[1][1] = 1;
		tour[2][0] = 3;
		tour[2][1] = 1;
		tour[3][0] = 4;
		tour[3][1] = 1;
		tour[4][0] = 4;
		tour[4][1] = 2;
		tour[5][0] = 3;
		tour[5][1] = 2;
		tour[6][0] = 2;
		tour[6][1] = 2;
		tour[7][0] = 1;
		tour[7][1] = 2;
		return tour;
	}

	private final Integer[] transform(double[][] tour) {
		Integer[] result = new Integer[tour.length];
		for (int i = 0; i < tour.length; i++) {
			result[i] = i;
		}
		return result;
	}

	@Test
	public final void testGetNeighbourhoodIndexesAfterDistanceOnTour() {

		double[][] tour = getSimpleTour();
		int[] neighbourhood1 = Util.getNeighbourhoodIndexesAfterDistanceOnTour(transform(tour), 1, 4, getDistancecalculator(tour));
		// expected indexes in the neighbourhood are: [2, 0, 3, 7]
		// System.out.println(Arrays.toString(neighbourhood1));
		checkIfIsIn(neighbourhood1, new int[] { 2, 0, 3, 7 });

		int[] neighbourhood2 = Util.getNeighbourhoodIndexesAfterDistanceOnTour(transform(tour), 7, 4, getDistancecalculator(tour));
		// expected indexes in the neighbourhood are: [0, 6, 1, 5]
		// System.out.println(Arrays.toString(neighbourhood2));
		checkIfIsIn(neighbourhood2, new int[] { 0, 6, 1, 5 });
	}

	@Test
	public final void testGetNeighbourhoodIndexesAfterDistance() {

		double[][] tour = getSimpleTour();
		int[] neighbourhood3 = Util.getNeighbourhoodIndexesAfterDistance(transform(tour), 1, 4, getDistancecalculator(tour));
		// expected indexes in the neighbourhood are: [2, 0, 7, 6]
		// System.out.println(Arrays.toString(neighbourhood3));
		checkIfIsIn(neighbourhood3, new int[] { 2, 0, 7, 6 });

		int[] neighbourhood4 = Util.getNeighbourhoodIndexesAfterDistance(transform(tour), 7, 4, getDistancecalculator(tour));
		// expected indexes in the neighbourhood are: [0, 6, 1, 5]
		// System.out.println(Arrays.toString(neighbourhood4));
		checkIfIsIn(neighbourhood4, new int[] { 0, 6, 1, 5 });

	}

	private final void checkIfIsIn(int[] result, int[] correct) {
		for (int i = 0; i < correct.length; i++) {
			boolean isIn = false;
			for (int j = 0; j < result.length; j++) {
				if (correct[i] == result[j]) {
					isIn = true;
					break;
				}
			}
			if (!isIn) {
				fail(correct[i] + " is not in " + Arrays.toString(result));
			}
		}
	}

}
