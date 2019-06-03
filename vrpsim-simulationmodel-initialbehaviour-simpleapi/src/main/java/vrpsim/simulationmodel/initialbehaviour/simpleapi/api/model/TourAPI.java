package vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model;


import java.util.List;

public class TourAPI {

	final List<IJob> order;
	final VehicleAPI vehicle;
	final DriverAPI driver;

	public TourAPI(List<IJob> order, VehicleAPI vehicle, DriverAPI driver) {
		super();
		this.order = order;
		this.vehicle = vehicle;
		this.driver = driver;
	}

	public List<IJob> getOrder() {
		return order;
	}

	public VehicleAPI getVehicle() {
		return vehicle;
	}

	public DriverAPI getDriver() {
		return driver;
	}
	

}
