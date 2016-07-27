package vrpsim.examples.dynamicvrp.msa.algorithms.model;

public class Stop {

	private Stop next;
	private Stop pre;

	private final String id;
	private final double demand;
	private final double edd;
	private final double ldd;

	public Stop(String id, double demand, double edd, double ldd) {
		this.id = id;
		this.demand = demand;
		this.edd = edd;
		this.ldd = ldd;
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

	public boolean hasNext() {
		return next != null;
	}

	public String getId() {
		return id;
	}

	public Stop getNext() {
		return next;
	}

	public void setNext(Stop next) {
		this.next = next;
	}

	public Stop getPre() {
		return pre;
	}

	public void setPre(Stop pre) {
		this.pre = pre;
	}

}
