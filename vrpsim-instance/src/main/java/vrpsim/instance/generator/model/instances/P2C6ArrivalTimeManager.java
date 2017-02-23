package vrpsim.instance.generator.model.instances;

public class P2C6ArrivalTimeManager extends AbstractArrivalTimeManager {

	public P2C6ArrivalTimeManager(int timeHorizion) {
		super(timeHorizion);
	}

	@Override
	public double getPPeriod0() {
		return 0.0;
	}

	@Override
	public double getPPeriod1() {
		return 0.3;
	}

	@Override
	public double getPPeriod2() {
		return 0.7;
	}

	@Override
	public double getPPeriod3() {
		return 0;
	}

}
