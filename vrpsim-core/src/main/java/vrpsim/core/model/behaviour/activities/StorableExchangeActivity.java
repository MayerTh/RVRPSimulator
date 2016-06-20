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
import vrpsim.core.model.behaviour.activities.util.StorableExchangeJob;
import vrpsim.core.model.structure.util.storage.Capacity;
import vrpsim.core.model.structure.util.storage.IStorable;
import vrpsim.core.model.util.exceptions.NetworkException;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;

/**
 * Models the exchange of storables during a {@link ITour}.
 * 
 * @date 19.02.2016
 * @author thomas.mayer@unibw.de
 */
public class StorableExchangeActivity implements IActivity {

	private static Logger logger = LoggerFactory.getLogger(StorableExchangeActivity.class);

	private final StorableExchangeJob job;

	public StorableExchangeActivity(final StorableExchangeJob job) {
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
			throws VRPArithmeticException, NetworkException {

		ActivityPrepareActionResult activityDoActionResult = null;

		Capacity pickupCapacity = job.getStoreableParameters().getCapacity().mul(job.getNumber().doubleValue());
		Capacity availableCapacity = job.getStorableSource().getCurrentCapacity(
				job.getStoreableParameters().getStorableType());

		logger.debug("Prepare the exchange of {} goods from {} (capacity={}) to {}.", pickupCapacity.getValue(),
				this.job.getStorableSource().getVRPSimulationModelElementParameters().getId(),
				availableCapacity.getValue(),
				this.job.getStorableTarget().getVRPSimulationModelElementParameters().getId());

		if (!job.getStorableSource().isAvailable(clock)) {
			activityDoActionResult = new ActivityPrepareActionResult(false);
			activityDoActionResult.setResponsibleElement(job.getStorableSource());
			return activityDoActionResult;
		}

		if (!job.getStorableTarget().isAvailable(clock)) {
			activityDoActionResult = new ActivityPrepareActionResult(false);
			activityDoActionResult.setResponsibleElement(job.getStorableTarget());
			return activityDoActionResult;
		}

		if (availableCapacity.isSmaller(pickupCapacity)) {
			activityDoActionResult = new ActivityPrepareActionResult(false);
			activityDoActionResult.setResponsibleElement(job.getStorableSource());
			return activityDoActionResult;
		}

		if (!job.getStorableTarget().canStore(job.getStoreableParameters().getStorableType(), pickupCapacity)) {
			activityDoActionResult = new ActivityPrepareActionResult(false);
			activityDoActionResult.setResponsibleElement(job.getStorableTarget());
			return activityDoActionResult;
		}

		job.getStorableSource().allocateBy(job.getStorableTarget());
		job.getStorableTarget().allocateBy(job.getStorableSource());

		activityDoActionResult = new ActivityPrepareActionResult(true);
		ITime timeTillDoAction = clock.getCurrentSimulationTime().max(
				job.getStorableSource().getServiceTime(this.job, clock),
				job.getStorableTarget().getServiceTime(job, clock));
		activityDoActionResult.setTimeTillDoAction(timeTillDoAction);

		context.addElementsUpdated(job.getStorableTarget());
		context.addElementsUpdated(job.getStorableSource());

		return activityDoActionResult;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * vrpsim.core.model.behaviour.IActivity#doAction(vrpsim.core.simulator.
	 * IClock)
	 */
	@Override
	public ActivityDoActionResult doAction(IClock clock, TourContext context)
			throws VRPArithmeticException, StorageException {

		for (int i = 0; i < job.getNumber(); i++) {
			IStorable storable = job.getStorableSource().unload(job.getStoreableParameters().getStorableType());
			job.getStorableTarget().load(storable);
		}

		double capaSource = job.getStorableSource()
				.getCurrentCapacity(job.getStoreableParameters().getStorableType())
				.getValue();
		double capaTarget = job.getStorableTarget()
				.getCurrentCapacity(job.getStoreableParameters().getStorableType())
				.getValue();
		String source = job.getStorableSource().getVRPSimulationModelElementParameters().getId();
		String target = job.getStorableTarget().getVRPSimulationModelElementParameters().getId();

		logger.debug("Exchange of {} goods executed. {} new capacity={}, {} new capacity={}", job.getNumber(), source,
				capaSource, target, capaTarget);

		job.getStorableSource().freeFrom(job.getStorableTarget());
		job.getStorableTarget().freeFrom(job.getStorableSource());

		context.addElementsUpdated(job.getStorableTarget());
		context.addElementsUpdated(job.getStorableSource());

		return new ActivityDoActionResult(0.0);
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
		String sourceId = this.job.getStorableSource().getVRPSimulationModelElementParameters().getId();
		String targetId = this.job.getStorableTarget().getVRPSimulationModelElementParameters().getId();
		String targetHomeId = this.job.getStorableTarget().getVRPSimulationModelStructureElementParameters().getHome()
				.getVRPSimulationModelElementParameters().getId();
		String number = this.job.getNumber().toString();
		stringBuffer
				.append("(Exchange " + number + " from " + sourceId + " to " + targetId + " at " + targetHomeId + ")");
		return stringBuffer.toString();
	}
}
