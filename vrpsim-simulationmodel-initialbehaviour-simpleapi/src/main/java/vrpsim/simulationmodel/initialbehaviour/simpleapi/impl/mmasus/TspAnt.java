package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.mmasus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import isula.aco.Ant;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.us.IDistanceCalculator;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.us.OpResult;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.us.StringingT1;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.us.StringingT2;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.us.UnstringingT1;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.us.UnstringingT2;

/**
 * An specialized Ant for building solutions for the TSP problem. It is designed
 * according the algorithm described in Section 6.3 of Clever Algorithms by
 * Jason Brownlee.
 */
public class TspAnt extends Ant<Integer, TSPEnvironment> {

	private static Logger logger = LoggerFactory.getLogger(TspAnt.class);

	private static final double DELTA = Float.MIN_VALUE;
	private final int numberOfCities;
	private final int neighbourhoodSize;
	private int initialReference;

	public TspAnt(int numberOfCities, int neighbourhoodSize) {
		super();
		this.numberOfCities = numberOfCities;
		this.neighbourhoodSize = neighbourhoodSize;
		this.setSolution(new Integer[numberOfCities]);
	}

	@Override
	public void clear() {
		super.clear();
		this.initialReference = new Random().nextInt(this.numberOfCities);
	}

	/**
	 * On TSP, a solution is ready when all the cities are part of the solution.
	 *
	 * @param environment
	 *            Environment instance with problem information.
	 * @return True if the solution is ready.
	 */
	@Override
	public boolean isSolutionReady(TSPEnvironment environment) {
		return getCurrentIndex() == environment.getNumberOfCities();
	}

	/**
	 * On TSP, the cost of a solution is the total distance traversed by the
	 * salesman.
	 *
	 * @param environment
	 *            Environment instance with problem information.
	 * @return Total distance.
	 */
	@Override
	public double getSolutionCost(TSPEnvironment environment) {
		return getTotalDistance(getSolution(), environment.getProblemGraph());
	}

	/**
	 * The heuristic contribution in TSP is related to the added travel distance
	 * given by selecting an specific component. According to the algorithm on the
	 * book, when the solution is empty we take a random city as a reference.
	 *
	 * @param solutionComponent
	 *            Solution component.
	 * @param positionInSolution
	 *            Position of this component in the solution.
	 * @param environment
	 *            Environment instance with problem information.
	 * @return Heuristic contribution.
	 */
	@Override
	public Double getHeuristicValue(Integer solutionComponent, Integer positionInSolution, TSPEnvironment environment) {
		Integer lastComponent = this.initialReference;
		if (getCurrentIndex() > 0) {
			lastComponent = this.getSolution()[getCurrentIndex() - 1];
		}
		double distance = getDistance(lastComponent, solutionComponent, environment.getProblemGraph()) + DELTA;
		return 1 / distance;
	}

	/**
	 * Just retrieves a value from the pheromone matrix.
	 *
	 * @param solutionComponent
	 *            Solution component.
	 * @param positionInSolution
	 *            Position of this component in the solution.
	 * @param environment
	 *            Environment instance with problem information.
	 * @return
	 */
	@Override
	public Double getPheromoneTrailValue(Integer solutionComponent, Integer positionInSolution, TSPEnvironment environment) {

		Integer previousComponent = this.initialReference;
		if (positionInSolution > 0) {
			previousComponent = getSolution()[positionInSolution - 1];
		}

		double[][] pheromoneMatrix = environment.getPheromoneMatrix();
		return pheromoneMatrix[solutionComponent][previousComponent];
	}

	/**
	 * On TSP, the neighbourhood is given by the non-visited cities.
	 *
	 * @param environment
	 *            Environment instance with problem information.
	 * @return
	 */
	@Override
	public List<Integer> getNeighbourhood(TSPEnvironment environment) {
		List<Integer> neighbourhood = new ArrayList<>();

		for (int cityIndex = 0; cityIndex < environment.getNumberOfCities(); cityIndex += 1) {
			if (!this.isNodeVisited(cityIndex)) {
				neighbourhood.add(cityIndex);
			}
		}

		return neighbourhood;
	}

