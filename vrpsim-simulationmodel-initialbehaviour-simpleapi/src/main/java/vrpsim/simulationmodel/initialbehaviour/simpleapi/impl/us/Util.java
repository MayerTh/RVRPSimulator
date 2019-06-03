package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.us;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

	private static Logger logger = LoggerFactory.getLogger(Util.class);

	public static boolean isNull(Map<Integer, OpNode> structure) {
		Set<OpNode> handled = new HashSet<>();
		OpNode workWith = structure.get(structure.keySet().iterator().next());
		int inserted = 0;
		while (!handled.contains(workWith)) {
			handled.add(workWith);
			workWith = workWith.getNext();
			if (workWith == null) {
				return true;
			}
			inserted++;
		}
		return !(inserted == structure.size());
	}

	public static int adaptIndex(int index, int tourLength) {
		int result = index % tourLength;
		result = result < 0 ? result + tourLength : result;
		return result;
	}

	// private static String lineBreak = "\n";
	private static String lineBreak = "";

	public static String toString(Map<Integer, OpNode> structure) {
		String result = "" + lineBreak;
		for (Integer key : structure.keySet()) {
			result += "i(" + key + ")" + structure.get(key).toString() + ")||" + lineBreak;
		}
		return result + lineBreak;
	}

	/**
	 * Generates the {@link OpNode} out of the Integer values. The keys in the
	 * returned {@link Map} are the original indexes.
	 * 
	 * @param tour
	 * @return
	 */
	public static Map<Integer, OpNode> constrcutOpNodes(Integer[] tour) {
		Map<Integer, OpNode> result = new HashMap<>();
		// Build middle part.
		for (int i = 0; i < tour.length; i++) {
			OpNode next = new OpNode(tour[i]);
			result.put(i, next);

			int pi = (i - 1) % tour.length;
			pi = (pi < 0) ? pi + tour.length : pi;

			OpNode previous = result.get(pi);
			if (previous != null) {
				previous.setNext(next);
				next.setPrevious(previous);
			}
		}

		// Set next and previous from last and first.
		OpNode first = result.get(0);
		OpNode last = result.get(tour.length - 1);
		last.setNext(first);
		first.setPrevious(last);

		return result;
	}

	/**
	 * Removes the element with the given index from the tour, returns a new tour.
	 * 
	 * @param tour
	 * @param toRemoveIndex
	 * @return
	 */
	public static Integer[] removeAndCopy(Integer[] tour, int toRemoveIndex) {
		Integer[] result = new Integer[tour.length - 1];
		System.arraycopy(tour, 0, result, 0, toRemoveIndex);
		System.arraycopy(tour, toRemoveIndex + 1, result, toRemoveIndex, tour.length - toRemoveIndex - 1);
		return result;
	}

	/**
	 * Removes an index out of the rour, returns a copy.
	 * 
	 * @param tour
	 * @param indexI
	 * @return
	 */
	public static Integer[] remove(Integer[] tour, int indexI) {
		Integer[] result = new Integer[tour.length - 1];
		int correct = 0;
		for (int i = 0; i < tour.length; i++) {
			if (i != indexI) {
				result[i - correct] = tour[i];
			} else {
				correct = 1;
			}
		}
		return result;
	}

	public static double getDistance(Map<Integer, OpNode> structure, IDistanceCalculator distanceCalculator) {
		double distance = 0;
		Set<OpNode> handled = new HashSet<>();
		OpNode workWith = structure.get(structure.keySet().iterator().next());
		while (!handled.contains(workWith)) {
			distance += distanceCalculator.getDistance(workWith.getValue(), workWith.getNext().getValue());
			handled.add(workWith);
			workWith = workWith.getNext();
		}
		return distance;
	}

	public static Integer[] getTour(Map<Integer, OpNode> tour) {

		if (tour == null) {
			logger.trace("Can not build Integer[] tour because tour is null");
			return null;
		}

		Integer[] result = new Integer[tour.size()];
		Set<OpNode> handled = new HashSet<>();
		OpNode workWith = tour.get(tour.keySet().iterator().next());
		int insert = 0;
		while (!handled.contains(workWith)) {
			result[insert++] = workWith.getValue();
			handled.add(workWith);
			workWith = workWith.getNext();
		}
		return result;
	}

	public static TourResult getTour(Map<Integer, OpNode> tour, int startIndex) {

		if (tour == null) {
			logger.trace("Can not build Integer[] tour because tour is null");
			return null;
		}

		Integer[] result = new Integer[tour.size()];
		int inserted = 0;
		Set<OpNode> visited = new HashSet<>();
		OpNode workWith = tour.get(startIndex);
		visited.add(workWith);
		result[inserted++] = workWith.getValue();
		boolean tourIsValid = true;
		while (visited.size() < result.length) {
			OpNode newNext = workWith.getNext();
			if (newNext != null && !visited.contains(newNext)) {
				workWith = newNext;
				visited.add(workWith);
				result[inserted++] = workWith.getValue();
			} else {
				OpNode newNextOld = workWith.getNextOld();
				if (newNextOld != null && !visited.contains(newNextOld)) {
					workWith = newNextOld;
					visited.add(workWith);
					result[inserted++] = workWith.getValue();
				} else {
					OpNode previous = workWith.getPrevious();
					if (previous != null && !visited.contains(previous)) {
						workWith = previous;
						visited.add(workWith);
						result[inserted++] = workWith.getValue();
					} else {
						OpNode previousOld = workWith.getPreviousOld();
						if (previousOld != null && !visited.contains(previousOld)) {
							workWith = previousOld;
							visited.add(workWith);
							result[inserted++] = workWith.getValue();
						} else {
							tourIsValid = false;
							break;
						}
					}
				}
			}

		}

		return new TourResult(tourIsValid, result);
	}

	public static class TourResult {
		private final boolean validTour;
		private final Integer[] tour;

		public TourResult(boolean validTour, Integer[] tour) {
			super();
			this.validTour = validTour;
			this.tour = tour;
		}

		public boolean isValidTour() {
			return validTour;
		}

		public Integer[] getTour() {
			return tour;
		}
		
	}

	/**
	 * Returns the distance of the tour based on the given distance calculator.
	 * 
	 * @param tour
	 * @param distanceCalculator
	 * @return
	 */
	public static double getDistance(Integer[] tour, IDistanceCalculator distanceCalculator) {
		double cost = 0;
		for (int i = 0; i < tour.length; i++) {
			int index1 = tour[i];
			int index2 = tour[(i + 1) % tour.length];
			cost += distanceCalculator.getDistance(index1, index2);
		}
		return cost;
	}

	/**
	 * Reverts the edges between start and end.
	 * 
	 * @param tour
	 * @param startRevert
	 * @param endRevert
	 */
	public static void revert(Map<Integer, OpNode> tour, int startRevert, int endRevert) {

		startRevert = Util.adaptIndex(startRevert, tour.size());
		endRevert = Util.adaptIndex(endRevert, tour.size());

		OpNode startOpNode = tour.get(startRevert);
		OpNode endOpNode = tour.get(endRevert);

		if (startRevert == endRevert) {
			tour.get(startRevert).revert();
		} else {
			OpNode toRevert = startOpNode;
			while (true) {
				OpNode nextToRevert = toRevert.getNext();
				toRevert.revert();
				toRevert = nextToRevert;
				if (toRevert.equals(endOpNode)) {
					break;
				}
			}
			endOpNode.revert();
		}

	}

	/**
	 * Inserts new edges between from and to.
	 * 
	 * @param structure
	 * @param from
	 * @param to
	 */
	public static void insert(Map<Integer, OpNode> structure, int from, int to) {
		from = Util.adaptIndex(from, structure.size());
		to = Util.adaptIndex(to, structure.size());
		OpNode f = structure.get(from);
		OpNode t = structure.get(to);
		f.insert(t);
	}

	/**
	 * Reverts a part of the tour within the tour. Works and returns a copy.
	 * 
	 * @param tour
	 * @param startRevert
	 * @param endrevert
	 * @return
	 */
	public static Integer[] revertAndCopy(Integer[] tour, int startRevert, int endrevert) {

		// Correct the indexs
		startRevert = startRevert % tour.length;
		startRevert = (startRevert < 0) ? startRevert + tour.length : startRevert;
		endrevert = endrevert % tour.length;
		endrevert = (endrevert < 0) ? endrevert + tour.length : endrevert;

		Integer[] result = new Integer[tour.length];
		int revertIndex = 0;
		for (int i = 0; i < tour.length; i++) {
			int value = tour[i];
			if (i >= startRevert && i <= endrevert) {
				value = endrevert - revertIndex;
				revertIndex++;
			}
			result[i] = value;
		}
		return result;
	}

	/**
	 * 
	 * @param tour
	 *            - Indexes of the points in the Tour.
	 * @param neighbourhoodFrom
	 *            - Index of the given tour which represents the point which is the
	 *            start point for the neighbourhood
	 * @param neighbourhoodSize
	 *            - Size of the neighbourhood.
	 * @param distanceCalculator
	 *            - Calculates the distances between points based on the indexes of
	 *            the points.
	 * @return a list of indexes from the given tour list which represent the
	 *         neighbourhood
	 */
	public static int[] getNeighbourhoodIndexesAfterDistance(Integer[] tour, int neighbourhoodFrom, int neighbourhoodSize,
			IDistanceCalculator distanceCalculator) {

		// Possible to start with negative indexes.
		neighbourhoodFrom = neighbourhoodFrom % tour.length;
		neighbourhoodFrom = (neighbourhoodFrom < 0) ? neighbourhoodFrom + tour.length : neighbourhoodFrom;

		int[] neighbourhood = null;
		if (tour.length > neighbourhoodSize) {
			neighbourhood = new int[neighbourhoodSize];

			int left = 1;
			int right = 1;
			for (int i = 0; i < neighbourhoodSize; i++) {

				int indexLeft = (neighbourhoodFrom - left) % tour.length;
				indexLeft = (indexLeft < 0) ? indexLeft + tour.length : indexLeft;
				int indexRight = (neighbourhoodFrom + right) % tour.length;
				indexRight = (indexRight < 0) ? indexRight + tour.length : indexRight;
				// Note: caching from already calculated distances should be done by the
				// IDistanceCalculator
				double distanceLeft = distanceCalculator.getDistance(tour[neighbourhoodFrom], tour[indexLeft]);
				double distanceRight = distanceCalculator.getDistance(tour[neighbourhoodFrom], tour[indexRight]);

				if (distanceLeft < distanceRight) {
					neighbourhood[i] = indexLeft;
					left += 1;
				} else {
					neighbourhood[i] = indexRight;
					right += 1;
				}

			}
		} else {
			neighbourhood = new int[tour.length - 1];
			int filled = 0;
			for (int i = 0; i < tour.length; i++) {
				if (i != neighbourhoodFrom) {
					neighbourhood[filled++] = tour[i];
				}
			}
		}

		return neighbourhood;
	}

	/**
	 * 
	 * @param tour
	 *            - Indexes of the points in the Tour.
	 * @param neighbourhoodFromIndex
	 *            - Index of the given tour which represents the point which is the
	 *            start point for the neighbourhood
	 * @param neighbourhoodSize
	 *            - Size of the neighbourhood.
	 * @param distanceCalculator
	 *            - Calculates the distances between points based on the indexes of
	 *            the points.
	 * @return a list of indexes from the given tour list which represent the
	 *         neighbourhood
	 */
	public static int[] getNeighbourhoodIndexesFromIndexInTour(Integer[] tour, int neighbourhoodFromIndex, int neighbourhoodSize,
			IDistanceCalculator distanceCalculator) {

		// Possible to start with negative indexes.
		neighbourhoodFromIndex = neighbourhoodFromIndex % tour.length;
		neighbourhoodFromIndex = (neighbourhoodFromIndex < 0) ? neighbourhoodFromIndex + tour.length : neighbourhoodFromIndex;

		if(neighbourhoodSize > tour.length) {
			int[] result = new int[tour.length-1];
			int offset = 0;
			for(int i = 0; i < tour.length;i++) {
				if(i != neighbourhoodFromIndex) {
					result[i - offset] = i;
				} else {
					offset = 1;
				}
			}
			return result;
		}
		
		List<Distance> distances = new ArrayList<>();
		for (int i = 0; i < tour.length; i++) {
			if (i != neighbourhoodFromIndex) {
				double d = distanceCalculator.getDistance(tour[neighbourhoodFromIndex], tour[i]);
				distances.add(new Distance(i, d));
			}
		}

		Collections.sort(distances);
		int[] neighbourhood = new int[neighbourhoodSize];
		for (int i = 0; i < neighbourhoodSize; i++) {
			neighbourhood[i] = distances.get(i).index;
		}

		return neighbourhood;
	}

	public static int[] getNeighbourhoodIndexesFromValue(Integer[] tour, Integer neighbourhoodFromValue, int neighbourhoodSize,
			IDistanceCalculator distanceCalculator) {
		
		if(neighbourhoodSize > tour.length) {
			int[] result = new int[tour.length-1];
			int offset = 0;
			for(int i = 0; i < tour.length;i++) {
				if(tour[i] != neighbourhoodFromValue) {
					result[i - offset] = i;
				} else {
					offset = 1;
				}
			}
			return result;
		}
		
		List<Distance> distances = new ArrayList<>();
		for (int i = 0; i < tour.length; i++) {
			if (tour[i] != neighbourhoodFromValue) {
				double d = distanceCalculator.getDistance(tour[i], neighbourhoodFromValue);
				distances.add(new Distance(i, d));
			}
		}

		Collections.sort(distances);
		int[] neighbourhood = new int[neighbourhoodSize];
		for (int i = 0; i < neighbourhoodSize; i++) {
			neighbourhood[i] = distances.get(i).index;
		}

		return neighbourhood;

	}

	public static class Distance implements Comparable<Distance> {
		public int index;
		public double distance;

		public Distance(int index, double distance) {
			super();
			this.index = index;
			this.distance = distance;
		}

		@Override
		public int compareTo(Distance o) {
			return Double.compare(distance, o.distance);
		}
	}

	/**
	 * 
	 * @param tour
	 *            - Indexes of the points in the Tour.
	 * @param neighbourhoodFrom
	 *            - Index of the given tour which represents the point which is the
	 *            start point for the neighbourhood
	 * @param neighbourhoodSize
	 *            - Size of the neighbourhood.
	 * @param distanceCalculator
	 *            - Calculates the distances between points based on the indexes of
	 *            the points.
	 * @return a list of indexes from the given tour list which represent the
	 *         neighbourhood
	 */
	public static int[] getNeighbourhoodIndexesAfterDistanceOnTour(Integer[] tour, int neighbourhoodFrom, int neighbourhoodSize,
			IDistanceCalculator distanceCalculator) {

		int[] neighbourhood = null;
		if (tour.length > neighbourhoodSize) {
			neighbourhood = new int[neighbourhoodSize];

			int left = 1;
			int right = 1;
			double allDistanceLeft = 0.0;
			double allDistanceRight = 0.0;
			for (int i = 0; i < neighbourhoodSize; i++) {

				int indexLeft1 = (neighbourhoodFrom - left) % tour.length;
				indexLeft1 = (indexLeft1 < 0) ? indexLeft1 + tour.length : indexLeft1;
				int indexLeft2 = (indexLeft1 + 1) % tour.length;
				int indexRight1 = (neighbourhoodFrom + right) % tour.length;
				int indexRight2 = (indexRight1 - 1) % tour.length;
				indexRight2 = (indexRight2 < 0) ? indexRight2 + tour.length : indexRight2;

				// Note: caching from already calculated distances should be done by the
				// IDistanceCalculator
				double distanceLeftToPrevious = distanceCalculator.getDistance(tour[indexLeft2], tour[indexLeft1]);
				double distanceRightToPrevious = distanceCalculator.getDistance(tour[indexRight2], tour[indexRight1]);

				if (distanceLeftToPrevious + allDistanceLeft < distanceRightToPrevious + allDistanceRight) {
					neighbourhood[i] = indexLeft1;
					left += 1;
					allDistanceLeft += distanceLeftToPrevious;
				} else {
					neighbourhood[i] = indexRight1;
					right += 1;
					allDistanceRight += distanceRightToPrevious;
				}

			}
		} else {
			neighbourhood = new int[tour.length - 1];
			int filled = 0;
			for (int i = 0; i < tour.length; i++) {
				if (i != neighbourhoodFrom) {
					neighbourhood[filled++] = tour[i];
				}
			}
		}

		return neighbourhood;
	}

	/**
	 * Returns the intersection between the two sets.
	 * 
	 * @param setA
	 * @param setB
	 * @return
	 */
	public static final int[] getIntersection(int[] setA, int[] setB) {
		HashSet<Integer> set1 = new HashSet<Integer>();
		for (int i : setA) {
			set1.add(i);
		}

		HashSet<Integer> set2 = new HashSet<Integer>();
		for (int i : setB) {
			if (set1.contains(i)) {
				set2.add(i);
			}
		}

		int[] result = new int[set2.size()];
		int i = 0;
		for (int n : set2) {
			result[i++] = n;
		}

		return result;
	}

}
