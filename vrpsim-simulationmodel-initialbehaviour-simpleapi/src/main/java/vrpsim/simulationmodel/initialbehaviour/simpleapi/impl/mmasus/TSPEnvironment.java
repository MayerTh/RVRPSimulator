package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.mmasus;

import isula.aco.Environment;
import isula.aco.exception.InvalidInputException;

public class TSPEnvironment extends Environment {


	public TSPEnvironment(double[][] problemGraph) throws InvalidInputException {
		super(problemGraph);
	}

	public int getNumberOfCities() {
        return getProblemGraph().length;
    }
	
	@Override
	protected double[][] createPheromoneMatrix() {
		int numberOfCities = getNumberOfCities();
        return new double[numberOfCities][numberOfCities];
	}

}
