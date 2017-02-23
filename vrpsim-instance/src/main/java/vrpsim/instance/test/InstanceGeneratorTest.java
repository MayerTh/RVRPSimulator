package vrpsim.instance.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.instance.calc.DODCalculator;
import vrpsim.instance.calc.DimensionDODCalculator;
import vrpsim.instance.calc.EDODCalculator;
import vrpsim.instance.calc.IDegreeOfDynamismCalculator;
import vrpsim.instance.calc.WeightDODCalculator;
import vrpsim.instance.dynamicvrpmodel.ExtendedDynamicVRPREPModel;
import vrpsim.instance.generator.IInstanceGenerator;
import vrpsim.instance.generator.InstanceGenerator;
import vrpsim.instance.generator.config.IIdGenerator;
import vrpsim.instance.generator.config.InstanceGeneratorConfig;
import vrpsim.instance.generator.model.AbstractPropertyManager;
import vrpsim.instance.generator.model.PropertyConfig;
import vrpsim.instance.generator.model.instances.C1ParcelDimensionManager;
import vrpsim.instance.generator.model.instances.C1ParcelWeightManager;
import vrpsim.instance.generator.model.instances.P1ArrivalTimeManager;

public class InstanceGeneratorTest {

	private static Logger logger = LoggerFactory.getLogger(InstanceGeneratorTest.class);

	private static int numberInstance = 100;
	private static int numberRequests = 100;
	private static int timeHorizon = 1000;
	private static Random random = new Random(123);

	private static AbstractPropertyManager arrivalTimeManager = new P1ArrivalTimeManager(timeHorizon);
	private static AbstractPropertyManager dimensionTimeManager = new C1ParcelDimensionManager(timeHorizon);
	private static AbstractPropertyManager weightManager = new C1ParcelWeightManager(timeHorizon);

	public static void main(String[] args) throws IOException {

		logger.info("Generating instances");

		List<InstanceDegreeContainer> containers = new ArrayList<InstanceDegreeContainer>();
		IDegreeOfDynamismCalculator dodCalculator = new DODCalculator();
		IDegreeOfDynamismCalculator edodCalculator = new EDODCalculator();
		IDegreeOfDynamismCalculator weightEdodCalculator = new WeightDODCalculator();
		IDegreeOfDynamismCalculator dimensionEdodCalculator = new DimensionDODCalculator();

		IIdGenerator idGenerator = new IdGeneratorForTest();
		IInstanceGenerator randomInstanceGenerator = new InstanceGenerator();
		PropertyConfig propertyConfig = new PropertyConfig(arrivalTimeManager, dimensionTimeManager, weightManager);
		InstanceGeneratorConfig config = new InstanceGeneratorConfig(random, propertyConfig, idGenerator, numberRequests);

		for (int i = 0; i < numberInstance; i++) {
			ExtendedDynamicVRPREPModel edm = randomInstanceGenerator.generateRandomInstance(config);
			containers.add(new InstanceDegreeContainer(dodCalculator.calculate(edm, timeHorizon), edodCalculator.calculate(edm, timeHorizon),
					weightEdodCalculator.calculate(edm, timeHorizon), dimensionEdodCalculator.calculate(edm, timeHorizon), i));
		}

		new RExporter().exportTo("out_new", "Test", "Test", containers);

	}

}
