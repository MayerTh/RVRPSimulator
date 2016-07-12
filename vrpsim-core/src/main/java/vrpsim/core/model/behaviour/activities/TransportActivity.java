/**
 * Copyright (C) 2016 Thomas Mayer (thomas.mayer@unibw.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vrpsim.core.model.behaviour.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.behaviour.activities.util.ActivityDoActionResult;
import vrpsim.core.model.behaviour.activities.util.ActivityPrepareActionResult;
import vrpsim.core.model.behaviour.activities.util.IJob;
import vrpsim.core.model.behaviour.activities.util.ServiceTimeCalculationInformationContainer;
import vrpsim.core.model.behaviour.activities.util.TransportJob;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.behaviour.tour.TourContext;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IWay;
import vrpsim.core.model.util.exceptions.BehaviourException;
import vrpsim.core.model.util.exceptions.NetworkException;
import vrpsim.core.model.util.exceptions.detail.InvalidOperationForCapacity;
import vrpsim.core.model.util.exceptions.detail.NoDirectConnectionBetweenNodesException;
import vrpsim.core.model.util.exceptions.detail.NoStorageForTypeException;
import vrpsim.core.model.util.exceptions.detail.StorageOutOfStockException;
import vrpsim.core.model.util.exceptions.detail.StorageOverflowException;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;

/**
 * Models the transport of storables during a {@link ITour}.
 * 
 * @date 19.02.2016
 * @author thomas.mayer@unibw.de
 */
public class TransportActivity implements IActivity {

	private static Logger logger = LoggerFactory.getLogger(TransportActivity.class);

	private final TransportJob job;

	private double costs;
	private IActivity successor;
	private IWay usedWay;

	public TransportActivity(final TransportJob job) {
		this.job = job;
	}

	@Override
	public ActivityPrepareActionResult prepareAction(IClock clock, TourContext context)
			throws ArithmeticException, NetworkException, BehaviourException {

		logger.debug("Prepare the transport of storables with {} from {} to {}.",
				context.getVehicle().getVRPSimulationModelElementParameters().getId(),
				context.getCurrentPlace().getVRPSimulationModelElementParameters().getId(),
				job.getTransportTarget().getVRPSimulationModelElementParameters().getId());

		this.validate(context);
		for (IWay way : ((INode) context.getCurrentPlace()).getWays()) {
			if (way.getTarget().getVRPSimulationModelElementParameters().getId()
					.equals(job.getTransportTarget().getVRPSimulationModelElementParameters().getId())) {
				this.usedWay = way;
				break;
			}
		}

		if (usedWay == null) {
			throw new NoDirectConnectionBetweenNodesException("There is no direct connection between node "
					+ context.getCurrentPlace().getVRPSimulationModelElementParameters().getId() + " and node "
					+ job.getTransportTarget().getVRPSimulationModelElementParameters().getId() + ". "
					+ this.getClass().getSimpleName()
					+ " requires a direct connection between nodes, if you can not garuantee this direct connection please use another implementation of TransportActivity.");
		}

		ActivityPrepareActionResult actionResult;
		if (!this.usedWay.isAvailable(clock)) {
			actionResult = new ActivityPrepareActionResult(false, "Way for transportation is blocked.");
			actionResult.setResponsibleElement(this.usedWay);
		} else {
			ServiceTimeCalculationInformationContainer container = new ServiceTimeCalculationInformationContainer(
					context.getVehicle(), context.getDriver(), null, null, null);
			ITime serviceTime = this.usedWay.getServiceTime(container, clock);
			this.costs = this.usedWay.getDistance();
			this.usedWay.allocateBy(this);
			actionResult = new ActivityPrepareActionResult(true,
					"Transport activity successfully prepared, transport will take " + serviceTime + ".");
			actionResult.setTimeTillDoAction(serviceTime);
		}

		return actionResult;
	}

	@Override
	public ActivityDoActionResult doAction(IClock clock, TourContext context) throws StorageOutOfStockException,
			NoStorageForTypeException, InvalidOperationForCapacity, StorageOverflowException {

		if (this.usedWay != null) {
			this.usedWay.releaseFrom(this);
			this.usedWay = null;
		}

		context.setCurrentPlace(this.job.getTransportTarget());
		logger.debug("Transport executed. New current place set to {}.",
				context.getCurrentPlace().getVRPSimulationModelElementParameters().getId());

		return new ActivityDoActionResult(this.costs);
	}

	@Override
	public IJob getJob() {
		return this.job;
	}

	@Override
	public void allocatedElementStateChanged(IVRPSimulationModelElement element) throws BehaviourException {
		if (this.usedWay != null) {
			throw new BehaviourException(
					"During transportation the state of the allocated IWay changed. Can not handle this state change yet.");
		}
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

}
