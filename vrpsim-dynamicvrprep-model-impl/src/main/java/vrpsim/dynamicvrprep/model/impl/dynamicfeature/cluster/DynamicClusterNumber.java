package vrpsim.dynamicvrprep.model.impl.dynamicfeature.cluster;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.api.dynamicfeature.ADynamicModelFeature;
import vrpsim.feature.TSPFeatures;
import vrpsim.feature.model.TSPFeature;

public class DynamicClusterNumber extends ADynamicModelFeature {

	private static Logger logger = LoggerFactory.getLogger(DynamicClusterNumber.class);

	@Override
	public Map<String, Double> calculateDynamicFeature(DynamicVRPREPModel model) {
		TSPFeatures tspFeatures;
		Map<String, Double> result = new HashMap<>();
		try {
			tspFeatures = new TSPFeatures(TSPFeature.cluster_05pct_number_of_clusters, false);
			result.put(TSPFeature.cluster_05pct_number_of_clusters.toString(),
					tspFeatures.getFeature(model.getCoordinatesOfRequests(true))[0]);
			return result;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			logger.error("Can not calculate feature {} due to {} {}.", this.getIdentifier(), e.getCause(), e.getMessage());
		}
		result.put(TSPFeature.cluster_05pct_number_of_clusters.toString(), Double.NaN);
		return result;
	}

}
