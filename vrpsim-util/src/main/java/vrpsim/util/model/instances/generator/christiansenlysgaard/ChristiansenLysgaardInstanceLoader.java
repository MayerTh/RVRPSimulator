package vrpsim.util.model.instances.generator.christiansenlysgaard;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.util.model.instances.generator.util.InstanceLoaderUtil;

public class ChristiansenLysgaardInstanceLoader {

	private final InstanceLoaderUtil instanceLoaderUtil = new InstanceLoaderUtil();

	/**
	 * Returns a list of internal paths of files representing an VRP instance
	 * from Christiansen and Lysgaard (2007).
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<String> getAvailablePathsToInstances() throws IOException {
		List<String> availableInstances = new ArrayList<>();
		final String path = "stochasticvrp/christiansenlysgaard2007/instances";
		availableInstances.addAll(this.instanceLoaderUtil.getAvailablePathsToInstances(path));
		return availableInstances;
	}

	/**
	 * Return the Christiansen and Lysgaard instance defined by the path.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public VRPSimulationModel loadInstance(String path) throws IOException, URISyntaxException {
		throw new RuntimeException("Not implemented.");
	}

}
