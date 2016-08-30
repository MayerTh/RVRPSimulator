package vrpsim.examples.dynamicvrp.msa.algorithms.model;

public class Stop {

	private final String id;
	private final double demand;
	private final double edd;
	private final double ldd;
	private final double serviceTime;

	public Stop(String id, double demand, double edd, double ldd, double serviceTime) {
		this.id = id;
		this.demand = demand;
		this.edd = edd;
		this.ldd = ldd;
		this.serviceTime = serviceTime;
	}

	public double getServiceTime() {
		return serviceTime;
	}

	public double getDemand() {
		return demand;
	}

	public double getEdd() {
		return edd;
	}

	public double getLdd() {
		return ldd;
	}

	public String getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.id.equals(((Stop)obj).getId());
	}
	
	@Override
	public String toString() {
		return "CustId = " + this.id;
	}

}
