package vrpsim.dynamicvrprep.model.impl.dynamicfeature.angle;

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

public class OWN_DynamicToStaticNNearestNeighborsAngleMedian extends ADynamicModelFeature {

	private static Logger logger = LoggerFactory.getLogger(OWN_DynamicToStaticNNearestNeighborsAngleMedian.class);
	private final int numberNeighbors;

	@Override
	public String getIdentifier() {
		return this.numberNeighbors + "NNAngleMedian";
	}

	/**
	 * Minimum of neighbors is 2.
	 * 
	 * @param numberNeighbors
	 */
	public OWN_DynamicToStaticNNearestNeighborsAngleMedian(int numberNeighbors) {
		this.numberNeighbors = numberNeighbors < 2 ? 2 : numberNeighbors;
	}

	@Override
	public Map<String, Double> calculateDynamicFeature(DynamicVRPREPModel model) {

		double[][] staticPoints = model.getCoordinatesOfRequestsByType(RequestType.ONLY_STATIC);
		double[][] dynamicPoints = model.getCoordinatesOfRequestsByType(RequestType.ONLY_DYNAMIC);

		int index = 0;
		double[] values = new double[dynamicPoints.length];
		for (double[] dynamicPoint : dynamicPoints) {
			double x1 = dynamicPoint[0];
			double y1 = dynamicPoint[1];
			double[][] neighbors = DynamicModelDegreeCalculationHelper.getNNearestNeighbors(x1, y1, staticPoints, this.numberNeighbors);

			double[] angles = new double[(int) Math.pow(neighbors.length, 2) - neighbors.length];
			int angleIndex = 0;
			for (double[] n1 : neighbors) {

				double n1x = n1[0];
				double n1y = n1[1];

				for (double[] n2 : neighbors) {

					double n2x = n2[0];
					double n2y = n2[1];

					if (!(n1x == n2x && n1y == n2y)) {
						double angle = DynamicModelDegreeCalculationHelper.calculateAngle(x1, y1, n1x, n1y, n2x, n2y);
						angles[angleIndex++] = angle;
					}

				}

			}

			double v =  StatUtils.percentile(angles, 50);
			values[index++] = v;
		}

		Map<String, Double> result = new HashMap<>();
		result.put(this.getIdentifier() + "_max", StatUtils.max(values));
		result.put(this.getIdentifier() + "_sum", StatUtils.sum(values));
		double mean = StatUtils.mean(values);
		result.put(this.getIdentifier() + "_mean", mean);
		result.put(this.getIdentifier() + "_median", StatUtils.percentile(values, 50));
		double std = FastMath.sqrt(StatUtils.variance(values));
		result.put(this.getIdentifier() + "_std", std);
		result.put(this.getIdentifier() + "_var", StatUtils.variance(values));
		result.put(this.getIdentifier() + "_var_cof", (std / mean));
		return result;
	}

}
