package vrpsim.instance.generator.model.instances;

import vrpsim.instance.generator.model.AbstractPropertyManager;
import vrpsim.instance.generator.model.PropertyClass;
import vrpsim.instance.generator.model.PropertyPeriod;

public class C1ParcelWeightManager extends AbstractPropertyManager {

	private static double pPeriod1 = 0.3;
	private static double pPeriod2 = 0.3;
	private static double pPeriod3 = 0.1;
	private static double pPeriod4 = 0.1;
	private static double pPeriod5 = 0.1;
	private static double pPeriod6 = 0.1;

	public C1ParcelWeightManager(int timeHorizion) {
		super(timeHorizion);
	}

	@Override
	public void build(int timeHorizion) {

		// 500g parcel
		PropertyPeriod period1 = new PropertyPeriod(100, 500);
		// 1kg parcel
		PropertyPeriod period2 = new PropertyPeriod(501, 1000);
		// 2kg parcel
		PropertyPeriod period3 = new PropertyPeriod(1001, 2000);
		// 5kg parcel
		PropertyPeriod period4 = new PropertyPeriod(2001, 5000);
		// 10kg parcel
		PropertyPeriod period5 = new PropertyPeriod(5001, 10000);
		// 31.5kg parcel
		PropertyPeriod period6 = new PropertyPeriod(10001, 31500);

		PropertyClass pc1 = new PropertyClass(period1, pPeriod1);
		PropertyClass pc2 = new PropertyClass(period2, pPeriod2);
		PropertyClass pc3 = new PropertyClass(period3, pPeriod3);
		PropertyClass pc4 = new PropertyClass(period4, pPeriod4);
		PropertyClass pc5 = new PropertyClass(period5, pPeriod5);
		PropertyClass pc6 = new PropertyClass(period6, pPeriod6);

		this.sortedPropertyClases.add(pc1);
		this.sortedPropertyClases.add(pc2);
		this.sortedPropertyClases.add(pc3);
		this.sortedPropertyClases.add(pc4);
		this.sortedPropertyClases.add(pc5);
		this.sortedPropertyClases.add(pc6);

	}

}
