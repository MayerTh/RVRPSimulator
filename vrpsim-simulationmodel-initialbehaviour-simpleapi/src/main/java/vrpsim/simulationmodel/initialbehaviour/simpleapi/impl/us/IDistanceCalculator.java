package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.us;

public interface IDistanceCalculator {

	/**
	 * i and j represent ids from the tour see {@link IUnstringingOperator}.
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	double getDistance(Integer i, Integer j);
	
}
