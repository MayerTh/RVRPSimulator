package vrpsim.simulationmodel.dynamicbehaviour.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.impl.Location;
import vrpsim.core.model.network.impl.NetworkService;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.simulator.IClock;
import vrpsim.simulationmodel.dynamicbehaviour.DynamicHandlerException;
import vrpsim.simulationmodel.dynamicbehaviour.IDynamicBehaviourProviderHandler;
import vrpsim.simulationmodel.dynamicbehaviour.impl.twooptutil.FLS;
import vrpsim.simulationmodel.dynamicbehaviour.impl.twooptutil.Point;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourActivity;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourState;

public class TwoOptDynamic implements IDynamicBehaviourProviderHandler {

	private static Logger logger = LoggerFactory.getLogger(TwoOptDynamic.class);

	private final Random random;

	public TwoOptDynamic(long seed) {
		this.random = new Random(seed);
	}

	@Override
	public void hanldeOrder(List<TourState> currentTourStates, TourActivity newOrder, IClock clock, StructureService structureService,
			NetworkService networkService) throws DynamicHandlerException {
		// Dynamic Problem

		if (allEmpty(currentTourStates)) {
			currentTourStates.get(0).getTourActivities().add(newOrder);
		} else {

			TourState tourState = currentTourStates.get(0);
			if (tourState.getTourActivities().size() >= 2) {
				// Add the new order at the 2. last place in the tour.
				tourState.getTourActivities().add(tourState.getTourActivities().size() - 2, newOrder);
			} else {
				if (tourState.isAddAtFirstPlaceAllowed()) {
					tourState.getTourActivities().add(tourState.getTourActivities().size() - 1, newOrder);
				} else {
					tourState.getTourActivities().add(tourState.getTourActivities().size() - 0, newOrder);
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Tour before TwoOptTSPMain: {}", print(tourState.getTourActivities()));
			}

			HashMap<String, TourActivity> backTourActivities = new HashMap<>();

			int start = tourState.isAddAtFirstPlaceAllowed() ? 0 : 1;
			Point[] points = new Point[tourState.getTourActivities().size() - start + 1];
			for (int i = start; i < tourState.getTourActivities().size(); i++) {
				TourActivity ta = tourState.getTourActivities().get(i);
				INode node = (INode) ta.getElement().getVRPSimulationModelStructureElementParameters().getHome();
				boolean isStart = i == start;
				Point p = new Point(node.getLocation().getX(), node.getLocation().getY(),
						node.getVRPSimulationModelElementParameters().getId(), true, isStart, false);
				points[i - start] = p;
				backTourActivities.put(node.getVRPSimulationModelElementParameters().getId(), ta);
			}

			// Add the depot.
			Location depotLocation = ((INode) tourState.getVehicle().getVRPSimulationModelStructureElementParameters().getHome())
					.getLocation();
			points[points.length - 1] = new Point(depotLocation.getX(), depotLocation.getY(), "DEPOT", true, false, true);

			logger.debug("Points before TwoOptTSPMain: {}", print(points));

			boolean tourIsValid = false;
			int counter = 0;
			Result result = null;
			while (!tourIsValid) {
				final FLS fls = new FLS();
				final double cost = fls.optimise(points);
				logger.debug("TwoOptTSPSolver result = {}", cost);
				result = isTourValid(points);
				tourIsValid = result.isValid();
				if (!tourIsValid) {
					logger.debug("Points not valid before shuffeling: {}", print(points));
					shuffleAndResetActive(points, random);
				}
				if (counter++ > 10) {
					break;
				}
			}

			logger.debug("Points after TwoOptTSPMain: {}", print(points));
			logger.debug("Start index is {}, turn rigth is {}", result.getStartIndex(), result.isGoRigth());

			if (result.isValid()) {
				List<TourActivity> sorted = new ArrayList<>();
				int startIndex = result.getStartIndex();
				if (result.isGoRigth()) {
					for (int i = 0; i < points.length; i++) {
						int index = (i + startIndex) % points.length;
						if (!points[index].isDepot()) {
							sorted.add(backTourActivities.get(points[index].getId()));
						}
					}
				} else {
					for (int i = points.length; i > 0; i--) {
						int index = (i - (points.length - startIndex)) % points.length;
						// modulus is not remainder
						if (index < 0) {
							index += points.length;
						}
						if (!points[index].isDepot()) {
							sorted.add(backTourActivities.get(points[index].getId()));
						}
					}
				}

				if (!tourState.isAddAtFirstPlaceAllowed()) {
					sorted.add(0, tourState.getTourActivities().get(0));
				}

				tourState.setTourActivities(sorted);
				logger.debug("Tour after TwoOptTSPMain: {}", print(tourState.getTourActivities()));
			} else {
				logger.warn("Could not handle dynamic order with 2-Opt approach, applied greedy handler instead.");
				GreedyHandler gh = new GreedyHandler();
				gh.hanldeOrder(currentTourStates, newOrder, clock, structureService, networkService);
				// Exception for testing purposes
				// throw new RuntimeException("Could not handle dynamic order with 2-Opt
				// approach, applied greedy handler instead.");
			}
		}

	}

	private void shuffleAndResetActive(Point[] points, Random random) {
		int index;
		Point temp;
		for (int i = points.length - 1; i > 0; i--) {
			index = random.nextInt(i + 1);
			temp = points[index];
			points[index] = points[i];
			points[i] = temp;
		}
		for (Point p : points) {
			p.setActive(true);
		}
	}

	private String print(Point[] points) {
		String s = "";
		for (Point p : points) {
			s += p.getId();
			if (p.isStart()) {
				s += " (START)";
			}
			if (p.isDepot()) {
				s += " (DEPOT)";
			}
			s += " -> ";
		}
		return s;
	}

	private Result isTourValid(Point[] points) {
		Result result = null;
		for (int i = 0; i < points.length; i++) {
			if (points[i].isStart()) {
				int endIndexA = (i + 1) % points.length;
				int endIndexB = (i - 1) % points.length;
				// modulus is not remainder
				if (endIndexB < 0) {
					endIndexB += points.length;
				}
				if (points[endIndexA].isDepot()) {
					result = new Result(true, i, false);
					break;
				} else if (points[endIndexB].isDepot()) {
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

	private boolean allEmpty(List<TourState> currentTourStates) {
		boolean allEmpty = true;
		for (TourState tourState : currentTourStates) {
			if (!tourState.getTourActivities().isEmpty()) {
				allEmpty = false;
				break;
			}
		}
		return allEmpty;
	}

	private String print(List<TourActivity> tour) {
		String s = "";
		for (TourActivity ta : tour) {
			s += ta.getElement().getVRPSimulationModelStructureElementParameters().getHome().getVRPSimulationModelElementParameters()
					.getId();
			s += " -> ";
		}
		return s;
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
