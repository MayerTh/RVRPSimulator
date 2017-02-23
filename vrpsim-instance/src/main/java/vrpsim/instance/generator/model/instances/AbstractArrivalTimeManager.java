package vrpsim.instance.generator.model.instances;

import vrpsim.instance.generator.model.AbstractPropertyManager;
import vrpsim.instance.generator.model.PropertyClass;
import vrpsim.instance.generator.model.PropertyPeriod;

public abstract class AbstractArrivalTimeManager extends AbstractPropertyManager {

	public AbstractArrivalTimeManager(int timeHorizion) {
		super(timeHorizion);
	}

	@Override
	public void build(int timeHorizion) {

		PropertyPeriod period0 = new PropertyPeriod(0, 0);
		PropertyPeriod period1 = new PropertyPeriod(1, new Double(timeHorizion / 3).intValue());
		PropertyPeriod period2 = new PropertyPeriod(new Double((timeHorizion / 3) + 1).intValue(), new Double((2 * timeHorizion) / 3).intValue());
		PropertyPeriod period3 = new PropertyPeriod(new Double(((2 * timeHorizion) / 3) + 1).intValue(), new Double(timeHorizion).intValue());

		PropertyClass pc0 = new PropertyClass(period0, getPPeriod0());
		PropertyClass pc1 = new PropertyClass(period1, getPPeriod1());
		PropertyClass pc2 = new PropertyClass(period2, getPPeriod2());
		PropertyClass pc3 = new PropertyClass(period3, getPPeriod3());

		this.sortedPropertyClases.add(pc0);
		this.sortedPropertyClases.add(pc1);
		this.sortedPropertyClases.add(pc2);
		this.sortedPropertyClases.add(pc3);

	}

	public abstract double getPPeriod0();

	public abstract double getPPeriod1();

	public abstract double getPPeriod2();

	public abstract double getPPeriod3();

}
