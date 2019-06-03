package vrpsim.simulationmodel.dynamicbehaviour.impl.usutil;

public interface IStringingOperator {

	/**
	 * In the tour are the ids/indexes from the nodes/points. The toString
	 * represents the value have to inserted.
	 * 
	 * @param tour
	 * @param toString
	 * @param distanceCalculator
	 * @param neighbourhoodSize
	 * @return
	 */
	OpResult performOperator(Integer[] tour, Integer toString, int neighbourhoodSize, IDistanceCalculator distanceCalculator);

}