	/**
	 * Just updates the pheromone matrix.
	 *
	 * @param solutionComponent
	 *            Solution component.
	 * @param positionInSolution
	 *            Position of this component in the solution.
	 * @param environment
	 *            Environment instance with problem information.
	 * @param value
	 *            New pheromone value.
	 */
	@Override
	public void setPheromoneTrailValue(Integer solutionComponent, Integer positionInSolution, TSPEnvironment environment, Double value) {
		Integer previousComponent = this.initialReference;
		if (positionInSolution > 0) {
			previousComponent = getSolution()[positionInSolution - 1];
		}

		double[][] pheromoneMatrix = environment.getPheromoneMatrix();
		pheromoneMatrix[solutionComponent][previousComponent] = value;
		pheromoneMatrix[previousComponent][solutionComponent] = value;

	}

	/**
	 * Calculates the total distance of a route for the salesman.
	 *
	 * @param route
	 *            Route to evaluate.
	 * @param problemRepresentation
	 *            Coordinate information.
	 * @return Total distance.
	 */
	public static double getTotalDistance(Integer[] route, double[][] problemRepresentation) {
		double totalDistance = 0.0;

		for (int solutionIndex = 1; solutionIndex < route.length; solutionIndex += 1) {
			int previousSolutionIndex = solutionIndex - 1;
			totalDistance += getDistance(route[previousSolutionIndex], route[solutionIndex], problemRepresentation);
		}

		totalDistance += getDistance(route[route.length - 1], route[0], problemRepresentation);
		return totalDistance;
	}

	/**
	 * Calculates the distance between two cities.
	 *
	 * @param anIndex
	 *            Index of a city.
	 * @param anotherIndex
	 *            Index of another city.
	 * @param problemRepresentation
	 *            Coordinate information.
	 * @return Distance between these cities.
	 */
	public static double getDistance(int anIndex, int anotherIndex, double[][] problemRepresentation) {
		double[] aCoordinate = getCityCoordinates(anIndex, problemRepresentation);
		double[] anotherCoordinate = getCityCoordinates(anotherIndex, problemRepresentation);
		// return Math.round(new EuclideanDistance().compute(aCoordinate,
		// anotherCoordinate));
		return new EuclideanDistance().compute(aCoordinate, anotherCoordinate);

	}

	/**
	 * Extracts the coordinates of a city from the coordinates array.
	 *
	 * @param index
	 *            City index.
	 * @param problemRepresentation
	 *            Coordinates array.
	 * @return Coordinates as a double array.
	 */
	private static double[] getCityCoordinates(int index, double[][] problemRepresentation) {
		return new double[] { problemRepresentation[index][0], problemRepresentation[index][1] };

	}

	@Override
	public void performLocalOptimizationOnCurrentSolution(TSPEnvironment environment) {

		// Apply US operator on current solution.
		logger.debug("APPLY local optimization for ant.");
		
		IDistanceCalculator distanceCalculator = new IDistanceCalculator() {
			@Override
			public double getDistance(Integer i, Integer j) {
				return TspAnt.getDistance(i, j, environment.getProblemGraph());
			}
		};
		
		StringingT1 stringingT1 = new StringingT1();
		StringingT2 stringingT2 = new StringingT2();
		UnstringingT1 unstringingT1 = new UnstringingT1();
		UnstringingT2 unstringingT2 = new UnstringingT2();
		
		Integer[] currentSolution = this.getSolution();

		double currentCosts = this.getSolutionCost(environment);
		logger.debug("Best solution before local optimization is {}", currentCosts);
		int t = 0;
		int n = currentSolution.length;
		Integer[] best1 = currentSolution;
		while(t < n) {
			int index = t;
			int value = best1[t];
			
			OpResult resultUnstring1 = unstringingT1.performOperator(best1, index, this.neighbourhoodSize, distanceCalculator);
			OpResult resultString1 = stringingT2.performOperator(resultUnstring1.getTour(), value, this.neighbourhoodSize, distanceCalculator);
			
			index = t;
			value = resultString1.getTour()[t];
			OpResult resultUnstring2 = unstringingT2.performOperator(resultString1.getTour(), index, this.neighbourhoodSize, distanceCalculator);
			OpResult resultString2 = stringingT1.performOperator(resultUnstring2.getTour(), value, this.neighbourhoodSize, distanceCalculator);
			
			if(currentCosts > resultString2.getCosts()) {
				currentCosts = resultString2.getCosts();
				best1 = resultString2.getTour();
			} else {
				t++;
			}
			logger.trace("t is {} n is {} for ", t, n);
		}
		
		logger.debug("Best solution after local optimization is {}", currentCosts);
		this.setSolution(best1);
		
	}

}
