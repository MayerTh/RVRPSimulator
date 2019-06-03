package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.mmasus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import isula.aco.ConfigurationProvider;
import isula.aco.algorithms.antsystem.RandomNodeSelection;
import isula.aco.exception.ConfigurationException;

public class MaximumNodeSelection extends RandomNodeSelection<Integer, TSPEnvironment> {

	@Override
	public boolean applyPolicy(TSPEnvironment arg0, ConfigurationProvider arg1) {
		Integer nextNode = null;
		double maximum = 0;

		HashMap<Integer, Double> componentsWithProbabilities = this.getComponentsWithProbabilities(arg0, arg1);
		Iterator<Map.Entry<Integer, Double>> componentWithProbabilitiesIterator = componentsWithProbabilities.entrySet().iterator();
		while (componentWithProbabilitiesIterator.hasNext()) {
			Map.Entry<Integer, Double> componentWithProbability = componentWithProbabilitiesIterator.next();

			Double probability = componentWithProbability.getValue();
			if (probability.isNaN()) {
				throw new ConfigurationException("The probability for component " + componentWithProbability.getKey() + " is not a number.");
			}

			if (maximum <= probability) {
				nextNode = componentWithProbability.getKey();
			}
		}

		getAnt().visitNode(nextNode);
		return true;
	}
	
}
