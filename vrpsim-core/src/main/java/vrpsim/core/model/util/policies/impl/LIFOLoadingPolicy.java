/**
 * Copyright © 2016 Thomas Mayer (thomas.mayer@unibw.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vrpsim.core.model.util.policies.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import vrpsim.core.model.structure.storage.IStorable;
import vrpsim.core.model.structure.storage.IStorableGenerator;
import vrpsim.core.model.structure.storage.impl.StorableParameters;
import vrpsim.core.model.util.policies.ILoadingPolicy;

/**
 * Implements a {@code Stack}.
 * 
 * @date 29.01.2016
 * @author thomas.mayer@unibw.de
 */
public class LIFOLoadingPolicy implements ILoadingPolicy {

	private Stack<IStorable> stack = new Stack<IStorable>();

	private final IStorableGenerator generator;

	public LIFOLoadingPolicy(IStorableGenerator generator) {
		this.generator = generator;
	}
	
	public void reset() {
		this.stack = new Stack<IStorable>();
	}

	@Override
	public void load(IStorable storable) {
		stack.push(storable);
	}

	@Override
	public IStorable unload() {
		IStorable storable = null;
		if (stack.size() > 1) {
			storable = stack.pop();
		}
		return storable;
	}

	@Override
	public int getCurrentNumberOfStorablesInside() {
		return this.stack.size();
	}

	@Override
	public void load(List<IStorable> storables) {
		storables.forEach(e -> stack.push(e));
	}

	@Override
	public List<IStorable> unload(StorableParameters storableParameters, int number) {
		List<IStorable> result = null;
		if (number <= stack.size()) {
			result = new ArrayList<>();
			for (int i = 0; i < number; i++) {
				result.add(stack.pop());
			}
		}
		return result;
	}

	@Override
	public double getCapacityInside() {
		double capaInside = 0.0;
		for (IStorable storable : stack) {
			capaInside += storable.getStorableParameters().getCapacityFactor() * 1;
		}
		return capaInside;
	}

	@Override
	public void load(StorableParameters storableParameters, int number) {
		for (int i = 0; i <= number; i++) {
			load(this.generator.generateStorable(storableParameters));
		}
	}

	@Override
	public double getCapacityInside(StorableParameters storableParameters) {
		double capaInside = 0.0;
		for (IStorable storable : stack) {
			if (storableParameters.getStorableType().equals(storable.getStorableParameters().getStorableType())) {
				capaInside += storable.getStorableParameters().getCapacityFactor() * 1;
			}
		}
		return capaInside;
	}

}
