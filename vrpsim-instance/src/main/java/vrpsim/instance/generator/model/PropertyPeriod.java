package vrpsim.instance.generator.model;

public class PropertyPeriod {

	private final int propertyMinValue;

	private final int propertyMaxValue;

	public PropertyPeriod(int propertyMinValue, int propertyMaxValue) {
		super();
		this.propertyMinValue = propertyMinValue;
		this.propertyMaxValue = propertyMaxValue;
	}

	public int getPropertyMinValue() {
		return propertyMinValue;
	}

	public int getPropertyMaxValue() {
		return propertyMaxValue;
	}

}
