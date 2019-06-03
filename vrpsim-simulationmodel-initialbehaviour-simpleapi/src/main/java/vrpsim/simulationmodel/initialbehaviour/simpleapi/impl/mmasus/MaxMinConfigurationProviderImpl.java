package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.mmasus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import isula.aco.algorithms.maxmin.MaxMinConfigurationProvider;

public class MaxMinConfigurationProviderImpl implements MaxMinConfigurationProvider {

	private final double evaporationRatio;
	private final double heuristicImportance;
	private final double initialPheromoneValue;
	private final int numberOfAnts;
	private final int numberOfIterations;
	private final double pheromoneImportance;
	private final double maximumPheromoneValue;
	private final double minimumPheromoneValue;
	private final double aForCalculatingMinimumPheromoneValue;
	private final double estimatedOptimalTour;
	private final int numberOfIterationsBeforeReset;
	private final int numberOfIterationsForAbort;
	private final int neighbourhoodSize;
	private final boolean applyLocalOptimizationWithLastAnt;
	private final boolean applyLocalOptimizationWithEveryBestAnt;

	public MaxMinConfigurationProviderImpl(double[][] problemGraph, double evaporationRatio, double heuristicImportance,
			double initialPheromoneValue, int numberOfAnts, int numberOfIterations, double pheromoneImportance,
			double maximumPheromoneValue, double minimumPheromoneValue, double aForCalculatingMinimumPheromoneValue,
			double estimatedOptimalTour, int numberOfIterationsBeforeReset, int numberOfIterationsForAbort, int neighbourhoodSize,
			boolean applyLocalOptimizationWithLastAnt, boolean applyLocalOptimizationWithEveryBestAnt) {

		super();
		this.evaporationRatio = evaporationRatio;
		this.heuristicImportance = heuristicImportance;
		// this.initialPheromoneValue = initialPheromoneValue;
		this.numberOfAnts = numberOfAnts;
		this.numberOfIterations = numberOfIterations;
		this.pheromoneImportance = pheromoneImportance;
		// this.maximumPheromoneValue = maximumPheromoneValue;
		this.minimumPheromoneValue = minimumPheromoneValue;
		this.aForCalculatingMinimumPheromoneValue = aForCalculatingMinimumPheromoneValue;
		this.estimatedOptimalTour = estimatedOptimalTour;
		this.numberOfIterationsBeforeReset = numberOfIterationsBeforeReset;
		this.numberOfIterationsForAbort = numberOfIterationsForAbort;
		this.neighbourhoodSize = neighbourhoodSize;
		this.applyLocalOptimizationWithLastAnt = applyLocalOptimizationWithLastAnt;
		this.applyLocalOptimizationWithEveryBestAnt = applyLocalOptimizationWithEveryBestAnt;
		this.maximumPheromoneValue = 1 / (this.evaporationRatio * estimatedOptimalTour);

		List<Integer> randomSolution = new ArrayList<>();
		int numberOfCities = problemGraph.length;
		for (int cityIndex = 0; cityIndex < numberOfCities; cityIndex += 1) {
			randomSolution.add(cityIndex);
		}
		Collections.shuffle(randomSolution);
		double randomQuality = TspAnt.getTotalDistance(randomSolution.toArray(new Integer[randomSolution.size()]), problemGraph);
		this.initialPheromoneValue = 1.0 / (numberOfCities * randomQuality);

	}

	@Override
	public int getNumberOfIterationsForAbort() {
		return numberOfIterationsForAbort;
	}

	@Override
	public double getEvaporationRatio() {
		return this.evaporationRatio;
	}

	@Override
	public double getHeuristicImportance() {
		return this.heuristicImportance;
	}

	@Override
	public double getInitialPheromoneValue() {
		return this.initialPheromoneValue;
	}

	@Override
	public int getNumberOfAnts() {
		return this.numberOfAnts;
	}

	@Override
	public int getNumberOfIterations() {
		return this.numberOfIterations;
	}

	@Override
	public double getPheromoneImportance() {
		return this.pheromoneImportance;
	}

	@Override
	public double getMaximumPheromoneValue() {
		return this.maximumPheromoneValue;
	}

	@Override
	public double getMinimumPheromoneValue() {
		return this.minimumPheromoneValue;
	}

	@Override
	public double getAForCalculatingMinimumPheromoneValue() {
		return this.aForCalculatingMinimumPheromoneValue;
	}

	@Override
	public double getEstimatedOptimalTour() {
		return this.estimatedOptimalTour;
	}

	@Override
	public int getNumberOfIterationsBeforeReset() {
		return this.numberOfIterationsBeforeReset;
	}

	@Override
	public int getNeighbourhoodSize() {
		return this.neighbourhoodSize;
	}

	@Override
	public boolean applyLocalOptimizationWithLastAnt() {
		return this.applyLocalOptimizationWithLastAnt;
	}

	@Override
	public boolean applyLocalOptimizationWithEveryBestAnt() {
		return this.applyLocalOptimizationWithEveryBestAnt;
	}

}
