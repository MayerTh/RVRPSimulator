package vrpsim.instance.generator;

import vrpsim.instance.dynamicvrpmodel.ExtendedDynamicVRPREPModel;
import vrpsim.instance.generator.config.InstanceGeneratorConfig;

public interface IInstanceGenerator {

	public ExtendedDynamicVRPREPModel generateRandomInstance(InstanceGeneratorConfig config);

}
