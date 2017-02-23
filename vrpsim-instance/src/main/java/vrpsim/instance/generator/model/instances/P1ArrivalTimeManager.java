package vrpsim.instance.generator.model.instances;

public class P1ArrivalTimeManager extends AbstractArrivalTimeManager {

	public P1ArrivalTimeManager(int timeHorizion) {
		super(timeHorizion);
	}

	@Override
	public double getPPeriod0() {
		return 0.5;
	}

	@Override
	public double getPPeriod1() {
		return 0.5;
	}

	@Override
	public double getPPeriod2() {
		return 0;
	}

	@Override
	public double getPPeriod3() {
		return 0;
	}

}
