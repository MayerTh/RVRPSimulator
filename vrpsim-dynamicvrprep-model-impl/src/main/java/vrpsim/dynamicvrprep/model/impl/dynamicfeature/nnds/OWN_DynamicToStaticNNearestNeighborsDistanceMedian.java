package vrpsim.dynamicvrprep.model.impl.dynamicfeature.nnds;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel.RequestType;
import vrpsim.dynamicvrprep.model.api.dynamicfeature.ADynamicModelFeature;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.util.DynamicModelDegreeCalculationHelper;

public class OWN_DynamicToStaticNNearestNeighborsDistanceMedian extends ADynamicModelFeature {

	private static Logger logger = LoggerFactory.getLogger(OWN_DynamicToStaticNNearestNeighborsDistanceMedian.class);
	private final int numberNeighbors;
	
	@Override
	public String getIdentifier() {
		return this.numberNeighbors + "NNDistanceMedian";
	}

	public OWN_DynamicToStaticNNearestNeighborsDistanceMedian(int numberNeighbors) {
		this.numberNeighbors = numberNeighbors;
	}

	@Override
	public Map<String, Double> calculateDynamicFeature(DynamicVRPREPModel model) {

		double[][] staticPoints = model.getCoordinatesOfRequestsByType(RequestType.ONLY_STATIC);
		double[][] dynamicPoints = model.getCoordinatesOfRequestsByType(RequestType.ONLY_DYNAMIC);

		int index = 0;
		double[] sums = new double[dynamicPoints.length];
		for (double[] dynamicPoint : dynamicPoints) {
			double x1 = dynamicPoint[0];
			double y1 = dynamicPoint[1];
			double[] distances = DynamicModelDegreeCalculationHelper.getDistancesFromNNearestNeighbors(x1, y1, staticPoints,
					this.numberNeighbors);
			double sumDistances = StatUtils.percentile(distances, 50);
			sums[index++] = sumDistances;
		}

		Map<String, Double> result = new HashMap<>();
		result.put(this.getIdentifier() + "_max", StatUtils.max(sums));
		result.put(this.getIdentifier() + "_sum", StatUtils.sum(sums));
		double mean = StatUtils.mean(sums);
		result.put(this.getIdentifier() + "_mean", mean);
		result.put(this.getIdentifier() + "_median", StatUtils.percentile(sums, 50));
		double std = FastMath.sqrt(StatUtils.variance(sums));
		result.put(this.getIdentifier() + "_std", std);
		result.put(this.getIdentifier() + "_var", StatUtils.variance(sums));
		result.put(this.getIdentifier() + "_var_cof", (std / mean));
		return result;
	}

}
