package vrpsim.examples.dynamicvrp.msa;

import java.io.IOException;
import java.net.URISyntaxException;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.simulator.Clock;
import vrpsim.core.simulator.MainProgramm;
import vrpsim.util.model.instances.generator.bent.BentInstanceLoader;
import vrpsim.visualization.Visualisation;

public class Run extends Visualisation {

	public static void main(String[] args) throws IOException, URISyntaxException {

		BentInstanceLoader bil = new BentInstanceLoader();
		String path = bil.getAvailablePathsToBentInstances().get(0);
		VRPSimulationModel model = bil.loadBentInstance(path);

		init(new MainProgramm(), model, new Clock.Time(1000.0));
		launch(args);

	}

}
