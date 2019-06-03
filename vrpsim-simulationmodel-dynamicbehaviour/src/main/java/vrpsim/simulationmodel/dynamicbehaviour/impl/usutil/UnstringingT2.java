package vrpsim.simulationmodel.dynamicbehaviour.impl.usutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnstringingT2 implements IUnstringingOperator {

	private static Logger logger = LoggerFactory.getLogger(UnstringingT2.class);

	/**
	 * Type II Removal: Assume that v j belongs to the neigh- bourhood of v i+1 , v
	 * k belongs to the neighbourhood of v i-1 , with v k being part of the subtour
	 * (v j+1 ,...,v i-2 ) and v l belongs to the neighbourhood of v k+1 , with v l
	 * being part of the subtour (v j ,...,v k-1 ). The re- moval of city v i
	 * results in the deletion of arcs (v i-1 ,v i ), (v i ,v i+1 ), (v j-1 ,v j ),
	 * (v k ,v k+1 ) and (v l ,v l+1 ); and the insertion of arcs (v i-1 ,v k ), (v
	 * l+1 ,v j-1 ), (v i+1 ,v j ) and (v l ,v k+1 ). As above, the subtours (v i+1
	 * ,...,v j-1 ) and (v l+1 ,...,v k ) are reversed.
	 */
	@Override
	public OpResult performOperator(Integer[] tour, int unstringIndex, int neighbourhoodSize, IDistanceCalculator distanceCalculator) {

		int unstringNext = (unstringIndex + 1) % tour.length;
		unstringNext = (unstringNext < 0) ? unstringNext + tour.length : unstringNext;
		int unstringPrevious = (unstringIndex - 1) % tour.length;
		unstringPrevious = (unstringPrevious < 0) ? unstringPrevious + tour.length : unstringPrevious;

		// int[] indexNeighbourhoodJ = Util.getNeighbourhoodIndexes(tour, unstringNext,
		// neighbourhoodSize, distanceCalculator);
		int[] indexNeighbourhoodJ = Util.getNeighbourhoodIndexesFromIndexInTour(tour, unstringNext, neighbourhoodSize, distanceCalculator);
		// int[] indexNeighbourhoodK = Util.getNeighbourhoodIndexes(tour,
		// unstringPrevious, neighbourhoodSize, distanceCalculator);
		int[] indexNeighbourhoodK = Util.getNeighbourhoodIndexesFromIndexInTour(tour, unstringPrevious, neighbourhoodSize, distanceCalculator);

		logger.trace("i is {}, indexes for neighborhood for i+1 {} vj are {}", unstringIndex, unstringNext,
				Arrays.toString(indexNeighbourhoodJ));
		logger.trace("i is {}, indexes for neighborhood for i-1 {} vk are {}", unstringIndex, unstringPrevious,
				Arrays.toString(indexNeighbourhoodK));

		List<Value> valuesToConsider = new ArrayList<>();
		for (int indexJ : indexNeighbourhoodJ) {

			int canNotBeJ = (unstringIndex - 1) % tour.length;
			canNotBeJ = (canNotBeJ < 0) ? canNotBeJ + tour.length : canNotBeJ;

			if (indexJ != unstringIndex && indexJ != canNotBeJ) {

				// determine valid tour where k have to be inside
				logger.trace("j is {} determine possible values for k", indexJ);
				int[] tourForK = getPoissbleIndexesFor(tour.length, indexJ + 1, unstringIndex - 2);
				int[] possibleIndexsForK = Util.getIntersection(tourForK, indexNeighbourhoodK);
				if (possibleIndexsForK != null) {

					for (int indexK : possibleIndexsForK) {
						int[] indexNeighbourhoodL = Util.getNeighbourhoodIndexesFromIndexInTour(tour, indexK + 1, neighbourhoodSize, distanceCalculator);
						logger.trace("i is {}, j is {}, k is {} indexes for neighborhood for k+1 {} vl are {}", unstringIndex, indexJ,
								indexK, indexK + 1, Arrays.toString(indexNeighbourhoodL));
						int[] tourForL = getPoissbleIndexesFor(tour.length, indexJ, indexK - 1);
						int[] possibleIndexesForL = Util.getIntersection(indexNeighbourhoodL, tourForL);

						for (int indexL : possibleIndexesForL) {
							valuesToConsider.add(new Value(indexJ, indexK, indexL));
						}

					}

				}
			}
		}

		Map<Integer, OpNode> bestTour = null;
		Value bestValue = null;
		double bestCost = Double.MAX_VALUE;
		for (Value toConsider : valuesToConsider) {
			int i = unstringIndex;
			int j = toConsider.j;
			int k = toConsider.k;
			int l = toConsider.l;
			logger.trace("Consider i = {}, j = {}, k = {}, l = {}", unstringIndex, j, k, l);

			// Build OpNodes
			Map<Integer, OpNode> nodes = Util.constrcutOpNodes(tour);
			// logger.trace("Tour before {}", Util.toString(nodes));

			// Revert
			Util.revert(nodes, i + 1, j - 1);
			Util.revert(nodes, l + 1, k);
			// Insert
			Util.insert(nodes, i - 1, k);
			Util.insert(nodes, l + 1, j - 1);
			Util.insert(nodes, i + 1, j);
			Util.insert(nodes, l, k + 1);
			// Remove
			nodes.remove(i);

			if (!Util.isNull(nodes)) {
				double c = Util.getDistance(nodes, distanceCalculator);
				logger.trace("Costs c of the solution is {}", c);
				if (c < bestCost) {
					bestCost = c;
					bestTour = nodes;
					bestValue = toConsider;
				}
			} else {
				logger.debug("No cost calculated due to invalid tour.");
			}

		}

		Integer[] result = null;
		if(bestTour == null) {
			Integer[] first = Arrays.copyOfRange(tour, 0, unstringIndex);
			Integer[] second = Arrays.copyOfRange(tour, unstringIndex+1, tour.length);
			result = ArrayUtils.addAll(first, second);
			bestCost = Util.getDistance(result, distanceCalculator);
		} else {
			result = Util.getTour(bestTour);
			logger.trace("Best cost for i = {} is {} with {} from {}", unstringIndex, bestCost, bestValue, Arrays.toString(result));
		}

		return new OpResult(result, bestCost);
	}

	private int[] getPoissbleIndexesFor(int tourLength, int startIndex, int endIndex) {
		startIndex = startIndex % tourLength;
		startIndex = startIndex < 0 ? startIndex + tourLength : startIndex;
		endIndex = endIndex % tourLength;
		endIndex = endIndex < 0 ? endIndex + tourLength : endIndex;
		int endIndexNext = (endIndex + 1) % tourLength;
		endIndexNext = endIndexNext < 0 ? endIndexNext + tourLength : endIndexNext;

		// Don't know why I have to add the one, but it works.
		int size = (endIndex - startIndex) % tourLength + 1;
		size = size < 0 ? size + tourLength : size;
		int[] result = new int[size];
		int index = startIndex;
		int i = 0;
		while (index != endIndexNext) {
			result[i++] = index;
			index = (index + 1) % tourLength;
		}
		return result;
	}

	public class Value {
		public int j;
		public int k;
		public int l;

		public Value(int j, int k, int l) {
			super();
			this.j = j;
			this.k = k;
			this.l = l;
		}

		@Override
		public String toString() {
			return "j=" + j + ", k=" + k + ", l=" + l;
		}
	}

}
