package vrpsim.instance.generator.model;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPropertyManager {

	protected List<PropertyClass> sortedPropertyClases = new ArrayList<PropertyClass>();

	public AbstractPropertyManager(int timeHorizion) {
		build(timeHorizion);
	}
	
	/**
	 * Returns the {@link PropertyPeriod} for the property considering the given random value. 
	 * Note that the given random value is equal distributed between 0 and 1.
	 * 
	 * @param randomValue
	 * @return
	 */
	public PropertyPeriod getPeriod(double randomValue) {

		PropertyPeriod result = null;

		int index = 0;
		double desicionValue = 0.0;
		while (index < this.sortedPropertyClases.size()) {
			PropertyClass propertyClass = this.sortedPropertyClases.get(index);
			desicionValue += propertyClass.getProbability();
			index++;
			if (desicionValue > randomValue) {
				result = propertyClass.getPeriod();
				break;
			}
		}

		return result;
	}

	/**
	 * Build the sortedPropertyClases.
	 */
	public abstract void build(int timeHorizion);

}
