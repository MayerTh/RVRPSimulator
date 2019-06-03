package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.mmasus;

import isula.aco.AcoProblemSolver;
import isula.aco.Ant;
import isula.aco.algorithms.maxmin.MaxMinConfigurationProvider;
import isula.aco.algorithms.maxmin.UpdatePheromoneMatrixForMaxMin;

public class TspUpdatePheromoneMatrixForMaxMin extends UpdatePheromoneMatrixForMaxMin<Integer, TSPEnvironment> {

	private final AcoProblemSolver<Integer, TSPEnvironment> solver;

	public TspUpdatePheromoneMatrixForMaxMin(AcoProblemSolver<Integer, TSPEnvironment> solver) {
		this.solver = solver;
	}

	@Override
	protected double getNewPheromoneValue(Ant<Integer, TSPEnvironment> ant, int positionInSolution, Integer solutionComponent,
			MaxMinConfigurationProvider configurationProvider) {
		Double contribution = 1 / ant.getSolutionCost(getEnvironment());
		return ant.getPheromoneTrailValue(solutionComponent, positionInSolution, getEnvironment()) + contribution;
	}

	@Override
	protected double getMinimumPheromoneValue(MaxMinConfigurationProvider configurationProvider) {
		return getMaximumPheromoneValue(configurationProvider) / configurationProvider.getAForCalculatingMinimumPheromoneValue();
	}

	@Override
	protected double getMaximumPheromoneValue(MaxMinConfigurationProvider configurationProvider) {
		// No update of the solution of the solver is necessary (done in the solver main loop)
		double currentBest = this.solver.getBestSolutionCost();
		if(currentBest <= 0.0) {
			currentBest = configurationProvider.getEstimatedOptimalTour();
		}
		return 1 / (configurationProvider.getEvaporationRatio() * currentBest);
	}

}
