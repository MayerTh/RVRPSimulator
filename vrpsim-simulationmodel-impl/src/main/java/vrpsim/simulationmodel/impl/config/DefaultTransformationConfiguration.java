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
package vrpsim.simulationmodel.impl.config;

import vrpsim.core.model.structure.storage.impl.CanStoreType;
import vrpsim.core.model.structure.storage.impl.StorableParameters;
import vrpsim.core.model.structure.storage.impl.StorableType;
import vrpsim.simulationmodel.api.ITransformationConfiguration;

public class DefaultTransformationConfiguration implements ITransformationConfiguration {

	final CanStoreType canStoreType;
	final StorableParameters storableParameters;
	int numberOfVehicles = 1;

	public DefaultTransformationConfiguration() {
		this.canStoreType = new CanStoreType(getSTORAGE_TYPE());
		this.storableParameters = new StorableParameters(1, new StorableType(this.getSTORABLE_TYPE()));
	}

	@Override
	public String getSTORAGE_TYPE() {
		return "shelf";
	}

	@Override
	public String getSTORABLE_TYPE() {
		return "box";
	}

	@Override
	public String getCAPACITY_UNIT() {
		return "piece";
	}

	@Override
	public int getNUMBER_OF_VEHICLES() {
		return numberOfVehicles;
	}

	@Override
	public CanStoreType getCANSTORETYPE() {
		return this.canStoreType;
	}

	@Override
	public StorableParameters getSTORABLEPARAMETERS() {
		return this.storableParameters;
	}

	@Override
	public Double getMAX_CAPACITY_IN_CUSTOMER_STORAGE() {
		return Double.MAX_VALUE;
	}

	@Override
	public Double getMAX_CAPACITY_IN_DEPOT_STORAGE() {
		return Double.MAX_VALUE;
	}

	@Override
	public void setNumberOfVehicles(int numberOfVehicles) {
		this.numberOfVehicles = numberOfVehicles;
	}

}
