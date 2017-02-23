package vrpsim.instance.dynamicvrpmodel;

public class AdditionalRequestInformation {

	private final int arrivalTime;

	private final int weight;

	private final ExtendedDimensionsType dimension;

	public AdditionalRequestInformation(int arrivalTime, int weight, ExtendedDimensionsType dimension) {
		super();
		this.arrivalTime = arrivalTime;
		this.weight = weight;
		this.dimension = dimension;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public double getWeight() {
		return weight;
	}

	public ExtendedDimensionsType getDimension() {
		return dimension;
	}

}
