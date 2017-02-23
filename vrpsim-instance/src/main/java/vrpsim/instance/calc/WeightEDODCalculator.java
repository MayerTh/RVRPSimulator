package vrpsim.instance.calc;

import vrpsim.instance.dynamicvrpmodel.AdditionalRequestInformation;
import vrpsim.instance.dynamicvrpmodel.ExtendedDynamicVRPREPModel;

public class WeightEDODCalculator extends AbstractDegreeOfDynanismCalculator {

	public double calculate(ExtendedDynamicVRPREPModel model, double timeHorizon) {

		double allWeightInTheSystem = 0.0;
		for (AdditionalRequestInformation dri : model.getRequestInformations().values()) {
			allWeightInTheSystem += dri.getWeight();
		}
		
		double sum = 0;
		for (AdditionalRequestInformation dri : model.getRequestInformations().values()) {
			if (dri.getArrivalTime() > 0) {
				double value = new Double(dri.getWeight()) / allWeightInTheSystem;
				sum += value;
			}
		}

		double result = Double.NaN;
		if (sum > 0) {
			result = sum / this.getNumberRequests(model);
		}

		return result;
	}

}
