package vrpsim.instance.calc;

import vrpsim.instance.dynamicvrpmodel.AdditionalRequestInformation;
import vrpsim.instance.dynamicvrpmodel.ExtendedDynamicVRPREPModel;

public abstract class AbstractDegreeOfDynanismCalculator implements IDegreeOfDynamismCalculator {

	protected int getNumberDynamicRequests(ExtendedDynamicVRPREPModel model) {
		int result = 0;
		for(AdditionalRequestInformation dri : model.getRequestInformations().values()) {
			if(dri.getArrivalTime() <= 0) {
				result++;
			}
		}
		return result;
	}

	protected int getNumberRequests(ExtendedDynamicVRPREPModel model) {
		return model.getRequestInformations().size();
	}

}
