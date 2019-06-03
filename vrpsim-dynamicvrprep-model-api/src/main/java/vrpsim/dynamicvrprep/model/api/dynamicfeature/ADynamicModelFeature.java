package vrpsim.dynamicvrprep.model.api.dynamicfeature;

import java.util.Map;

public abstract class ADynamicModelFeature implements IDynamicModelFeature {

	@Override
	public String getIdentifier() {
		return this.getClass().getSimpleName();
	}

	@Override
	public double getFirstValue(Map<String, Double> features) {
		return (double) features.values().toArray()[0];
	}

}
