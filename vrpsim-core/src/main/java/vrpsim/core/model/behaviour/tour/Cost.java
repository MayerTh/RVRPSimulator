package vrpsim.core.model.behaviour.tour;

import java.math.BigDecimal;

public class Cost {

	private double travelTime = 0L;
	private double waitingTime = 0D;
	private double serviceTime = 0D;
	private double travelDistance = 0D;

	public Cost() {
		// empty
	}

	public Cost(double travelTime, double waitingTime, double serviceTime, double travelDistance) {
		this.travelTime = travelTime;
		this.waitingTime = waitingTime;
		this.serviceTime = serviceTime;
		this.travelDistance = travelDistance;
	}

	public double getServiceTime() {
		return serviceTime;
	}

	public double getTravelTime() {
		return travelTime;
	}

	public void addTravelTime(long travelTime) {
		this.travelTime += travelTime;
	}

	public double getWaitingTime() {
		return waitingTime;
	}

	public void addWaitingTime(long waitingTime) {
		this.waitingTime += waitingTime;
	}

	public double getTravelDistance() {
		return travelDistance;
	}

	public void addTravelDistance(double travelDistance) {
		this.travelDistance += travelDistance;
	}

	/**
	 * Returns a new instance from {@link Cost}.
	 * 
	 * @param tourCosts
	 * @return
	 */
	public static Cost addCosts(Cost costs1, Cost costs2) {
		return new Cost(costs1.getTravelTime() + costs2.getTravelTime(), costs1.getWaitingTime() + costs2.getWaitingTime(),
				costs1.getServiceTime() + costs2.getServiceTime(), costs1.getTravelDistance() + costs2.getTravelDistance());
	}

	@Override
	public String toString() {
		return String.format("Cost[travelT=%s,travelD=%s,waitingT=%s,serviceT=%s", round(travelTime, 2), round(travelDistance, 2), waitingTime, serviceTime);
	}
	
	private double round(double wert, int stellen) {
		BigDecimal b = new BigDecimal(wert);
		return b.setScale(stellen, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

}