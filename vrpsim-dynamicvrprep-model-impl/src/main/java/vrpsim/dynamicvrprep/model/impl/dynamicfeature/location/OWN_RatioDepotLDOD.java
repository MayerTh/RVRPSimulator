package vrpsim.dynamicvrprep.model.impl.dynamicfeature.location;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.vrprep.model.instance.Instance.Fleet.VehicleProfile;
import org.vrprep.model.instance.Instance.Requests.Request;

import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.api.dynamicfeature.ADynamicModelFeature;
import vrpsim.dynamicvrprep.model.impl.dynamicfeature.util.DynamicModelDegreeCalculationHelper;

/**
 * Sum of the distance from all dynamic nodes to all depots to the sum of the
 * distance from all nodes to all depots.
 * 
 * @author mayert
 *
 */
public class OWN_RatioDepotLDOD extends ADynamicModelFeature {

	@Override
	public Map<String, Double>  calculateDynamicFeature(DynamicVRPREPModel model) {

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
		
		result.put(this.getIdentifier(), dynamicDistanceToDepot / totalDistanceToDepot);
		return result;
	}

}
