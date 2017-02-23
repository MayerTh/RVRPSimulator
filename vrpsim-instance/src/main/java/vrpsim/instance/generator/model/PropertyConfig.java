package vrpsim.instance.generator.model;

public class PropertyConfig {

	private final AbstractPropertyManager arrivalTimeManager;
	private final AbstractPropertyManager dimensionTimeManager;
	private final AbstractPropertyManager weightManager;

	public PropertyConfig(AbstractPropertyManager arrivalTimeManager, AbstractPropertyManager dimensionTimeManager, AbstractPropertyManager weightManager) {
		super();
		this.arrivalTimeManager = arrivalTimeManager;
		this.dimensionTimeManager = dimensionTimeManager;
		this.weightManager = weightManager;
	}

	public AbstractPropertyManager getArrivalTimeManager() {
		return arrivalTimeManager;
	}

	public AbstractPropertyManager getDimensionTimeManager() {
		return dimensionTimeManager;
	}

	public AbstractPropertyManager getWeightManager() {
		return weightManager;
	}

}
