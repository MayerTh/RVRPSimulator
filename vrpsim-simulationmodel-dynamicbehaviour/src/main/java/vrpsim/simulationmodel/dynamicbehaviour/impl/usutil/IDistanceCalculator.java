package vrpsim.simulationmodel.dynamicbehaviour.impl.usutil;

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
