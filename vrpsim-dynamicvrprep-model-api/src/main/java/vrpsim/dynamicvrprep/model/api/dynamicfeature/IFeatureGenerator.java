package vrpsim.dynamicvrprep.model.api.dynamicfeature;

import java.util.Arrays;
import java.util.Map;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;

public interface IFeatureGenerator {
	
	Map<String, Double> getFeatures(DynamicVRPREPModel model);
	
	public static String[] getIdentifiersInOrder(Map<String, Double> features) {
		String[] identifiers = new String[features.size()];
		int index = 0;
		for(String str : features.keySet()) {
			identifiers[index] = str;
			index++;
		}
		Arrays.sort(identifiers);
		return identifiers;
	}
	
	public static double[] getValuesForInOrder(Map<String, Double> features, String[] identifiers) {
		double[] values = new double[identifiers.length];
		int index = 0;
		for(String key : identifiers) {
			Double v = features.get(key);
			values[index] = v == null ? Double.NaN : v;
			index++;
		}
		return values;
	}
	
}
