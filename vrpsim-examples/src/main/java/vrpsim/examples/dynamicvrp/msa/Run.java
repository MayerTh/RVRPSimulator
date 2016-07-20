package vrpsim.examples.dynamicvrp.msa;

import java.io.IOException;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.simulator.Clock;
import vrpsim.core.simulator.MainProgramm;
import vrpsim.examples.dynamicvrp.msa.instance.BentInstanceLoader;
import vrpsim.visualization.Visualisation;

public class Run extends Visualisation {

	private static String file = "/dynamicvrp/bent2003/class1/0-100-rc101-1";
	
	public static void main(String[] args) throws IOException {
		
		BentInstanceLoader bil = new BentInstanceLoader();
		VRPSimulationModel model = bil.loadBentInstance(file); 
		
		init(new MainProgramm(), model, new Clock.Time(100.0));
		launch(args);
		
	}
	
}
 