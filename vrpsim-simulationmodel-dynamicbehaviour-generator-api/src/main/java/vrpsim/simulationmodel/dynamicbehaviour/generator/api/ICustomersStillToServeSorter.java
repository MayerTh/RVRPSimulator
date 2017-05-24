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
package vrpsim.simulationmodel.dynamicbehaviour.generator.api;

import java.util.List;

import vrpsim.core.model.network.Location;
import vrpsim.core.model.structure.vehicle.IVehicle;

public interface ICustomersStillToServeSorter {

	/**
	 * 
	 * 
	 * @param planned
	 * @param newCustomer
	 * @param vehicle
	 * @param vehicleLocation
	 * @param depot
	 * @param instanceName
	 * @param statisticsOutputFolder
	 * @param createStatistics
	 * @return
	 */
	List<CustomersStillToServe> sort(List<CustomersStillToServe> planned, CustomersStillToServe newCustomer, IVehicle vehicle, Location vehicleLocation, Location depot,
			String instanceName, String statisticsOutputFolder, boolean createStatistics);

}
