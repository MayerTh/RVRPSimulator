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
package vrpsim.r.util.api.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vrprep.model.instance.Instance;
import org.vrprep.model.instance.Instance.Network.Nodes.Node;
import org.vrprep.model.instance.Instance.Requests.Request;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.behaviour.Behaviour;
import vrpsim.core.model.behaviour.activities.IActivity;
import vrpsim.core.model.behaviour.activities.impl.TransportActivity;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.behaviour.tour.TourContext;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.network.impl.Location;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.r.util.api.model.RModel;
import vrpsim.r.util.api.model.RModelAdvanced;
import vrpsim.r.util.api.model.RPoint;
import vrpsim.r.util.api.model.RRaster;

public class RModelTransformator {

	private static Logger logger = LoggerFactory.getLogger(RModelTransformator.class);
	
	public static RModel transformTourContextTo(TourContext tourContext, IActivity startActivity) throws CannotCreateRModelException {

		if (tourContext.getPlaceHistory().size() < 1) {
			throw new CannotCreateRModelException("No elements in place history from tour context.");
		}

		RPoint start = new RPoint(
				((INode) tourContext.getVehicle().getVRPSimulationModelStructureElementParameters().getHome()).getLocation().getX(),
				((INode) tourContext.getVehicle().getVRPSimulationModelStructureElementParameters().getHome()).getLocation().getY(), false,
				false);

		List<RPoint> orderedWay = new ArrayList<>();
		for (int i = 0; i < tourContext.getPlaceHistory().size() - 1; i++) {
			IVRPSimulationModelNetworkElement element = tourContext.getPlaceHistory().get(i);
			if (element instanceof INode) {
				RPoint point = new RPoint(((INode) element).getLocation().getX(), ((INode) element).getLocation().getY(), false, false);
				if (!point.equals(start)) {
					orderedWay.add(point);
				}
			}
		}

		RPoint end = new RPoint(((INode) tourContext.getPlaceHistory().get(tourContext.getPlaceHistory().size() - 1)).getLocation().getX(),
				((INode) tourContext.getPlaceHistory().get(tourContext.getPlaceHistory().size() - 1)).getLocation().getY(), false, false);
		RPoint[] minmax = minmax(startActivity);
		return new RModel(start, orderedWay, end, minmax[1], minmax[0], true);

	}

	public static RModel transformStartActivityTo(IActivity startActivity, TourContext tourContext) {

		RPoint start = new RPoint(
				((INode) tourContext.getVehicle().getVRPSimulationModelStructureElementParameters().getHome()).getLocation().getX(),
				((INode) tourContext.getVehicle().getVRPSimulationModelStructureElementParameters().getHome()).getLocation().getY(), false,
				false);
		RPoint current = new RPoint(((INode) tourContext.getVehicle().getCurrentPlace()).getLocation().getX(),
				((INode) tourContext.getVehicle().getCurrentPlace()).getLocation().getY(), false, false);

		List<RPoint> orderedWay = new ArrayList<>();
		IActivity workWith = startActivity;
		while (workWith != null) {
			if (workWith instanceof TransportActivity) {
				TransportActivity ta = (TransportActivity) workWith;
				RPoint point = new RPoint(((INode) ta.getTransportTarget()).getLocation().getX(),
						((INode) ta.getTransportTarget()).getLocation().getY(), false, false);
				point.setHighlight(current.equals(point));
				orderedWay.add(point);
			}

			// if (workWith instanceof UnloadActivity) {
			// LoadUnloadJob job = (LoadUnloadJob) workWith.getJob();
			// RPoint point = new RPoint(((INode)
			// job.getLoadingPartner().getVRPSimulationModelStructureElementParameters()).getLocation().getX(),
			// ((INode)
			// job.getLoadingPartner().getVRPSimulationModelStructureElementParameters()).getLocation().getY(),
			// false);
			// point.setHighlight(current.equals(point));
			// orderedWay.add(point);
			// }
			workWith = workWith.getSuccessor();
		}

		RPoint end = orderedWay.remove(orderedWay.size() - 1);
		RPoint[] minmax = minmax(startActivity);
		return new RModel(start, orderedWay, end, minmax[1], minmax[0], true);

	}

	public static RModel transformStaticVRPREPModel(Instance model) {
		return transform(model, null);
	}

	public static RModel transformDynamicVRPREPModel(DynamicVRPREPModel model) {
		return transform(model.getVRPREPInstance(), model.getDynamicRequestInformation().keySet());
	}

