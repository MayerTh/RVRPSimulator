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

import vrpsim.core.model.behaviour.TourContext;
import vrpsim.core.model.behaviour.IActivity;
import vrpsim.core.model.behaviour.IJob;
import vrpsim.core.model.behaviour.activities.util.ActivityDoActionResult;
import vrpsim.core.model.behaviour.activities.util.ActivityPrepareActionResult;
import vrpsim.core.model.behaviour.activities.util.StorableTransportJob;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.IWay;
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
public class StorableTransportActivity implements IActivity {

	private static Logger logger = LoggerFactory.getLogger(StorableTransportActivity.class);

	private final StorableTransportJob job;
	private double costs;

	public StorableTransportActivity(final StorableTransportJob job) {
		this.job = job;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.behaviour.IActivity#prepareAction(vrpsim.core.simulator
	 * .IClock)
	 */
	@Override
	public ActivityPrepareActionResult prepareAction(IClock clock, TourContext context)
			throws ArithmeticException, NetworkException {

		logger.debug("Prepare the transport of storables with {} from {} to {}.",
				job.getTransporter().getVRPSimulationModelElementParameters().getId(),
				context.getCurrentPlace().getVRPSimulationModelElementParameters().getId(),
				job.getTransportTarget().getVRPSimulationModelElementParameters().getId());

		ITime serviceTime = null;
		for (IWay way : ((INode) context.getCurrentPlace()).getWays()) {
			if (way.getTarget().equals(job.getTransportTarget())) {
				serviceTime = way.getServiceTime(job, clock);
				this.costs = way.getDistance();
				break;
			}
		}

		if (serviceTime == null) {
			throw new NoDirectConnectionBetweenNodesException("There is no direct connection between node "
					+ context.getCurrentPlace().getVRPSimulationModelElementParameters().getId() + " and node " + job.getTransportTarget().getVRPSimulationModelElementParameters().getId() + ". "
					+ this.getClass().getSimpleName()
					+ " requires a direct connection between nodes, if you can not garuantee this direct connection please use another implementation of TransportActivity.");
		}

		ActivityPrepareActionResult actionResult = new ActivityPrepareActionResult(true);
		actionResult.setTimeTillDoAction(serviceTime);

		return actionResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.behaviour.IActivity#doAction(vrpsim.core.simulator.
	 * IClock)
	 */
	@Override
	public ActivityDoActionResult doAction(IClock clock, TourContext context) throws StorageOutOfStockException,
			NoStorageForTypeException, InvalidOperationForCapacity, StorageOverflowException {
		context.setCurrentPlace(this.job.getTransportTarget());

		logger.debug("Transport executed. New current place set to {}.",
				context.getCurrentPlace().getVRPSimulationModelElementParameters().getId());

		return new ActivityDoActionResult(this.costs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vrpsim.core.model.behaviour.IActivity#getJob()
	 */
	@Override
	public IJob getJob() {
		return this.job;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		String targetNodeId = this.job.getTransportTarget().getVRPSimulationModelElementParameters().getId();
		stringBuffer.append("(Transport to " + targetNodeId + ")");
		return stringBuffer.toString();
	}

}
