package vrpsim.core.model.util.policies.impl;

import java.util.ArrayList;
import java.util.List;

import vrpsim.core.model.structure.storage.IStorable;
import vrpsim.core.model.structure.storage.IStorableGenerator;
import vrpsim.core.model.structure.storage.impl.StorableParameters;
import vrpsim.core.model.util.policies.ILoadingPolicy;

public class EndlessNoStateLoadingPolicy implements ILoadingPolicy {

	private int numberInside = 0;
	private final IStorableGenerator generator;

	public EndlessNoStateLoadingPolicy(IStorableGenerator generator) {
		this.generator = generator;
	}
	
	@Override
	public void reset() {
		// Do nothing.
	}

	@Override
	public void load(IStorable storable) {
		numberInside++;
	}

	@Override
	public void load(List<IStorable> storables) {
		numberInside = +storables.size();
	}

	@Override
	public IStorable unload() {
		numberInside--;
		return generator.generateDefaultStorable();
	}

	@Override
	public List<IStorable> unload(StorableParameters storableParameters, int number) {
		numberInside = -number;
		List<IStorable> result = new ArrayList<>();
		for (int i = 0; i < number; i++) {
			result.add(generator.generateStorable(storableParameters));
		}
		return result;
	}

	@Override
	public int getCurrentNumberOfStorablesInside() {
		return numberInside;
	}

	@Override
	public double getCapacityInside() {
		return numberInside;
	}

	@Override
	public void load(StorableParameters storableParameters, int number) {
		numberInside += number;
	}

	@Override
	public double getCapacityInside(StorableParameters storableParameters) {
		return numberInside;
	}

}
