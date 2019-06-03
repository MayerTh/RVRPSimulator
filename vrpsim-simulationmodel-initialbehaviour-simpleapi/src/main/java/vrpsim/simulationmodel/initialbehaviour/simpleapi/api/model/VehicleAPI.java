package vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model;


/**
 * @author Nikita
 *
 */
public class VehicleAPI {

	final double capacity;
	final String id;
	
	/**
	 * @param capacity
	 * @param id link to Vehicle Object from SimulationModel Model
	 */
	public VehicleAPI(double capacity, String id) {
		super();
		this.capacity = capacity;
		this.id = id;
	}

	public double getCapacity() {
		return capacity;
	}

	public String getId() {
		return id;
	}


}
