package vrpsim.simulationmodel.dynamicbehaviour.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.impl.Location;
import vrpsim.core.model.network.impl.NetworkService;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.simulator.IClock;
import vrpsim.simulationmodel.dynamicbehaviour.DynamicHandlerException;
import vrpsim.simulationmodel.dynamicbehaviour.IDynamicBehaviourProviderHandler;
import vrpsim.simulationmodel.dynamicbehaviour.impl.usutil.IDistanceCalculator;
import vrpsim.simulationmodel.dynamicbehaviour.impl.usutil.OpResult;
import vrpsim.simulationmodel.dynamicbehaviour.impl.usutil.StringingT1;
import vrpsim.simulationmodel.dynamicbehaviour.impl.usutil.StringingT2;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourActivity;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourState;

public class US implements IDynamicBehaviourProviderHandler {

	private static Logger logger = LoggerFactory.getLogger(US.class);

	private final int neighbourhoodSize;

	public US(int neighbourhoodSize) {
		this.neighbourhoodSize = neighbourhoodSize;
	}

	private List<TourActivity> activities;
	private TourActivity toStringActivity;

	private Integer offset;
	private Integer startValue;
	private Integer depotValue;
	private Integer toStringValue;

	private Location depotLocation;
	private Location toStringLocation;

	@Override
	public void hanldeOrder(List<TourState> currentTourStates, TourActivity newOrder, IClock clock, StructureService structureService,
			NetworkService networkService) throws DynamicHandlerException {

		TourState tourState = currentTourStates.get(0);
		List<TourActivity> tourActivities = tourState.getTourActivities();
		this.toStringActivity = newOrder;

		this.offset = tourState.isAddAtFirstPlaceAllowed() ? 0 : 1;
		this.startValue = this.offset;
		// Add two at the end for depot and toString
		Integer[] tour = new Integer[tourActivities.size() - offset + 2];
		for (int i = 0; i < tourActivities.size(); i++) {
			tour[i] = i + offset;
		}
		int depotIndex = tourActivities.size() - offset + 0;
		int toStringIndex = depotIndex + 1;
		this.depotValue = depotIndex + this.offset;
		this.toStringValue = toStringIndex + this.offset;
		tour[depotIndex] = this.depotValue;
		tour[toStringIndex] = this.toStringValue;

		this.activities = tourActivities;
		this.depotLocation = ((INode) structureService.getDepots().get(0).getVRPSimulationModelStructureElementParameters().getHome())
				.getLocation();
		this.toStringLocation = ((INode) newOrder.getElement().getVRPSimulationModelStructureElementParameters().getHome()).getLocation();

		IDistanceCalculator distanceCalculator = new IDistanceCalculator() {
			@Override
			public double getDistance(Integer i, Integer j) {
				if ((i == startValue && j == depotValue) || (i == depotValue && j == startValue)) {
					return 0.0;
				}
				return getRealDistance(get(i), get(j));
			}
		};

		StringingT1 stringingT1 = new StringingT1();
//		logger.debug("Tour before T1 {}", Arrays.toString(tour));
		OpResult tourResultT1 = stringingT1.performOperator(tour, toStringIndex, neighbourhoodSize, distanceCalculator);
		Result resultT1 = isTourValid(tourResultT1);
		StringingT2 stringingT2 = new StringingT2();
//		logger.debug("Tour before T2 {}", Arrays.toString(tour));
		OpResult tourResultT2 = stringingT2.performOperator(tour, toStringIndex, neighbourhoodSize, distanceCalculator);
		Result resultT2 = isTourValid(tourResultT2);

		if (resultT1.isValid && resultT2.isValid) {

			if (tourResultT1.getCosts() < tourResultT2.getCosts()) {
				buildSolutionAndSetToTourState(resultT1, tourResultT1, tourState);
			} else {
				buildSolutionAndSetToTourState(resultT2, tourResultT2, tourState);
			}

		} else {

			if (resultT1.isValid || resultT2.isValid) {
				if (resultT1.isValid) {
					buildSolutionAndSetToTourState(resultT1, tourResultT1, tourState);
				}
				if (resultT2.isValid) {
					buildSolutionAndSetToTourState(resultT2, tourResultT2, tourState);
				}
			} else {
				// No result is valid, so do it with greedy.
				logger.warn("Could not handle dynamic order with US operator, applied greedy handler instead.");
				GreedyHandler gh = new GreedyHandler();
				gh.hanldeOrder(currentTourStates, newOrder, clock, structureService, networkService);
				// Exception for testing purposes
//				throw new RuntimeException("Could not handle dynamic order with US operator, applied greedy handler instead.");
			}

		}

	}

