package vrpsim.core.model.events.strategies.impl.util;

import vrpsim.core.model.structure.storage.impl.StorableParameters;

public class Consum {

	private final StorableParameters storableParameters;

	private final int number;

	public Consum(StorableParameters storableParameters, int number) {
		this.storableParameters = storableParameters;
		this.number = number;
	}

	public StorableParameters getStorableParameters() {
		return storableParameters;
	}

	public int getNumber() {
		return number;
	}

}
