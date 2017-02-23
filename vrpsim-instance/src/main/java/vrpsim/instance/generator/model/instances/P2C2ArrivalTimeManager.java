package vrpsim.instance.generator.model.instances;

public class P2C2ArrivalTimeManager extends AbstractArrivalTimeManager {

	public P2C2ArrivalTimeManager(int timeHorizion) {
		super(timeHorizion);
	}

	@Override
	public double getPPeriod0() {
		return 0.5;
	}

	@Override
	public double getPPeriod1() {
		return 0.1;
	}

	@Override
	public double getPPeriod2() {
		return 0.4;
	}

	@Override
	public double getPPeriod3() {
		return 0;
	}

}
