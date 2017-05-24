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
package vrpsim.simulationmodel.dynamicbehaviour.generator.jspritimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Delivery;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.job.Service.Builder;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.DeliverService;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;

import vrpsim.core.model.network.INode;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.util.storage.CanStoreType;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.simulationmodel.dynamicbehaviour.generator.api.CustomersStillToServe;
import vrpsim.simulationmodel.dynamicbehaviour.generator.api.ICustomersStillToServeSorter;

public class JspritUnloadContainerSorter implements ICustomersStillToServeSorter {

	private static Logger logger = LoggerFactory.getLogger(JspritUnloadContainerSorter.class);


	public List<CustomersStillToServe> sort(List<CustomersStillToServe> containersToSort, CustomersStillToServe newCustomer,  IVehicle vehicle, vrpsim.core.model.network.Location currentLocationOfVehicle,
			vrpsim.core.model.network.Location depot, String instanceName, String statisticsOutputFolder, boolean createStatistics) {

		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		
		try {
		
		vrpBuilder.addAllVehicles(buildJspritVehicles(vehicle, currentLocationOfVehicle));
		vrpBuilder.addAllJobs(buildJspritServices(containersToSort));

		} catch (VRPArithmeticException e) {
			logger.error("Can not build vehicles/jobs for Jsprit sorter. ");
			e.printStackTrace();
		}
		
		VehicleRoutingProblem problem = vrpBuilder.build();
		VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);
		Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
		VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

		return translate(bestSolution, containersToSort);
	}

	private List<CustomersStillToServe> translate(VehicleRoutingProblemSolution solution, List<CustomersStillToServe> containersToSort) {

		logger.info("Unsorted customers {}", containersToSort);
		
		if (solution.getUnassignedJobs().size() > 0) {
			logger.error("There unassigned joby in the Jsprit solution.");
			throw new RuntimeException("There unassigned joby in the Jsprit solution.");
		}

		List<CustomersStillToServe> sorted = new ArrayList<>();

		for (VehicleRoute vehicleRoute : solution.getRoutes()) {

			for (TourActivity tourActivity : vehicleRoute.getActivities()) {

				if (tourActivity instanceof DeliverService) {
					CustomersStillToServe unloadBy = getUnloadByByLocation(containersToSort, tourActivity.getLocation());
					int amount = tourActivity.getSize().getNuOfDimensions();
					sorted.add(new CustomersStillToServe(unloadBy.getCustomer(), unloadBy.getStoreableParameters(), amount));
				}

			}
		}

		if (sorted.size() != containersToSort.size()) {
			logger.error("There are not searverd customers in the Jsprit solution.");
			throw new RuntimeException("There are not searverd customers in the Jsprit solution.");
		}

		logger.info("Sorted customers {}", sorted);
		return sorted;
	}

	private CustomersStillToServe getUnloadByByLocation(List<CustomersStillToServe> containersToSort, Location location) {
		CustomersStillToServe result = null;
		for (CustomersStillToServe unloadContainer : containersToSort) {
			vrpsim.core.model.network.Location l = ((INode) unloadContainer.getCustomer().getVRPSimulationModelStructureElementParameters().getHome()).getLocation();
			if (l.equals(transformTo(location))) {
				result = unloadContainer;
				break;
			}
		}

		if (result == null) {
			logger.error("Something went wrong by finding ICustoemr with the helpf of the location.");
			throw new RuntimeException("Something went wrong by finding ICustoemr with the helpf of the location.");
		}

		return result;
	}

	private vrpsim.core.model.network.Location transformTo(Location location) {
		return new vrpsim.core.model.network.Location(location.getCoordinate().getX(), location.getCoordinate().getY(), null);
	}

	private List<Service> buildJspritServices(List<CustomersStillToServe> containersToSort) throws VRPArithmeticException {
		List<Service> result = new ArrayList<Service>();
		for (CustomersStillToServe container : containersToSort) {
			ICustomer customer = container.getCustomer();
			int amount = container.getAmount();

			String id = customer.getVRPSimulationModelElementParameters().getId();
			int WEIGHT_INDEX = 0;// order.getStorableType().getCanStoreTypes().get(0).getlId().intValue();
			double coordX = 0.0;
			double coordY = 0.0;
			if (customer.getVRPSimulationModelStructureElementParameters().getHome() instanceof INode) {
				coordX = ((INode) customer.getCurrentPlace()).getLocation().getX();
				coordY = ((INode) customer.getCurrentPlace()).getLocation().getY();
			}

			// @SuppressWarnings("unchecked")
			Builder<Delivery> serviceBuilder = Delivery.Builder.newInstance(id);
			serviceBuilder.addSizeDimension(WEIGHT_INDEX, amount);
			serviceBuilder.setLocation(Location.newInstance(coordX, coordY));
			Delivery tmpService = serviceBuilder.build();
			result.add(tmpService);

			logger.debug("Service build: {}", tmpService);
		}
		return result;
	}

	private List<VehicleImpl> buildJspritVehicles(IVehicle vehicle, vrpsim.core.model.network.Location currentLocation) throws VRPArithmeticException {

		List<VehicleImpl> result = new ArrayList<VehicleImpl>();

		VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance(vehicle.getVRPSimulationModelElementParameters().getId() + "_type");
		for (CanStoreType type : vehicle.getAllCanStoreTypes()) {
			int WEIGHT_INDEX = 0;// type.getlId().intValue();
			// int capa = maxCapacity == null ?
			// vehicle.getFreeCapacity(type).getValue().intValue() :
			// maxCapacity.intValue();
			int capa = vehicle.getFreeCapacity(type).getValue().intValue();
			vehicleTypeBuilder.addCapacityDimension(WEIGHT_INDEX, capa);
		}

		double coordX = 0.0;
		double coordY = 0.0;
		coordX = currentLocation.getX();
		coordY = currentLocation.getY();

		VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicle.getVRPSimulationModelElementParameters().getId());
		vehicleBuilder.setStartLocation(Location.newInstance(coordX, coordY));
		vehicleBuilder.setType(vehicleTypeBuilder.build());
//		vehicleBuilder.setReturnToDepot(false);
		VehicleImpl tmpVehicle = vehicleBuilder.build();
		result.add(tmpVehicle);
		logger.debug("Vehicle build: {}", tmpVehicle);

		return result;

	}


	
}
