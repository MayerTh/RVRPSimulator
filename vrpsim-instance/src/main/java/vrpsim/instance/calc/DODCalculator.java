package vrpsim.instance.calc;

import vrpsim.instance.dynamicvrpmodel.ExtendedDynamicVRPREPModel;

public class DODCalculator extends AbstractDegreeOfDynanismCalculator {

	public double calculate(ExtendedDynamicVRPREPModel model, double timeHorizon) {
		
		double numberDynamicrequests = this.getNumberDynamicRequests(model);
		double numberRequests = this.getNumberRequests(model);
		
		double result = Double.NaN;
		if(numberRequests > 0) {
			result = numberDynamicrequests/numberRequests;
		}
		
		return result;
	}

}
