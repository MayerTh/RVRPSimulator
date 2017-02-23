package vrpsim.instance.generator.model;

public class PropertyClass {

	private final PropertyPeriod period;

	private final double probability;

	public PropertyClass(PropertyPeriod period, double probability) {
		super();
		this.period = period;
		this.probability = probability;
	}

	public PropertyPeriod getPeriod() {
		return period;
	}

	public double getProbability() {
		return probability;
	}

}
