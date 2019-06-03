package vrpsim.core.model.util.policies.impl;

import java.util.List;

import vrpsim.core.model.network.IWay;

public class Route {

	private final double rountingTime;
	private final double rountingDistance;

	private final List<IWay> usedWays;

	public Route(double rountingDistance, double rountingTime, final List<IWay> usedWays) {
		this.rountingDistance = rountingDistance;
		this.rountingTime = rountingTime;
		this.usedWays = usedWays;
	}

	public double getRountingTime() {
		return rountingTime;
	}

	public double getRountingDistance() {
		return rountingDistance;
	}

	public List<IWay> getUsedWays() {
		return usedWays;
	}

}
