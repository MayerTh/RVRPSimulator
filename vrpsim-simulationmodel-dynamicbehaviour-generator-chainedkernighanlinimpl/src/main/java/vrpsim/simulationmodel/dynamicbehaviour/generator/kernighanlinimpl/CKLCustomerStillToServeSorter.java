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
package vrpsim.simulationmodel.dynamicbehaviour.generator.kernighanlinimpl;

import java.util.List;

import vrpsim.core.model.network.Location;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.simulationmodel.dynamicbehaviour.generator.api.CustomersStillToServe;
import vrpsim.simulationmodel.dynamicbehaviour.generator.api.ICustomersStillToServeSorter;

public class CKLCustomerStillToServeSorter implements ICustomersStillToServeSorter {

	@Override
	public List<CustomersStillToServe> sort(List<CustomersStillToServe> planned, CustomersStillToServe newCustomer, IVehicle vehicle, Location vehicleLocation, Location depot,
			String instanceName, String statisticsOutputFolder, boolean createStatistics) {

		
		
		return null;
	}

	private double getLenght(Location start, List<Location> sortedLocations, Location end) {
		double sum = 0.0;
		if (!sortedLocations.isEmpty()) {
			sum += getLenght(start, sortedLocations.get(0));
			for (int i = 0; i < sortedLocations.size() - 1; i++) {
				sum += getLenght(sortedLocations.get(i), sortedLocations.get(i + 1));
			}
			sum += getLenght(sortedLocations.get(sortedLocations.size() - 1), end);
		} else {
			sum = getLenght(start, end);
		}
		return sum;
	}

	private double getLenght(Location location1, Location location2) {
		double a = Math.abs(location1.getX() - location2.getX());
		double b = Math.abs(location1.getY() - location2.getY());
		double c = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
		return c;
	}

}
