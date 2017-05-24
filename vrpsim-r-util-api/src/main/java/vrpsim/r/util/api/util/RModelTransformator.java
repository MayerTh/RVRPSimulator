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
import java.util.List;

import org.vrprep.model.instance.Instance;
import org.vrprep.model.instance.Instance.Network.Nodes.Node;
import org.vrprep.model.instance.Instance.Requests.Request;

import vrpsim.core.model.behaviour.activities.IActivity;
import vrpsim.core.model.behaviour.activities.TransportActivity;
import vrpsim.core.model.behaviour.activities.util.TransportJob;
import vrpsim.core.model.behaviour.tour.TourContext;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.r.util.api.model.RModel;
import vrpsim.r.util.api.model.RPoint;

public class RModelTransformator {

	public static RModel transformTourContextTo(TourContext tourContext, IActivity startActivity) throws CannotCreateRModelException {

		if (tourContext.getPlaceHistory().size() < 1) {
			throw new CannotCreateRModelException("No elements in place history from tour context.");
		}

		RPoint start = new RPoint(((INode) tourContext.getVehicle().getVRPSimulationModelStructureElementParameters().getHome()).getLocation().getX(),
				((INode) tourContext.getVehicle().getVRPSimulationModelStructureElementParameters().getHome()).getLocation().getY(), false);

		List<RPoint> orderedWay = new ArrayList<>();
		for (int i = 0; i < tourContext.getPlaceHistory().size() - 1; i++) {
			IVRPSimulationModelNetworkElement element = tourContext.getPlaceHistory().get(i);
			if (element instanceof INode) {
				RPoint point = new RPoint(((INode) element).getLocation().getX(), ((INode) element).getLocation().getY(), false);
				if (!point.equals(start)) {
					orderedWay.add(point);
				}
			}
		}

		RPoint end = new RPoint(((INode) tourContext.getPlaceHistory().get(tourContext.getPlaceHistory().size() - 1)).getLocation().getX(),
				((INode) tourContext.getPlaceHistory().get(tourContext.getPlaceHistory().size() - 1)).getLocation().getY(), false);

		return new RModel(start, orderedWay, end, max(startActivity), true);

	}

	public static RModel transformStartActivityTo(IActivity startActivity, TourContext tourContext) {

		RPoint start = new RPoint(((INode) tourContext.getVehicle().getVRPSimulationModelStructureElementParameters().getHome()).getLocation().getX(),
				((INode) tourContext.getVehicle().getVRPSimulationModelStructureElementParameters().getHome()).getLocation().getY(), false);
		RPoint current = new RPoint(((INode) tourContext.getCurrentPlace()).getLocation().getX(), ((INode) tourContext.getCurrentPlace()).getLocation().getY(), false);

		List<RPoint> orderedWay = new ArrayList<>();
		IActivity workWith = startActivity;
		while (workWith != null) {
			if (workWith instanceof TransportActivity) {
				TransportJob job = (TransportJob) workWith.getJob();
				RPoint point = new RPoint(((INode) job.getTransportTarget()).getLocation().getX(), ((INode) job.getTransportTarget()).getLocation().getY(), false);
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
		return new RModel(start, orderedWay, end, max(startActivity), true);

	}

	public static RModel transformDynamicVRPREPModel(DynamicVRPREPModel model) {
		
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		List<RPoint> listOfPoints = new ArrayList<>();
		for(Request request : model.getVRPREPInstance().getRequests().getRequest()) {
			BigInteger nodeId = request.getNode();
			Node node = getNodeById(nodeId, model.getVRPREPInstance());
			boolean highlight = model.getDynamicRequestInformation().containsKey(request.getId());
			listOfPoints.add(new RPoint(node.getCx(), node.getCy(), highlight));
			
			if(maxX < node.getCx()) {
				maxX = node.getCx();
			}
			
			if(maxY < node.getCy()) {
				maxY = node.getCy();
			}
		}
		
		Node departureNode = getNodeById(model.getVRPREPInstance().getFleet().getVehicleProfile().get(0).getDepartureNode().get(0), model.getVRPREPInstance());
		RPoint departure = new RPoint(departureNode.getCx(), departureNode.getCy(), false);
		return new RModel(departure, listOfPoints, departure, new RPoint(maxX, maxY, false), false);
		
	}
	
	private static Node getNodeById(BigInteger nodeId, Instance instance) {
		Node result = null;
		for(Node node : instance.getNetwork().getNodes().getNode()) {
			if(node.getId().equals(nodeId)) {
				result = node;
				break;
			}
		}
		return result;
	}

	private static RPoint max(IActivity startActivity) {

		RPoint maxPoint = new RPoint(Double.MIN_VALUE, Double.MIN_VALUE, false);
		IActivity workWith = startActivity;
		while (workWith != null) {
			if (workWith instanceof TransportActivity) {

				TransportJob job = (TransportJob) workWith.getJob();
				double x = ((INode) job.getTransportTarget()).getLocation().getX();
				double y = ((INode) job.getTransportTarget()).getLocation().getY();

				if (x > maxPoint.getX()) {
					maxPoint.setX(x);
				}
				if (y > maxPoint.getY()) {
					maxPoint.setY(y);
				}

			}
			workWith = workWith.getSuccessor();
		}

		return maxPoint;

	}

}
