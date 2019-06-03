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
package vrpsim.examples.visualization;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vrprep.model.instance.Instance;
import org.vrprep.model.instance.Instance.Fleet.VehicleProfile;
import org.vrprep.model.instance.Instance.Network;
import org.vrprep.model.instance.Instance.Network.Nodes.Node;
import org.vrprep.model.instance.Instance.Requests.Request;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Delivery;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.job.Service.Builder;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;

public class JspritVRPREPInstanceSolver {

	private static Logger logger = LoggerFactory.getLogger(JspritVRPREPInstanceSolver.class);

	public VehicleRoutingProblemSolution solve(Instance instance, boolean overwriteVehicleCapacityWithMaxCapacity) {

		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		vrpBuilder.addAllJobs(buildJspritServices(instance.getRequests().getRequest(), instance.getNetwork()));
		
		Double maxCapa = null;
		if(overwriteVehicleCapacityWithMaxCapacity) {
			maxCapa = getMaxNeededCapacity(instance.getRequests().getRequest());
		}
		
		vrpBuilder.addAllVehicles(buildJspritVehicles(instance.getFleet().getVehicleProfile(), instance.getNetwork(), maxCapa));

		VehicleRoutingProblem problem = vrpBuilder.build();

		VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);
		Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
		VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

		return bestSolution;
	}
	
	private Double getMaxNeededCapacity(List<Request> list) {
		return list.stream().mapToDouble((Request::getQuantity)).sum();
	}

	private List<Service> buildJspritServices(List<Request> list, Network network) {
		List<Service> result = new ArrayList<Service>();

		for (Request request : list) {

			String id = request.getId().toString();
			int amount = request.getQuantity().intValue();
			int WEIGHT_INDEX = 0;// order.getStorableType().getCanStoreTypes().get(0).getlId().intValue();
			double coordX = 0.0;
			double coordY = 0.0;

			Node node = getNode(request.getNode(), network);
			coordX = node.getCx();
			coordY = node.getCy();

			// Here no TimeWindow considered
			// double start = request.get;
			// double end = order.getLatestDueDate().getDoubleValue();

			Builder<Delivery> serviceBuilder = Delivery.Builder.newInstance(id);
			serviceBuilder.addSizeDimension(WEIGHT_INDEX, amount);
			serviceBuilder.setLocation(Location.newInstance(coordX, coordY));
			serviceBuilder.setServiceTime(request.getServiceTime() == null ? 0.0 : request.getServiceTime());

			// Bug in jsprit: by only using addTimeWindow -> time window
			// is
			// considered during solution finding, but not in the
			// toString method
			// serviceBuilder.addTimeWindow(TimeWindow.newInstance(start, end));
			// serviceBuilder.setTimeWindow(TimeWindow.newInstance(start, end));

			Delivery tmpService = serviceBuilder.build();
			result.add(tmpService);

			logger.debug("Service build: {}", tmpService);
		}
		return result;
	}

	private List<VehicleImpl> buildJspritVehicles(List<VehicleProfile> list, Network network, Double maxCapa) {

		List<VehicleImpl> result = new ArrayList<VehicleImpl>();

		int vpc = 0;
		for (VehicleProfile vehicleProfile : list) {
			String idProfile = "vehicleProfile-" + (vpc++);
			VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance(idProfile);
			int WEIGHT_INDEX = 0;
			
			int capa = maxCapa == null ? vehicleProfile.getCapacity().intValue() : maxCapa.intValue();
			vehicleTypeBuilder.addCapacityDimension(WEIGHT_INDEX, capa);
			VehicleTypeImpl vehicleTypeImpl = vehicleTypeBuilder.build();
			
			int numberOfVehicles = vehicleProfile.getNumber() == null ?  1 : vehicleProfile.getNumber().intValue();
			for (int i = 0; i < numberOfVehicles; i++) {

				Node node = getNode(vehicleProfile.getDepartureNode().get(0), network);

				double coordX = node.getCx();
				double coordY = node.getCy();

				String idVehicle =  idProfile + ":vehicle-" + i;
				VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(idVehicle);
				vehicleBuilder.setStartLocation(Location.newInstance(coordX, coordY));
				vehicleBuilder.setType(vehicleTypeImpl);
				VehicleImpl tmpVehicle = vehicleBuilder.build();
				result.add(tmpVehicle);
				logger.debug("Vehicle build: {}", tmpVehicle);
			}
		}

		return result;

	}

	private Node getNode(BigInteger nodeId, Network network) {
		Node result = null;
		for (Node node : network.getNodes().getNode()) {
			if (node.getId().equals(nodeId)) {
				result = node;
				break;
			}
		}
		return result;
	}

}
