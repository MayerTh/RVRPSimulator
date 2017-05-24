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
package vrpsim.core.model.behaviour.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.IVRPSimulationModelElement;
import vrpsim.core.model.behaviour.activities.util.ActivityDoActionResult;
import vrpsim.core.model.behaviour.activities.util.ActivityPrepareActionResult;
import vrpsim.core.model.behaviour.activities.util.IJob;
import vrpsim.core.model.behaviour.activities.util.LoadUnloadJob;
import vrpsim.core.model.behaviour.activities.util.TimeCalculationInformationContainer;
import vrpsim.core.model.behaviour.tour.TourContext;
import vrpsim.core.model.structure.util.storage.Capacity;
import vrpsim.core.model.structure.util.storage.IStorable;
import vrpsim.core.model.structure.util.storage.StorableType;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.exceptions.BehaviourException;
import vrpsim.core.model.util.exceptions.NetworkException;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.simulator.IClock;
import vrpsim.core.simulator.ITime;

/**
 * Describes the loading of a {@link IVehicle} with {@link StorableType}.
 * 
 * @date 19.02.2016
 * @author thomas.mayer@unibw.de
 */
public class UnloadActivity implements IActivity {

	private static Logger logger = LoggerFactory.getLogger(UnloadActivity.class);

	private final LoadUnloadJob job;

	private IActivity successor;
	private boolean isActive = false;

	public UnloadActivity(final LoadUnloadJob job) {
		this.job = job;
	}

	@Override
	public ActivityPrepareActionResult prepareAction(IClock clock, TourContext context)
			throws VRPArithmeticException, NetworkException, BehaviourException {

		this.validate(context);
		ActivityPrepareActionResult activityDoActionResult = null;
		this.isActive = true;

		Capacity pickupCapacity = job.getStoreableParameters().getCapacity().mul(job.getNumber().doubleValue());
		Capacity availableCapacity = context.getVehicle()
				.getCurrentCapacity(job.getStoreableParameters().getStorableType());

		logger.debug("Prepare to unload {} goods from {} (capacity={}) to {}.", pickupCapacity.getValue(),
				context.getVehicle().getVRPSimulationModelElementParameters().getId(), availableCapacity.getValue(),
				this.job.getLoadingPartner().getVRPSimulationModelElementParameters().getId());

		if (!job.getLoadingPartner().isAvailable(clock)) {
			activityDoActionResult = new ActivityPrepareActionResult(false, "Unload partner is not available.");
			activityDoActionResult.setResponsibleElement(job.getLoadingPartner());
			return activityDoActionResult;
		}

		if (availableCapacity.isSmaller(pickupCapacity)) {
			activityDoActionResult = new ActivityPrepareActionResult(false,
					"Not enough capacity in vehicle to unload.");
			activityDoActionResult.setResponsibleElement(context.getVehicle());
			return activityDoActionResult;
		}

		if (!job.getLoadingPartner().canStore(job.getStoreableParameters().getStorableType(), pickupCapacity)) {
			activityDoActionResult = new ActivityPrepareActionResult(false,
					"Can not unload, cause capacity of loading partner is full.");
			activityDoActionResult.setResponsibleElement(job.getLoadingPartner());
			return activityDoActionResult;
		}

		job.getLoadingPartner().allocateBy(this);

		activityDoActionResult = new ActivityPrepareActionResult(true, "Unloading possible");
		TimeCalculationInformationContainer container = new TimeCalculationInformationContainer(
				context.getVehicle(), context.getDriver(), job.getLoadingPartner(), job.getStoreableParameters(),
				job.getNumber());

		ITime timeTillDoAction = clock.getCurrentSimulationTime().max(
				job.getLoadingPartner().getServiceTime(container, clock),
				context.getVehicle().getServiceTime(container, clock));
		activityDoActionResult.setTimeTillDoAction(timeTillDoAction);

		context.addElementsUpdated(job.getLoadingPartner());
		context.addElementsUpdated(context.getVehicle());

		return activityDoActionResult;

	}

	@Override
	public ActivityDoActionResult doAction(IClock clock, TourContext context)
			throws VRPArithmeticException, StorageException {

		this.isActive = false;
		
		for (int i = 0; i < job.getNumber(); i++) {
			IStorable storable = context.getVehicle().unload(job.getStoreableParameters().getStorableType());
			job.getLoadingPartner().load(storable);
		}

		double capaSource = context.getVehicle().getCurrentCapacity(job.getStoreableParameters().getStorableType())
				.getValue();
		double capaTarget = job.getLoadingPartner().getCurrentCapacity(job.getStoreableParameters().getStorableType())
				.getValue();
		String source = context.getVehicle().getVRPSimulationModelElementParameters().getId();
		String target = job.getLoadingPartner().getVRPSimulationModelElementParameters().getId();

		logger.debug("Unload of {} goods executed. {} new capacity={}, {} new capacity={}", job.getNumber(), source,
				capaSource, target, capaTarget);

		job.getLoadingPartner().releaseFrom(this);

		context.addElementsUpdated(context.getVehicle());
		context.addElementsUpdated(job.getLoadingPartner());

		return new ActivityDoActionResult(0.0);
	}

	@Override
	public IJob getJob() {
		return this.job;
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
	public void allocatedElementStateChanged(IVRPSimulationModelElement element) throws BehaviourException {
		if (isActive) {
			throw new BehaviourException("The state of loading partner "
					+ job.getLoadingPartner().getVRPSimulationModelElementParameters().getId()
					+ " changed during acitivty execution.");
		}
	}
	
	@Override
	public void validate(TourContext context) throws BehaviourException {
		String currentPlace = context.getCurrentPlace().getVRPSimulationModelElementParameters().getId();
		String placeOfLoadingPartner = this.job.getLoadingPartner().getCurrentPlace().getVRPSimulationModelElementParameters().getId();
		if(!currentPlace.equals(placeOfLoadingPartner)) {
			throw new BehaviourException("Current place is unequal with place of loading partner.");
		}
		
	}
}
