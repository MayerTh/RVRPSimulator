package vrpsim.feature;

public enum FeatureIdentifier {

	TSPMETA("TSPMETA", "All features privided from R package tspmeta, function fueature(tsp_instance)");

	private final String name;
	private final String description;

	/**
	 * @param name
	 * @param description
	 */
	private FeatureIdentifier(final String name, final String description) {
		this.name = name;
		this.description = description;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

}
