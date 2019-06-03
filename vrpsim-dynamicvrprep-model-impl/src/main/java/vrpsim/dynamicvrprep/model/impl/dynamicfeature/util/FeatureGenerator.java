package vrpsim.dynamicvrprep.model.impl.dynamicfeature.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.api.dynamicfeature.IDynamicModelFeature;
import vrpsim.dynamicvrprep.model.api.dynamicfeature.IFeatureGenerator;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.angle.DynamicAngleMean;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.angle.DynamicAngleVar;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.angle.OWN_DynamicToStaticNNearestNeighborsAngleMax;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.angle.OWN_DynamicToStaticNNearestNeighborsAngleMean;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.angle.OWN_DynamicToStaticNNearestNeighborsAngleMedian;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.angle.OWN_DynamicToStaticNNearestNeighborsAngleSum;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.area.OWN_RatioCoveredArea;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.centroid.OWN_RatioMedianDistanceToCentroid;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.centroid.OWN_RatioSumDistanceToCentroid;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.cluster.DynamicClusterDistMean;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.cluster.DynamicClusterNumber;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.distance.DynamicDistanceMean;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.distance.DynamicDistanceVar;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.location.OWN_RatioDepotLDOD;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.location.OWN_RatioLDOD;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.mst.DynamicMSTDistanceMean;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.mst.DynamicMSTDistanceVar;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.nnds.DynamicNNDDistanceMean;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.nnds.DynamicNNDDistanceVar;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.nnds.OWN_DynamicToStaticNNearestNeighborsDistanceMax;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.nnds.OWN_DynamicToStaticNNearestNeighborsDistanceMean;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.nnds.OWN_DynamicToStaticNNearestNeighborsDistanceMedian;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.nnds.OWN_DynamicToStaticNNearestNeighborsDistanceSum;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.number.DOD;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.time.EDOD;

public class FeatureGenerator implements IFeatureGenerator {

	private List<IDynamicModelFeature> features = new ArrayList<>();

	public FeatureGenerator() {
		
		features.add(new DOD());
		features.add(new EDOD());
		
		features.add(new OWN_RatioLDOD());
		features.add(new OWN_RatioDepotLDOD());
		features.add(new OWN_RatioCoveredArea());
		features.add(new OWN_RatioMedianDistanceToCentroid());
		features.add(new OWN_RatioSumDistanceToCentroid());
		
		features.add(new DynamicAngleMean());
		features.add(new DynamicAngleVar());
		features.add(new DynamicMSTDistanceMean());
		features.add(new DynamicMSTDistanceVar());
		features.add(new DynamicNNDDistanceMean());
		features.add(new DynamicNNDDistanceVar());
//		features.add(new DynamicDistanceDistinct());
		features.add(new DynamicDistanceMean());
		features.add(new DynamicDistanceVar());
		features.add(new DynamicClusterNumber());
		features.add(new DynamicClusterDistMean());

		features.add(new OWN_DynamicToStaticNNearestNeighborsAngleMax(2));
		features.add(new OWN_DynamicToStaticNNearestNeighborsAngleMean(2));
		features.add(new OWN_DynamicToStaticNNearestNeighborsAngleMedian(2));
		features.add(new OWN_DynamicToStaticNNearestNeighborsAngleSum(2));
		
		features.add(new OWN_DynamicToStaticNNearestNeighborsAngleMax(3));
		features.add(new OWN_DynamicToStaticNNearestNeighborsAngleMean(3));
		features.add(new OWN_DynamicToStaticNNearestNeighborsAngleMedian(3));
		features.add(new OWN_DynamicToStaticNNearestNeighborsAngleSum(3));
		
		features.add(new OWN_DynamicToStaticNNearestNeighborsAngleMax(5));
		features.add(new OWN_DynamicToStaticNNearestNeighborsAngleMean(5));
		features.add(new OWN_DynamicToStaticNNearestNeighborsAngleMedian(5));
		features.add(new OWN_DynamicToStaticNNearestNeighborsAngleSum(5));
		
		features.add(new OWN_DynamicToStaticNNearestNeighborsDistanceMax(1));
		features.add(new OWN_DynamicToStaticNNearestNeighborsDistanceMean(1));
		features.add(new OWN_DynamicToStaticNNearestNeighborsDistanceMedian(1));
		features.add(new OWN_DynamicToStaticNNearestNeighborsDistanceSum(1));
		
		features.add(new OWN_DynamicToStaticNNearestNeighborsDistanceMax(2));
		features.add(new OWN_DynamicToStaticNNearestNeighborsDistanceMean(2));
		features.add(new OWN_DynamicToStaticNNearestNeighborsDistanceMedian(2));
		features.add(new OWN_DynamicToStaticNNearestNeighborsDistanceSum(2));
		
		features.add(new OWN_DynamicToStaticNNearestNeighborsDistanceMax(5));
		features.add(new OWN_DynamicToStaticNNearestNeighborsDistanceMean(5));
		features.add(new OWN_DynamicToStaticNNearestNeighborsDistanceMedian(5));
		features.add(new OWN_DynamicToStaticNNearestNeighborsDistanceSum(5));
		
	}
	
	@Override
	public Map<String, Double> getFeatures(DynamicVRPREPModel model) {
		Map<String, Double> result = new HashMap<>();
		for (int i = 0; i < features.size(); i++) {
			result.putAll(features.get(i).calculateDynamicFeature(model));
		}
		return result;
	}

}
