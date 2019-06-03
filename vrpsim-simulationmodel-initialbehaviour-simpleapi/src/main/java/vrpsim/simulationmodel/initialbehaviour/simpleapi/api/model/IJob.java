package vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model;


public interface IJob {
	
	/**
	 * wenn >0 beladen
	 * wenn <0 entladen
	 * @return
	 */
	public double getload();
	
	/**
	 * @return
	 */
	public LocationAPI getLocation();
	
	/**
	 * @return
	 */
	public String getId();

}
