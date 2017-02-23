package vrpsim.instance.calc;

import vrpsim.instance.dynamicvrpmodel.ExtendedDynamicVRPREPModel;

public interface IDegreeOfDynamismCalculator {

	/**
	 * Calculates the degree of dynamism for the given model.
	 * 
	 * @param model
	 * @return
	 */
	public double calculate(ExtendedDynamicVRPREPModel model, double timeHorizon);
}