	private static RModel transform(Instance model, Set<BigInteger> toHighlight) {

		if (toHighlight == null) {
			toHighlight = new HashSet<>();
		}

		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		List<RPoint> listOfPoints = new ArrayList<>();
		for (Request request : model.getRequests().getRequest()) {
			BigInteger nodeId = request.getNode();
			Node node = getNodeById(nodeId, model);
			boolean highlight = toHighlight.contains(request.getId());
			listOfPoints.add(new RPoint(node.getCx(), node.getCy(), highlight, false));

			if (maxX < node.getCx()) {
				maxX = node.getCx();
			}

			if (maxY < node.getCy()) {
				maxY = node.getCy();
			}

			if (minX > node.getCx()) {
				minX = node.getCx();
			}

			if (minY > node.getCy()) {
				minY = node.getCy();
			}
		}

		Set<BigInteger> depots = new HashSet<>();
		model.getFleet().getVehicleProfile().stream().forEach(e -> depots.addAll(e.getDepartureNode()));
		for (BigInteger nodeId : depots) {
			Node node = getNodeById(nodeId, model);
			listOfPoints.add(new RPoint(node.getCx(), node.getCy(), false, true));

			if (maxX < node.getCx()) {
				maxX = node.getCx();
			}

			if (maxY < node.getCy()) {
				maxY = node.getCy();
			}

			if (minX > node.getCx()) {
				minX = node.getCx();
			}

			if (minY > node.getCy()) {
				minY = node.getCy();
			}
		}

		Node departureNode = getNodeById(model.getFleet().getVehicleProfile().get(0).getDepartureNode().get(0), model);
		RPoint departure = new RPoint(departureNode.getCx(), departureNode.getCy(), false, true);
		return new RModel(departure, listOfPoints, departure, new RPoint(maxX, maxY, false, false), new RPoint(minX, minY, false, false),
				false);

	}

	private static Node getNodeById(BigInteger nodeId, Instance instance) {
		Node result = null;
		for (Node node : instance.getNetwork().getNodes().getNode()) {
			if (node.getId().equals(nodeId)) {
				result = node;
				break;
			}
		}
		return result;
	}

	private static RPoint[] minmax(IActivity startActivity) {

		RPoint maxPoint = new RPoint(Double.MIN_VALUE, Double.MIN_VALUE, false, false);
		RPoint minPoint = new RPoint(Double.MAX_VALUE, Double.MAX_VALUE, false, false);
		IActivity workWith = startActivity;
		while (workWith != null) {
			if (workWith instanceof TransportActivity) {
				TransportActivity ta = (TransportActivity) workWith;
				double x = ((INode) ta.getTransportTarget()).getLocation().getX();
				double y = ((INode) ta.getTransportTarget()).getLocation().getY();

				if (x > maxPoint.getX()) {
					maxPoint.setX(x);
				}
				if (y > maxPoint.getY()) {
					maxPoint.setY(y);
				}
				if (x < minPoint.getX()) {
					minPoint.setX(x);
				}
				if (y < minPoint.getY()) {
					minPoint.setY(y);
				}

			}
			workWith = workWith.getSuccessor();
		}

		RPoint[] result = new RPoint[2];
		result[0] = minPoint;
		result[1] = maxPoint;
		return result;

	}

	public static RModelAdvanced transform(VRPSimulationModel model) {

		List<RPoint> points = new LinkedList<>();

		Location lDepot = model.getNetworkService().getLocationByDepot(model.getStructureService().getDepots().get(0));
		RPoint depot = new RPoint(lDepot.getX(), lDepot.getY(), false, true);
		points.add(depot);

		for (ICustomer customer : model.getStructureService().getCustomers()) {
			Location location = model.getNetworkService().getLocationByCustomer(customer);
			RPoint rp = new RPoint(location.getX(), location.getY(), false, false);
			if (customer.isHasDynamicEvents()) {
				rp.setHighlight(true);
			}
			points.add(rp);
		}

		return new RModelAdvanced(null, points, null);
	}

	public static RModelAdvanced transformPlanned(VRPSimulationModel model, boolean executed, RRaster raster) {

		List<List<RPoint>> connectedPointList = new ArrayList<>();
		Behaviour be = null;
		if(!executed) {
			be = model.getBehaviourProvider().getBehaviourBeforeInitialization(model.getStructureService(),model.getNetworkService());
		} else {
			be = model.getBehaviourProvider().getBehaviourFromInitialBehaviourProvider();
		}
				
		for (ITour tour : be.getTours()) {
			List<RPoint> connectedPoints = new ArrayList<>();
			IActivity workWith = tour.getStartActivity();
			
			while (workWith != null) {
				
				if (workWith instanceof TransportActivity) {
					TransportActivity ta = (TransportActivity) workWith;
					INode node = (INode) ta.getTransportTarget();

					List<ICustomer> customers = model.getStructureService().getCustomersByNodeId(node);
					if (!customers.isEmpty()) {
						RPoint point = new RPoint(node.getLocation().getX(), ((INode) ta.getTransportTarget()).getLocation().getY(), false,
								false);
						
						if (customers.get(0).isHasDynamicEvents()) {
							point.setHighlight(true);
						}
						connectedPoints.add(point);
					}

				}
				workWith = workWith.getSuccessor();
			}
			connectedPointList.add(connectedPoints);
		}

		List<RPoint> unconnectedPoints = new LinkedList<>();

		Location lDepot = model.getNetworkService().getLocationByDepot(model.getStructureService().getDepots().get(0));
		RPoint depot = new RPoint(lDepot.getX(), lDepot.getY(), false, true);
		unconnectedPoints.add(depot);

		if (!executed) {
			for (ICustomer customer : model.getStructureService().getCustomers()) {
				if (customer.isHasDynamicEvents()) {
					Location location = model.getNetworkService().getLocationByCustomer(customer);
					RPoint rp = new RPoint(location.getX(), location.getY(), false, false);
					rp.setHighlight(true);
					unconnectedPoints.add(rp);
				}
			}
		}
		
		return new RModelAdvanced(connectedPointList, unconnectedPoints, raster);
	}

}
