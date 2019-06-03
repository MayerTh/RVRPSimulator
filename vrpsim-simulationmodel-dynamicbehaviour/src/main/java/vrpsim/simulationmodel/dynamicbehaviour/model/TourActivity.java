package vrpsim.simulationmodel.dynamicbehaviour.model;

import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.storage.impl.StorableParameters;

public class TourActivity {

	final IVRPSimulationModelStructureElementWithStorage element;
	final StorableParameters storeableParameters;
	final Integer amount;

	public TourActivity(IVRPSimulationModelStructureElementWithStorage element, StorableParameters storeableParameters,
			Integer amount) {
		this.element = element;
		this.storeableParameters = storeableParameters;
		this.amount = amount;
	}

	public IVRPSimulationModelStructureElementWithStorage getElement() {
		return element;
	}

	public StorableParameters getStoreableParameters() {
		return storeableParameters;
	}

	/**
	 * If negative than unloading, if positive than loading.
	 * 
	 * @return
	 */
	public Integer getAmount() {
		return amount;
	}

}
