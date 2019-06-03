package vrpsim.simulationmodel.dynamicbehaviour.model;

import java.util.List;

import vrpsim.core.model.behaviour.activities.IActivity;
import vrpsim.core.model.behaviour.activities.impl.TransportActivity;
import vrpsim.core.model.structure.driver.IDriver;
import vrpsim.core.model.structure.vehicle.IVehicle;

public class TourState {

	private final long id;
	private final boolean addAtFirstPlaceAllowed;
	private double startTime = 0D;

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public boolean isAddAtFirstPlaceAllowed() {
		return addAtFirstPlaceAllowed;
	}

	private IVehicle vehicle;
	private IDriver driver;

	private List<TourActivity> tourActivities;

	public TourState(long id, IActivity currentActivity, IVehicle vehicle, IDriver driver, List<TourActivity> customersStillToServe) {
		this.id = id;
		this.vehicle = vehicle;
		this.driver = driver;
		this.tourActivities = customersStillToServe;
		this.addAtFirstPlaceAllowed = !(currentActivity != null && currentActivity instanceof TransportActivity
				&& currentActivity.isPrepared());
	}

	public IDriver getDriver() {
		return driver;
	}

	public void setDriver(IDriver driver) {
		this.driver = driver;
	}

	public IVehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(IVehicle vehicle) {
		this.vehicle = vehicle;
	}

	public long getId() {
		return id;
	}

	/**
	 * Returns the list of {@link TourActivity} the vehicle is serving next.
	 * 
	 * @return
	 */
	public List<TourActivity> getTourActivities() {
		return tourActivities;
	}

	public void setTourActivities(List<TourActivity> tourActivities) {
		this.tourActivities = tourActivities;
	}

}
