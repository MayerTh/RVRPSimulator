package vrpsim.instance.generator.config;

import java.util.Random;

import vrpsim.instance.generator.model.PropertyConfig;

public class InstanceGeneratorConfig {

	private final Random random;

	private final IIdGenerator idGenerator;

	private final int numberRequests;

	private final PropertyConfig propertyConfig;

	public InstanceGeneratorConfig(Random random, PropertyConfig propertyConfig, IIdGenerator idGenerator, int numberRequests) {
		super();
		this.random = random;
		this.idGenerator = idGenerator;
		this.numberRequests = numberRequests;
		this.propertyConfig = propertyConfig;
	}

	public int getNumberRequests() {
		return numberRequests;
	}

	public Random getRandom() {
		return random;
	}

	public IIdGenerator getIdGenerator() {
		return idGenerator;
	}

	public PropertyConfig getPropertyConfig() {
		return propertyConfig;
	}

}