	private Location get(int value) {

		if (value == this.depotValue) {
			return this.depotLocation;
		}
		if (value == this.toStringValue) {
			return this.toStringLocation;
		}

		return ((INode) this.activities.get(value).getElement().getVRPSimulationModelStructureElementParameters().getHome()).getLocation();
	}

	private void buildSolutionAndSetToTourState(Result result, OpResult tourResult, TourState tourState) {

		List<TourActivity> sorted = new ArrayList<>();
		int startIndex = result.getStartIndex();
		if (result.isGoRigth()) {
			for (int i = 0; i < tourResult.getTour().length; i++) {
				int index = (i + startIndex) % tourResult.getTour().length;
				if (tourResult.getTour()[index] == this.toStringValue) {
					sorted.add(this.toStringActivity);
				} else {
					if (tourResult.getTour()[index] != this.depotValue) {
						sorted.add(this.activities.get(tourResult.getTour()[index]));
					}
				}
			}
		} else {
			for (int i = tourResult.getTour().length; i > 0; i--) {
				int index = (i - (tourResult.getTour().length - startIndex)) % tourResult.getTour().length;
				// modulus is not remainder
				if (index < 0) {
					index += tourResult.getTour().length;
				}
				if (tourResult.getTour()[index] == this.toStringValue) {
					sorted.add(this.toStringActivity);
				} else {
					if (tourResult.getTour()[index] != this.depotValue) {
						sorted.add(this.activities.get(tourResult.getTour()[index]));
					}
				}
			}
		}

		if (this.offset > 0) {
			sorted.add(0, this.activities.get(0));
		}

		tourState.setTourActivities(sorted);
	}

	private Result isTourValid(OpResult tourResult) {
		Result result = null;
		for (int i = 0; i < tourResult.getTour().length; i++) {
			if (tourResult.getTour()[i] == this.startValue) {
				int endIndexA = (i + 1) % tourResult.getTour().length;
				int endIndexB = (i - 1) % tourResult.getTour().length;
				// modulus is not remainder
				if (endIndexB < 0) {
					endIndexB += tourResult.getTour().length;
				}

				if (tourResult.getTour()[endIndexA] == this.depotValue) {
					result = new Result(true, i, false);
					break;
				} else if (tourResult.getTour()[endIndexB] == this.depotValue) {
					result = new Result(true, i, true);
					break;
				}
				break;
			}
		}

		if (result == null) {
			result = new Result(false, -1, false);
		}

		return result;
	}

	private double getRealDistance(Location a, Location b) {
		EuclideanDistance ed = new EuclideanDistance();
		double[] cooA = new double[] { a.getX(), a.getY() };
		double[] cooB = new double[] { b.getX(), b.getY() };
		return ed.compute(cooA, cooB);
	}

	public class Result {
		private final boolean isValid;
		private final int startIndex;
		private final boolean goRigth;

		public Result(boolean isValid, int startIndex, boolean goRigth) {
			super();
			this.isValid = isValid;
			this.startIndex = startIndex;
			this.goRigth = goRigth;
		}

		public boolean isValid() {
			return isValid;
		}

		public int getStartIndex() {
			return startIndex;
		}

		public boolean isGoRigth() {
			return goRigth;
		}

	}

}
