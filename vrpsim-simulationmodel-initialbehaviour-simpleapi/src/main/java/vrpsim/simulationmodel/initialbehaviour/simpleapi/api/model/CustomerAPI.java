package vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model;


public class CustomerAPI implements IJob {

	final int load;
	final LocationAPI location;
	final String id;

	public CustomerAPI(int load, LocationAPI location, String id) {
		super();
		this.load = load;
		this.location = location;
		this.id = id;
	}
	
	public CustomerAPI(int load, double xCoord, double yCoord, String id) {
		super();
		this.load = load;
		this.location = new LocationAPI(xCoord, yCoord);
		this.id = id;
	}

	@Override
	public double getload() {
		return this.load;
	}

	@Override
	public LocationAPI getLocation() {
		return location;
	}

	@Override
	public String getId() {
		return id;
	}
	
	

}
