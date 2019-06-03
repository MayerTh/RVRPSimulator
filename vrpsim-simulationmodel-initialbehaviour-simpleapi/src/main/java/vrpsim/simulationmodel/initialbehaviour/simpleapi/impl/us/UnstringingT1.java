package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.us;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnstringingT1 implements IUnstringingOperator {

	private static Logger logger = LoggerFactory.getLogger(UnstringingT1.class);

	/**
	 * Type I Removal: Assume that v j belongs to the neighbourhood of v i+1 and v k
	 * belongs to the neighbourhood of v i-1 , with v k being part of the sub-tour
	 * (v i+1 ,...,v j-1 ). The removal of city v i results in the deletion of arcs
	 * (v i-1 ,v i ), (v i ,v i+1 ), (v k ,v k+1 ) and (v j ,v j+1 ); and the
	 * insertion of arcs (v i-1 ,v k ), (v i+1 ,v j ) and (v k+1 ,v j+1 ). Also, the
	 * sub-tours (v i+1 ,...,v k ) and (v k+1 ,...,v j ) are reversed.
	 */
	@Override
	public OpResult performOperator(Integer[] tour, int unstringIndex, int neighbourhoodSize, IDistanceCalculator distanceCalculator) {

		int unstringNext = (unstringIndex + 1) % tour.length;
		unstringNext = (unstringNext < 0) ? unstringNext + tour.length : unstringNext;
		int unstringPrevious = (unstringIndex - 1) % tour.length;
		unstringPrevious = (unstringPrevious < 0) ? unstringPrevious + tour.length : unstringPrevious;

		int[] indexNeighbourhoodVJ = Util.getNeighbourhoodIndexesFromIndexInTour(tour, unstringNext, neighbourhoodSize, distanceCalculator);
		int[] indexNeighbourhoodVK = Util.getNeighbourhoodIndexesFromIndexInTour(tour, unstringPrevious, neighbourhoodSize,
				distanceCalculator);

		logger.trace("Indexes for neighbourhood for i+1 {} vj are {}", unstringNext, Arrays.toString(indexNeighbourhoodVJ));
		logger.trace("Indexes for neighbourhood for i-1 {} vk are {}", unstringPrevious, Arrays.toString(indexNeighbourhoodVK));

		List<Point> indexesToConsider = new ArrayList<>();
		for (int n = 0; n < indexNeighbourhoodVJ.length; n++) {
			int indexJ = indexNeighbourhoodVJ[n];
//			if (indexJ != unstringIndex && indexJ != unstringIndex-1) {
			
			int canNotBeJ = (unstringIndex - 1) % tour.length;
			canNotBeJ = (canNotBeJ < 0) ? canNotBeJ + tour.length : canNotBeJ;
			
			if (indexJ != unstringIndex && indexJ != canNotBeJ) {
//			if (indexJ != unstringIndex) {
				int[] possibleIndexesK = getPoissbleIndexesFor(tour.length, unstringNext, indexJ - 1);
				int[] intersectionForK = Util.getIntersection(possibleIndexesK, indexNeighbourhoodVK);
				if (intersectionForK != null) {
					logger.trace("j={}, k can be {}", indexJ, Arrays.toString(intersectionForK));
					for (int m = 0; m < intersectionForK.length; m++) {
						int indexK = intersectionForK[m];
						
						int canNotBeK = (unstringIndex - 1) % tour.length;
						canNotBeK = (canNotBeK < 0) ? canNotBeK + tour.length : canNotBeK;
						
//						if(indexK != indexJ && indexK != canNotBeK) {
						if(indexK != indexJ) {
							indexesToConsider.add(new Point(indexJ, indexK));
						}
					}
				}
			}
		}

		Map<Integer, OpNode> bestTour = null;
		Point bestValue = null;
		double bestCost = Double.MAX_VALUE;
		for (Point p : indexesToConsider) {

			int i = unstringIndex;
			int j = p.x;
			int k = p.y;

			logger.trace("Consider i = {}, j = {} and k = {}", unstringIndex, j, k);

			// Build OpNodes
			Map<Integer, OpNode> nodes = Util.constrcutOpNodes(tour);
			// Revert
//			logger.trace("Start: {}", Util.toString(nodes));
			Util.revert(nodes, i + 1, k);
//			logger.trace("After revert SubTour({} ... {}): {}", i+1, k, Util.toString(nodes));
			Util.revert(nodes, k + 1, j);
//			logger.trace("After revert SubTour({} ... {}): {}", k+1, j, Util.toString(nodes));
			// Insert
			Util.insert(nodes, i - 1, k);
//			logger.trace("After insert {} -> {}: {}", i-1, k, Util.toString(nodes));
			Util.insert(nodes, i + 1, j);
//			logger.trace("After insert {} -> {}: {}", i+1, j, Util.toString(nodes));
			Util.insert(nodes, k + 1, j + 1);
//			logger.trace("After insert {} -> {}: {}", k+1, j+1, Util.toString(nodes));
			// Remove
			nodes.remove(i);

			if (!Util.isNull(nodes)) {
				double c = Util.getDistance(nodes, distanceCalculator);
				logger.trace("Costs c of the solution is {}", c);
				if (c < bestCost) {
					bestCost = c;
					bestTour = nodes;
					bestValue = p;
				}
			} else {
				logger.debug("No cost calculated due invalid tour.");
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
			logger.trace("Best cost for i = {} is {} with j={}, k={} from {}", unstringIndex, bestCost, bestValue.x, bestValue.y, Util.toString(bestTour));
			logger.trace("Best cost fir i = {} is {} with j={}, k={} from {}", unstringIndex, bestCost, bestValue.x, bestValue.y, Arrays.toString(result));
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

}
