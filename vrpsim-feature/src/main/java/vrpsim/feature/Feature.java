package vrpsim.feature;

public interface Feature {

	/**
	 * Returns the identifier of the feature.
	 * 
	 * @return
	 */
	public FeatureIdentifier getFeatureIdentifier();
	
	/**
	 * @param vectors - list of vectors.
	 * @return
	 */
	public double[] getFeature(double[][] data);
	
	
}
