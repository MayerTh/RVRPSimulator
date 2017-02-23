package vrpsim.instance.dynamicvrpmodel;

import org.vrprep.model.instance.DimensionsType;

public class ExtendedDimensionsType extends DimensionsType {

	private final int volume;
	
	public ExtendedDimensionsType(double depth, double height, double width) {
		super();
		this.depth = depth;
		this.height = height;
		this.width = width;
		this.volume = new Double(this.depth * this.height * this.width).intValue();
	}
	
	public ExtendedDimensionsType(int volume) {
		super();
		this.volume = volume;
	}

	public int getVolumeForDimension() {
		return this.volume;
	}

}
