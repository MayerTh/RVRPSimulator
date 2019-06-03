package vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model;


public class LocationAPI {
	final double xCoord;
	final double yCoord;

	public LocationAPI(double xCoord, double yCoord) {
		super();
		this.xCoord = xCoord;
		this.yCoord = yCoord;
	}

	public double getxCoord() {
		return xCoord;
	}

	public double getyCoord() {
		return yCoord;
	}

	
}
