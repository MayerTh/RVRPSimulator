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
package vrpsim.simulationmodel.initialbehaviour.generator.jspritimpl.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Delivery;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.job.Service.Builder;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter.Print;
import com.graphhopper.jsprit.core.util.Solutions;

import vrpsim.core.model.network.INode;
import vrpsim.core.model.solution.Order;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.customer.IStaticCustomer;
import vrpsim.core.model.structure.util.storage.CanStoreType;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;

public class JspritSolver {

	private static Logger logger = LoggerFactory.getLogger(JspritSolver.class);

	public VehicleRoutingProblemSolution solve(StructureService structureService, String instanceName, String statisticsOutputFolder, boolean createStatistics)
			throws VRPArithmeticException {

		String name = instanceName;

		// Double maxCapa = null;
		// if (overwriteVehicleCapacityWithMaxCapacity) {
		// maxCapa = getMaxNeededCapacity(structureService.getCustomers());
		// }

		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		vrpBuilder.addAllVehicles(buildJspritVehicles(structureService.getVehicles()));
		vrpBuilder.addAllJobs(buildJspritServices(structureService.getCustomers()));

		VehicleRoutingProblem problem = vrpBuilder.build();
		if (createStatistics) {
			new Plotter(problem).plot(statisticsOutputFolder + "/jsprit-problem" + name + ".png", instanceName + " problem");
		}

		VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);
		Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
		VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

		Double cost = bestSolution.getCost();
		if (createStatistics) {
			new Plotter(problem, Solutions.bestOf(solutions)).plot(statisticsOutputFolder + "/jsprit-solution" + name + "_cost=" + round(cost, 2) + ".png",
					instanceName + " cost=" + cost);
			SolutionPrinter.print(problem, bestSolution, Print.VERBOSE);
		}

		return bestSolution;
	}

	private double round(double wert, int stellen) {
		BigDecimal b = new BigDecimal(wert);
		return b.setScale(stellen, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	private List<Service> buildJspritServices(List<ICustomer> customers) throws VRPArithmeticException {
		List<Service> result = new ArrayList<Service>();
		for (ICustomer customer : customers) {
			if (customer instanceof IStaticCustomer) {
				int ordercounter = 0;
				for (Order order : ((IStaticCustomer) customer).getStaticOrdersBeforeEventGeneration()) {
					// String id =
					// customer.getVRPSimulationModelElementParameters().getId()
					// + "-o" + (ordercounter++);
					String id = customer.getVRPSimulationModelStructureElementParameters().getHome().getVRPSimulationModelElementParameters().getId();
					int amount = order.getAmount();
					int WEIGHT_INDEX = 0;// order.getStorableType().getCanStoreTypes().get(0).getlId().intValue();
					double coordX = 0.0;
					double coordY = 0.0;
					if (customer.getCurrentPlace() instanceof INode) {
						coordX = ((INode) customer.getCurrentPlace()).getLocation().getX();
						coordY = ((INode) customer.getCurrentPlace()).getLocation().getY();
					}

					// @SuppressWarnings("unchecked")
					Builder<Delivery> serviceBuilder = Delivery.Builder.newInstance(id);
					serviceBuilder.addSizeDimension(WEIGHT_INDEX, amount);
					serviceBuilder.setLocation(Location.newInstance(coordX, coordY));
					serviceBuilder.setServiceTime(((ICustomer) order.getOwner()).getServiceTimeFunction().getTime(null, null));
					if (order.getEarliestDueDate() != null && order.getLatestDueDate() != null) {
						double start = order.getEarliestDueDate().getDoubleValue();
						double end = order.getLatestDueDate().getDoubleValue();

						logger.debug("time window start = {}", start);
						logger.debug("time window end = {}", end);

						// Bug in jsprit: by only using addTimeWindow -> time
						// window
						// is
						// considered during solution finding, but not in the
						// toString method
						serviceBuilder.addTimeWindow(TimeWindow.newInstance(start, end));
						serviceBuilder.setTimeWindow(TimeWindow.newInstance(start, end));
					}

					Delivery tmpService = serviceBuilder.build();
					result.add(tmpService);

					logger.debug("Service build: {}", tmpService);
				}
			}
		}
		return result;
	}

	private List<VehicleImpl> buildJspritVehicles(List<IVehicle> vehicles) throws VRPArithmeticException {

		List<VehicleImpl> result = new ArrayList<VehicleImpl>();

		for (IVehicle vehicle : vehicles) {
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
			if (vehicle.getCurrentPlace() instanceof INode) {
				coordX = ((INode) vehicle.getCurrentPlace()).getLocation().getX();
				coordY = ((INode) vehicle.getCurrentPlace()).getLocation().getY();
			}
			VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicle.getVRPSimulationModelElementParameters().getId());
			vehicleBuilder.setStartLocation(Location.newInstance(coordX, coordY));
			vehicleBuilder.setType(vehicleTypeBuilder.build());
			VehicleImpl tmpVehicle = vehicleBuilder.build();
			result.add(tmpVehicle);
			logger.debug("Vehicle build: {}", tmpVehicle);
		}

		return result;

	}

}
