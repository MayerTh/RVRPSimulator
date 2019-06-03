package vrpsim.simulationmodel.dynamicbehaviour.impl.usutil;

public interface IUnstringingOperator {

	/**
	 * In the tour are the ids/indexes from the nodes/points. The unstringIndex
	 * represents an index from the tour.
	 * 
	 * @param tour
	 * @param unstringIndex
	 * @param distanceCalculator
	 * @param neighbourhoodSize
	 * @return
	 */
	OpResult performOperator(Integer[] tour, int unstringIndex, int neighbourhoodSize, IDistanceCalculator distanceCalculator);

}
