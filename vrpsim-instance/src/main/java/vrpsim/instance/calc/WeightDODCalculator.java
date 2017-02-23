package vrpsim.instance.calc;

import vrpsim.instance.dynamicvrpmodel.AdditionalRequestInformation;
import vrpsim.instance.dynamicvrpmodel.ExtendedDynamicVRPREPModel;

public class WeightDODCalculator extends AbstractDegreeOfDynanismCalculator {

	public double calculate(ExtendedDynamicVRPREPModel model, double timeHorizon) {

		double dynamicWeight = this.getDynamicWeight(model);
		double all = this.getAllWeight(model);

		double result = Double.NaN;
		if (model.getRequestInformations().size() > 0) {
			result = dynamicWeight / all;
		}

		return result;
	}

	private double getAllWeight(ExtendedDynamicVRPREPModel model) {
		double sum = 0.0;
		for (AdditionalRequestInformation ari : model.getRequestInformations().values()) {
			sum += ari.getWeight();
		}
		return sum;
	}

	private double getDynamicWeight(ExtendedDynamicVRPREPModel model) {
		double sum = 0.0;
		for (AdditionalRequestInformation ari : model.getRequestInformations().values()) {
			if (ari.getArrivalTime() > 0) {
				sum += ari.getWeight();
			}
		}
		return sum;
	}

}
