package vrpsim.instance.calc;

import vrpsim.instance.dynamicvrpmodel.AdditionalRequestInformation;
import vrpsim.instance.dynamicvrpmodel.ExtendedDynamicVRPREPModel;

public class DimensionDODCalculator extends AbstractDegreeOfDynanismCalculator {

	public double calculate(ExtendedDynamicVRPREPModel model, double timeHorizon) {

		double dynamicWeight = this.getDynamicDimension(model);
		double all = this.getAllDimension(model);

		double result = Double.NaN;
		if (model.getRequestInformations().size() > 0) {
			result = dynamicWeight / all;
		}

		return result;
	}

	private double getAllDimension(ExtendedDynamicVRPREPModel model) {
		double sum = 0.0;
		for (AdditionalRequestInformation ari : model.getRequestInformations().values()) {
			sum += ari.getDimension().getVolumeForDimension();
		}
		return sum;
	}

	private double getDynamicDimension(ExtendedDynamicVRPREPModel model) {
		double sum = 0.0;
		for (AdditionalRequestInformation ari : model.getRequestInformations().values()) {
			if (ari.getArrivalTime() > 0) {
				sum += ari.getDimension().getVolumeForDimension();
			}
		}
		return sum;
	}

}
