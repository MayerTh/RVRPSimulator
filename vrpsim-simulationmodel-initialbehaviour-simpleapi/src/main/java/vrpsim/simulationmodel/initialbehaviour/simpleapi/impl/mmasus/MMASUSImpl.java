package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.mmasus;

import java.util.Random;

import javax.naming.ConfigurationException;

import isula.aco.AcoProblemSolver;
import isula.aco.Ant;
import isula.aco.AntColony;
import isula.aco.ConfigurationProvider;
import isula.aco.algorithms.antsystem.RandomNodeSelection;
import isula.aco.algorithms.maxmin.StartPheromoneMatrixForMaxMin;
import isula.aco.exception.InvalidInputException;

public class MMASUSImpl {

	// Alpha
	private final double pheromoneImportance = 1;
	// Beta
	private final double heuristicImportance = 5;
	private final int numberOfAnts = 50;
	private final int numberOfIterations = 300;
	private final double evaporationRatio = 0.2;
	private double estimatedOptimalTour;

	// Nicht im paper angegeben
	private final double aForCalculatingMinimumPheromoneValue = 3;
	private final int numberOfIterationsBeforeReset = 80;
	private final int numberOfIterationsForAbort = 200;
	private final int neighbourhoodSize = 4;

	private final boolean applyLocalOptimizationWithLastAnt;
	private final boolean applyLocalOptimizationForEveryBestAnt;

	private final double maximumPheromoneValue = Double.NaN;
	private final double minimumPheromoneValue = Double.NaN;
	private final double initialPheromoneValue = Double.NaN;
	
	public MMASUSImpl(boolean applyLocalOptimizationForEveryBestAnt) {
		this.applyLocalOptimizationForEveryBestAnt = applyLocalOptimizationForEveryBestAnt;
		this.applyLocalOptimizationWithLastAnt = !this.applyLocalOptimizationForEveryBestAnt;
	}

	public Integer[] impl(double[][] problemGraph, double estimatedOptimalTour) throws InvalidInputException, ConfigurationException {

		this.estimatedOptimalTour = estimatedOptimalTour;

		ConfigurationProvider configurationProvider = new MaxMinConfigurationProviderImpl(problemGraph, evaporationRatio,
				heuristicImportance, initialPheromoneValue, numberOfAnts, numberOfIterations, pheromoneImportance, maximumPheromoneValue,
				minimumPheromoneValue, aForCalculatingMinimumPheromoneValue, this.estimatedOptimalTour, numberOfIterationsBeforeReset,
				numberOfIterationsForAbort, neighbourhoodSize, applyLocalOptimizationWithLastAnt, applyLocalOptimizationForEveryBestAnt);
		TSPEnvironment tspEnvironment = new TSPEnvironment(problemGraph);
		// tspEnvironment.populatePheromoneMatrix(configurationProvider.getInitialPheromoneValue());
		AntColony<Integer, TSPEnvironment> antColony = getAntColony(configurationProvider);

		AcoProblemSolver<Integer, TSPEnvironment> solver = new AcoProblemSolver<>();
		solver.initialize(tspEnvironment, antColony, configurationProvider);

		TspUpdatePheromoneMatrixForMaxMin updatePM = new TspUpdatePheromoneMatrixForMaxMin(solver);
		solver.addDaemonActions(new StartPheromoneMatrixForMaxMin<Integer, TSPEnvironment>(), updatePM);
		antColony.addAntPolicies(new RandomNodeSelection<>());
		// antColony.addAntPolicies(new MaximumNodeSelection());

		solver.solveProblem();
		// visualize(tspEnvironment.getPheromoneMatrix());

		return solver.getBestSolution();
	}

	public static void visualize(double[][] matrix) {

		int rows = matrix.length;
		int columns = matrix[0].length;

		for (int i = 0; i < rows; i++) {
			String s = "";
			for (int j = 0; j < columns; j++) {
				s += matrix[i][j] + " ";
			}
			System.out.println(s);
		}

	}

	/**
	 * Produces an Ant Colony instance for the TSP problem.
	 *
	 * @param configurationProvider
	 *            Algorithm configuration.^
	 * @return Ant Colony instance.
	 */
	private AntColony<Integer, TSPEnvironment> getAntColony(final ConfigurationProvider configurationProvider) {
		return new AntColony<Integer, TSPEnvironment>(configurationProvider.getNumberOfAnts()) {
			@Override
			protected Ant<Integer, TSPEnvironment> createAnt(TSPEnvironment environment) {
				int initialReference = new Random().nextInt(environment.getNumberOfCities());
				return new TspAnt(environment.getNumberOfCities(), configurationProvider.getNeighbourhoodSize());
			}
		};
	}

}
