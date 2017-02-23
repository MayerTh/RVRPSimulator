package vrpsim.instance.calc;

import vrpsim.instance.dynamicvrpmodel.AdditionalRequestInformation;
import vrpsim.instance.dynamicvrpmodel.ExtendedDynamicVRPREPModel;

public class DimensionEDODCalculator extends AbstractDegreeOfDynanismCalculator {

	public double calculate(ExtendedDynamicVRPREPModel model, double timeHorizon) {

		double allDimensionInTheSystem = 0.0;
		for (AdditionalRequestInformation dri : model.getRequestInformations().values()) {
			allDimensionInTheSystem += dri.getDimension().getVolumeForDimension();
		}
		
		double sum = 0;
		for (AdditionalRequestInformation dri : model.getRequestInformations().values()) {
			if (dri.getArrivalTime() > 0) {
				double value = new Double(dri.getDimension().getVolumeForDimension()) / allDimensionInTheSystem;
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
