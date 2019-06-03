package vrpsim.simulationmodel.dynamicbehaviour.impl.usutil;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnstringingT1Test {

	private static Logger logger = LoggerFactory.getLogger(UnstringingT1Test.class);

	public static final void main(String[] args) {
		new UnstringingT1Test().testPerformOperator();
	}

	private final Map<Integer, Integer[]> buildIsCorrect() {
		HashMap<Integer, Integer[]> correct = new HashMap<>();
		correct.put(0, new Integer[] { 1, 5, 6, 4, 3, 2 });
		correct.put(1, new Integer[] { 0, 3, 2, 5, 4, 6 });
		correct.put(2, new Integer[] { 0, 1, 3, 5, 4, 6 });
		correct.put(3, new Integer[] { 0, 1, 2, 5, 4, 6 });
		correct.put(4, new Integer[] { 0, 6, 5, 2, 3, 1 });
		correct.put(5, new Integer[] { 2, 1, 0, 3, 4, 6 });
		correct.put(6, new Integer[] { 2, 1, 0, 3, 4, 5 });
		return correct;
	}

	/**
	 * Returns true if the order of the result is like in the given correct list
	 * (the correct list is determined with the index within the given map).
	 * 
	 * @param index
	 * @param result
	 * @param correct
	 * @return
	 */
	private final boolean isOrderCorrect(int index, Integer[] result, Map<Integer, Integer[]> correct) {
		boolean isCorrect = true;
		Integer[] correctOrder = correct.get(index);
		int startIndex = 0;
		boolean start = true;
		for (Integer r : result) {
			if (start) {
				start = false;
				startIndex = determineStartIndex(correctOrder, r);
			}
			int indexInCorrect = startIndex % correctOrder.length;
			indexInCorrect = indexInCorrect < 0 ? indexInCorrect + correctOrder.length : indexInCorrect;
			if (r != correctOrder[indexInCorrect]) {
				isCorrect = false;
				break;
			}
			startIndex++;
		}
		return isCorrect;
	}

	/**
	 * Returns the index from the given value in the list.
	 * 
	 * @param values
	 * @param value
	 * @return
	 */
	private final int determineStartIndex(Integer[] values, int value) {
		int result = -1;
		for (int i = 0; i < values.length; i++) {
			if (values[i] == value) {
				result = i;
				break;
			}
		}
		return result;
	}

	@Test
	public final void testPerformOperator() {

		Map<Integer, Integer[]> correct = buildIsCorrect();
		UnstringingT1 unstringT1 = new UnstringingT1();
		double[][] tour = getSimpleTour();

		for (int i = 0; i < tour.length; i++) {
//			 int i = 0;
			Integer[] result = unstringT1.performOperator(transform(tour), i, 4, getDistancecalculator(tour)).getTour();
			logger.debug("Result tour for index {} is {}", i, Arrays.toString(result));
			if (!isOrderCorrect(i, result, correct)) {
				 fail("False result for index " + i + ".");
			}
		}

	}

	private IDistanceCalculator getDistancecalculator(double[][] tour) {
		return new IDistanceCalculator() {
			@Override
			public double getDistance(Integer i, Integer j) {
				EuclideanDistance dc = new EuclideanDistance();
				return dc.compute(tour[i], tour[j]);
			}
		};
	}

	private final Integer[] transform(double[][] tour) {
		Integer[] result = new Integer[tour.length];
		for (int i = 0; i < tour.length; i++) {
			result[i] = i;
		}
		return result;
	}

	private final double[][] getSimpleTour() {

		// 0
		// 1
		// 3 2
		// 4 5
		// 6
		//
		// Tour is: 0-1-2-3-4-5-6

		double[][] tour = new double[7][2];
		// 0
		tour[0][0] = 0;
		tour[0][1] = 3.5;
		// 1
		tour[1][0] = 1.6;
		tour[1][1] = 3.3;
		// 2
		tour[2][0] = 2;
		tour[2][1] = 2.2;
		// 3
		tour[3][0] = 1;
		tour[3][1] = 2.2;
		// 4
		tour[4][0] = 1;
		tour[4][1] = 1.1;
		// 5
		tour[5][0] = 2;
		tour[5][1] = 1.1;
		// 6
		tour[6][0] = 0;
		tour[6][1] = 0;
		return tour;
	}

}
