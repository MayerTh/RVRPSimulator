package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.us;

public class OpResult {

	final Integer[] tour;
	final double costs;

	public OpResult(Integer[] tour, double costs) {
		super();
		this.tour = tour;
		this.costs = costs;
	}

	public Integer[] getTour() {
		return tour;
	}

	public double getCosts() {
		return costs;
	}

}
