package vrpsim.instance.test;

public class InstanceDegreeContainer implements Comparable<InstanceDegreeContainer>{

	private final double dod;
	private final double edod;
	private final double d_edod;
	private final double w_edod;
	private final int id;

	public InstanceDegreeContainer(double dod, double edod, double w_edod, double d_edod, int id) {
		super();
		this.dod = dod;
		this.edod = edod;
		this.d_edod = d_edod;
		this.w_edod = w_edod;
		this.id = id;
	}

	public double getDod() {
		return dod;
	}

	public double getEdod() {
		return edod;
	}

	public int getId() {
		return id;
	}

	public double getD_edod() {
		return d_edod;
	}

	public double getW_edod() {
		return w_edod;
	}

	public int compareTo(InstanceDegreeContainer o) {
		return Double.compare(this.w_edod, o.getW_edod());
	}

}
