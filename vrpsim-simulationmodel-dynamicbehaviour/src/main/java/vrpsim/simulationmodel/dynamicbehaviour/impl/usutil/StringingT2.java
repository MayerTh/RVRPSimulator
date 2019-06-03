package vrpsim.simulationmodel.dynamicbehaviour.impl.usutil;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.simulationmodel.dynamicbehaviour.impl.usutil.Util.TourResult;

public class StringingT2 implements IStringingOperator {

	private static Logger logger = LoggerFactory.getLogger(StringingT2.class);

	@Override
	public OpResult performOperator(Integer[] tour, Integer toString, int neighbourhoodSize, IDistanceCalculator distanceCalculator) {

		int counterNull = 0;
		int counterNotNull = 0;
		int[] neigbourHoodJ = Util.getNeighbourhoodIndexesFromValue(tour, toString, neighbourhoodSize, distanceCalculator);
		// logger.trace("neigbourHoodJ for x={} is {} ", toString,
		// Arrays.toString(neigbourHoodJ));

		Integer[] bestTour = null;
		double bestCost = Double.MAX_VALUE;
		for (int i = 0; i < tour.length; i++) {

			// Map<Integer, OpNode> workWithTourModel = tourModel;
			int iNext = (i + 1) % tour.length;
			iNext = (iNext < 0) ? iNext + tour.length : iNext;
			int[] neigbourHoodK = Util.getNeighbourhoodIndexesFromIndexInTour(tour, iNext, neighbourhoodSize, distanceCalculator);
			// logger.trace("neigbourHoodK for i+1={} is {} ", iNext,
			// Arrays.toString(neigbourHoodK));

			for (int j : neigbourHoodJ) {

				if (i == j) {
					Map<Integer, OpNode> tourModel = Util.constrcutOpNodes(tour);
					int indexToString = tour.length;
					tourModel.put(indexToString, new OpNode(toString));

					// logger.trace("Insert between (i,i+1) - ({},{})", i, iNext);
					// insert between i and i+1
					tourModel.get(i).insert(tourModel.get(indexToString));
					tourModel.get(indexToString).insert(tourModel.get(iNext));
					// calc costs
					double cost = Util.getDistance(tourModel, distanceCalculator);
					// logger.trace("Costs c of the solution is {}", cost);
					// Integer[] result = Util.getTour(tourModel);
					// logger.trace("Costs {} from {}", cost, Arrays.toString(result));
					if (cost < bestCost) {
						bestCost = cost;
						bestTour = Util.getTour(tourModel, toString).getTour();
					}
				} else {

					int jNext = (j + 1) % tour.length;
					jNext = (jNext < 0) ? jNext + tour.length : jNext;
					int[] neigbourHoodL = Util.getNeighbourhoodIndexesFromIndexInTour(tour, jNext, neighbourhoodSize, distanceCalculator);
					// logger.trace("neigbourHoodL j+1={} is {} ", jNext,
					// Arrays.toString(neigbourHoodL));

					for (int k : neigbourHoodK) {
						if (k != j && k != jNext) {

							for (int l : neigbourHoodL) {
								if (l != i && i != iNext) {

									Map<Integer, OpNode> workWithTourModel = Util.constrcutOpNodes(tour);
									int indexToString = tour.length;
									workWithTourModel.put(indexToString, new OpNode(toString));

									int kPrevious = (k - 1) % tour.length;
									kPrevious = (kPrevious < 0) ? kPrevious + tour.length : kPrevious;
									int lPrevious = (l - 1) % tour.length;
									lPrevious = (lPrevious < 0) ? lPrevious + tour.length : lPrevious;

									// logger.trace("Consider i = i({})v({}), j = i({})v({}), k = i({})v({}), l =
									// i({})v({})", i,
									// workWithTourModel.get(i).getValue(), j, workWithTourModel.get(j).getValue(),
									// k,
									// workWithTourModel.get(k).getValue(), l, workWithTourModel.get(l).getValue());
									// logger.trace("Before: {}", Util.toString(workWithTourModel));

									// delete
									workWithTourModel.get(i).deleteTo();
									workWithTourModel.get(lPrevious).deleteTo();
									workWithTourModel.get(j).deleteTo();
									workWithTourModel.get(kPrevious).deleteTo();
									// logger.trace("After Deletion: {}", Util.toString(workWithTourModel));

									// insert
									workWithTourModel.get(i).insert(workWithTourModel.get(indexToString));
									// logger.trace("After insert {} to {}: {}", i, indexToString,
									// Util.toString(workWithTourModel));
									workWithTourModel.get(indexToString).insert(workWithTourModel.get(j));
									// logger.trace("After insert {} to {}: {}", indexToString, j,
									// Util.toString(workWithTourModel));
									workWithTourModel.get(l).insert(workWithTourModel.get(jNext));
									// logger.trace("After insert {} to {}: {}", l, jNext,
									// Util.toString(workWithTourModel));
									workWithTourModel.get(kPrevious).insert(workWithTourModel.get(lPrevious));
									// logger.trace("After insert {} to {}: {}", kPrevious, lPrevious,
									// Util.toString(workWithTourModel));
									workWithTourModel.get(iNext).insert(workWithTourModel.get(k));
									// logger.trace("After insert {} to {}: {}", iNext, k,
									// Util.toString(workWithTourModel));

									// revert
									// Util.revert(workWithTourModel, iNext, lPrevious);
									// logger.trace("After revert {} to {}: {}", iNext, lPrevious,
									// Util.toString(workWithTourModel));
									// Util.revert(workWithTourModel, l, j);
									// logger.trace("After revert {} to {}: {}", l, j,
									// Util.toString(workWithTourModel));

									TourResult result = Util.getTour(workWithTourModel, toString);
									// logger.trace("Calculated tour is {}", Arrays.toString(result.getTour()));
									if (result.isValidTour()) {
										counterNotNull++;
										double cost = Util.getDistance(result.getTour(), distanceCalculator);
										// logger.trace("Costs c of the solution is {}", cost);
										if (cost < bestCost) {
											bestCost = cost;
											bestTour = result.getTour();
										}

									} else {
										counterNull++;
										// logger.debug("Can not calculate costs due to NULL detected in solution.");
									}

								}
							}
						}
					}

				}
				// Test
				// break;
			}
			// Test
			// break;
		}

		// Integer[] result = Util.getTour(bestTourModel);
		// logger.debug("Best cost {} from {}", bestCost, Util.toString(bestTourModel));
		// logger.debug("Null/NotNull = {}/{} Best cost {} from {}", counterNull,
		// counterNotNull, bestCost, Arrays.toString(bestTour));
		return new OpResult(bestTour, bestCost);
	}

}
