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
package vrpsim.core.model.behaviour.activities.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.behaviour.activities.IActivity;
import vrpsim.core.model.behaviour.tour.Cost;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.behaviour.tour.TourContext;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.network.IWay;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementMovable;
import vrpsim.core.model.util.exceptions.BehaviourException;
import vrpsim.core.model.util.exceptions.detail.NoRoutingPossibleException;
import vrpsim.core.model.util.policies.IRoutingPolicy;
import vrpsim.core.model.util.policies.impl.Route;
import vrpsim.core.simulator.IClock;

/**
 * Models the transport of storables during a {@link ITour}.
 * 
 * @date 19.02.2016
 * @author thomas.mayer@unibw.de
 */
public class TransportActivity implements IActivity {

	private static Logger logger = LoggerFactory.getLogger(TransportActivity.class);
	private final IVRPSimulationModelNetworkElement transportTarget;
	private final IRoutingPolicy routingPolicy;
	private IActivity successor;

	private Route route;
	private boolean isPrepared = false;

	public TransportActivity(final IVRPSimulationModelNetworkElement transportTarget, final IRoutingPolicy routingPolicy) {
		this.transportTarget = transportTarget;
		this.routingPolicy = routingPolicy;
	}
	
	@Override
	public void reset() {
		this.route = null;
		this.isPrepared = false;
	}

	public IVRPSimulationModelNetworkElement getTransportTarget() {
		return transportTarget;
	}

	@Override
	public ActivityPrepareActionResult prepareAction(IClock clock, TourContext context)
			throws BehaviourException, NoRoutingPossibleException {

		IVRPSimulationModelStructureElementMovable movable = context.getVehicle();
		IVRPSimulationModelNetworkElement source = context.getVehicle().getCurrentPlace();

		logger.debug("Prepare the transport of storables with {} from {} to {}.", movable.getVRPSimulationModelElementParameters().getId(),
				source.getVRPSimulationModelElementParameters().getId(), transportTarget.getVRPSimulationModelElementParameters().getId());

		this.validate(context);
		ActivityPrepareActionResult actionResult = null;
		this.route = this.routingPolicy.getRouting(source, transportTarget, movable, clock);

		if (!context.getVehicle().isAvailableForInteractionInAllocation(clock, this)) {
			actionResult = new ActivityPrepareActionResult(false, "The vehicle is not available for interaction within allocation.");
			actionResult.setResponsibleElement(context.getVehicle());
		}

		if (actionResult == null) {
			for (IWay way : route.getUsedWays()) {
				if (!way.isAvailableForAllocation(clock)) {
					actionResult = new ActivityPrepareActionResult(false, "The way is not available for allocation.");
					actionResult.setResponsibleElement(way);
					break;
				}
			}
		}

		if (actionResult == null) {
			route.getUsedWays().parallelStream().forEach(e -> e.allocateBy(this));
			for (IWay way : route.getUsedWays()) {
				if (!way.isAvailableForInteractionInAllocation(clock, this)) {
					actionResult = new ActivityPrepareActionResult(false, "The way is not available for interaction within allocation.");
					actionResult.setResponsibleElement(way);
					route.getUsedWays().parallelStream().forEach(e -> e.releaseFrom(this));
					break;
				}
			}
		}

		if (actionResult == null) {
			route.getUsedWays().parallelStream().forEach(e -> e.allocateBy(this));
			String msg = "Transport activity successfully prepared, transport will take " + route.getRountingTime() + " for distance "
					+ route.getRountingDistance() + ".";
			actionResult = new ActivityPrepareActionResult(true, msg);
			actionResult.setReletaiveTimeTillDoAction(route.getRountingTime());
			logger.debug(msg);
		}

		return actionResult;
	}

	@Override
	public void doAction(IClock clock, TourContext context) {
		route.getUsedWays().parallelStream().forEach(e -> e.releaseFrom(this));
		Cost cost = new Cost(route.getRountingTime(), 0L, 0L, route.getRountingDistance());
		context.updateTourContextElementsAndCosts(transportTarget, cost);
		String transportTargetId = this.transportTarget.getVRPSimulationModelElementParameters().getId();
		logger.info("Transport executed. New current place set to {}.", transportTargetId);
	}
	
	@Override
	public String toString() {
		return "Transport to " + this.transportTarget.getVRPSimulationModelElementParameters().getId();
	}

	@Override
	public void allocatedElementStateChanged(IVRPSimulationModelElement element) throws BehaviourException {
	}

	@Override
	public IActivity getSuccessor() {
		return this.successor;
	}

	@Override
	public void setSuccessor(IActivity successor) {
		this.successor = successor;
	}

	@Override
	public void validate(TourContext context) throws BehaviourException {
		// Not relevant.
	}

	@Override
	public List<IVRPSimulationModelElement> getToAllocate() {
		// No structural element to allocate.
		return new ArrayList<>();
	}

	@Override
	public boolean isCustomerInteraction() {
		return false;
	}

	@Override
	public IVRPSimulationModelNetworkElement getLocation() {
		return this.transportTarget;
	}
	
	@Override
	public boolean isPrepared() {
		return this.isPrepared;
	}

	public void setPrepared(boolean isPrepared) {
		this.isPrepared = isPrepared;
	}

}
