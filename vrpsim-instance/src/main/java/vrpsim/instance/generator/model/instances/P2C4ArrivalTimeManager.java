package vrpsim.instance.generator.model.instances;

public class P2C4ArrivalTimeManager extends AbstractArrivalTimeManager {

	public P2C4ArrivalTimeManager(int timeHorizion) {
		super(timeHorizion);
	}

	@Override
	public double getPPeriod0() {
		return 0.2;
	}

	@Override
	public double getPPeriod1() {
		return 0.2;
	}

	@Override
	public double getPPeriod2() {
		return 0.6;
	}

	@Override
	public double getPPeriod3() {
		return 0;
	}

}
