package vrpsim.instance.generator.model.instances;

import vrpsim.instance.generator.model.AbstractPropertyManager;
import vrpsim.instance.generator.model.PropertyClass;
import vrpsim.instance.generator.model.PropertyPeriod;

public class C1ParcelDimensionManager extends AbstractPropertyManager {

	private static double pPeriod1 = 0.6;
	private static double pPeriod2 = 0.3;
	private static double pPeriod3 = 0.1;

	public C1ParcelDimensionManager(int timeHorizion) {
		super(timeHorizion);
	}

	@Override
	public void build(int timeHorizion) {

		// 13500 cm3 parcel (30/30/15)
		PropertyPeriod period1 = new PropertyPeriod(100, 13500);
		// 27000 cm3 parcel (60/30/15)
		PropertyPeriod period2 = new PropertyPeriod(13501, 27000);
		// 432000 cm3 parcel (120/60/60)
		PropertyPeriod period3 = new PropertyPeriod(27000, 432000);

		PropertyClass pc1 = new PropertyClass(period1, pPeriod1);
		PropertyClass pc2 = new PropertyClass(period2, pPeriod2);
		PropertyClass pc3 = new PropertyClass(period3, pPeriod3);

		this.sortedPropertyClases.add(pc1);
		this.sortedPropertyClases.add(pc2);
		this.sortedPropertyClases.add(pc3);

	}
	
}
