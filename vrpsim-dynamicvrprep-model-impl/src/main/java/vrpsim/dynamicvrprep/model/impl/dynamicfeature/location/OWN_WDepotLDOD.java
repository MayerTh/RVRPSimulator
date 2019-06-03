package vrpsim.dynamicvrprep.model.impl.dynamicfeature.location;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.vrprep.model.instance.Instance.Fleet.VehicleProfile;
import org.vrprep.model.instance.Instance.Requests.Request;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.api.dynamicfeature.ADynamicModelFeature;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.util.DynamicModelDegreeCalculationHelper;

public class OWN_WDepotLDOD extends ADynamicModelFeature {

	@Override
	public Map<String, Double> calculateDynamicFeature(DynamicVRPREPModel model) {

		Map<String, Double> result = new HashMap<>();
		DynamicModelDegreeCalculationHelper calcUtil = new DynamicModelDegreeCalculationHelper(model);
		double totalDistanceToDepot = 0.0;
		double dynamicDistanceToDepot = 0.0;

		for (Request r1 : model.getVRPREPInstance().getRequests().getRequest()) {

			double distanceR1 = 0.0;
			for (VehicleProfile vp : model.getVRPREPInstance().getFleet().getVehicleProfile()) {
				for (BigInteger departureNode : vp.getDepartureNode()) {
					distanceR1 += calcUtil.getDistanceNodeRequest(departureNode, r1.getId());
				}
			}

			totalDistanceToDepot += distanceR1;
			if (model.getDynamicRequestInformation().containsKey(r1.getId())) {
				dynamicDistanceToDepot += distanceR1;
			}
		}

		double weight = 0.0;
		for (Request r1 : model.getVRPREPInstance().getRequests().getRequest()) {
			for (Request r2 : model.getVRPREPInstance().getRequests().getRequest()) {
				if (model.getDynamicRequestInformation().containsKey(r1.getId())
						&& model.getDynamicRequestInformation().containsKey(r2.getId())) {
					weight += calcUtil.getDistance(r1.getId(), r2.getId());
				}
			}
		}

		result.put(this.getIdentifier(), (dynamicDistanceToDepot / totalDistanceToDepot) * weight);
		return result;
	}

}
