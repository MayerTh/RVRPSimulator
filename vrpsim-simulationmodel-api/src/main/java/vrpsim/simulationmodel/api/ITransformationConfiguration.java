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
package vrpsim.simulationmodel.api;

import vrpsim.core.model.structure.storage.impl.CanStoreType;
import vrpsim.core.model.structure.storage.impl.StorableParameters;

public interface ITransformationConfiguration {

	/**
	 * STorage type e.g. shelf.
	 * 
	 * @return
	 */
	public String getSTORAGE_TYPE();

	/**
	 * Storable type e.g. box.
	 * 
	 * @return
	 */
	public String getSTORABLE_TYPE();

	/**
	 * Capacity unit e.g. piece
	 * 
	 * @return
	 */
	public String getCAPACITY_UNIT();

	/**
	 * Return values >0.
	 * 
	 * @return
	 */
	public int getNUMBER_OF_VEHICLES();

	public CanStoreType getCANSTORETYPE();

	public StorableParameters getSTORABLEPARAMETERS();

	/**
	 * Return values >0.
	 * 
	 * @return
	 */
	public Double getMAX_CAPACITY_IN_CUSTOMER_STORAGE();

	/**
	 * Return values >0.
	 * 
	 * @return
	 */
	public Double getMAX_CAPACITY_IN_DEPOT_STORAGE();
	
	/**
	 * Sets the number of the vehicles.
	 */
	public void setNumberOfVehicles(int numberOfVehicles);

}
