package vrpsim.dynamicvrprep.model.impl.dynamicfeature.angle;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.api.dynamicfeature.ADynamicModelFeature;
import vrpsim.feature.TSPFeatures;
import vrpsim.feature.model.TSPFeature;

public class DynamicAngleVar extends ADynamicModelFeature {

	private static Logger logger = LoggerFactory.getLogger(DynamicAngleVar.class);

	@Override
	public Map<String, Double> calculateDynamicFeature(DynamicVRPREPModel model) {
		TSPFeatures tspFeatures;
		Map<String, Double> result = new HashMap<>();
		try {
			tspFeatures = new TSPFeatures(TSPFeature.angle_coef_of_var, false);
			double value = tspFeatures.getFeature(model.getCoordinatesOfRequests(true))[0];
			result.put(TSPFeature.angle_coef_of_var.toString(), value);
//			System.out.println("DynamicAngleVar is angle_coef_of_var, value is " + value);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			logger.error("Can not calculate feature {} due to {} {}.", this.getIdentifier(), e.getCause(), e.getMessage());
		}
		return result;
	}

}
