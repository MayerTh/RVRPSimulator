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

/**
 * Defines the type of a {@link IStorable} and the types where the
 * {@link IStorable} can be stored in, see {@link CanStoreType}.
 * 
 * @author mayert
 */
public class StorableType {

	private final String id;

	public StorableType(String id) {
		super();
		this.id = id;
	}

	/**
	 * Return the identifier of the storable type.
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equal = false;
		if (obj instanceof StorableType) {
			equal = ((StorableType) obj).getId().equals(this.id);
		}
		return equal;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public String toString() {
		return this.id;
	}

}
