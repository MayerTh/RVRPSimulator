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
package vrpsim.core.model.structure.storage.impl;

import vrpsim.core.model.structure.storage.IStorable;

public class Good implements IStorable {

	private final String id;
	private final StorableParameters storableParameters;

	public Good(final String id, final StorableParameters storableParameters) {
		this.id = id;
		this.storableParameters = storableParameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.storage.IStorable#getStorableParameters()
	 */
	public StorableParameters getStorableParameters() {
		return this.storableParameters;
	}

	@Override
	public String getStorableId() {
		return this.id;
	}
}
