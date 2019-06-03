package vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model;

public class DepotAPI implements IJob {
	
	final int load;
	final LocationAPI location;
	final String id;
	
	public DepotAPI(int load, LocationAPI location, String id) {
		super();
		this.load = load;
		this.location = location;
		this.id = id;
	}
	@Override
	public double getload() {
		return load;
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
