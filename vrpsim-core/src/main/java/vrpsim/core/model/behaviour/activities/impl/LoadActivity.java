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
import vrpsim.core.model.behaviour.tour.TourContext;
import vrpsim.core.model.network.IVRPSimulationModelNetworkElement;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementMovable;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.storage.IStorable;
import vrpsim.core.model.structure.storage.impl.StorableParameters;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.exceptions.BehaviourException;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.policies.IServiceTimePolicy;
import vrpsim.core.model.util.policies.impl.NoServiceTimePolicy;
import vrpsim.core.simulator.IClock;

/**
 * Describes the loading of an amount of {@link IStorable} from an
 * {@link IVRPSimulationModelStructureElementWithStorage} to the
 * {@link IVehicle} in {@link TourContext#getVehicle()}.
 * 
 * @date 19.02.2016
 * @author thomas.mayer@unibw.de
 */
public class LoadActivity implements IActivity {

	private static Logger logger = LoggerFactory.getLogger(LoadActivity.class);

	private final StorableParameters storableParameters;
	private final int number;
	private final IVRPSimulationModelStructureElementWithStorage loadingPartner;
	private final IServiceTimePolicy policy;
	private boolean isPrepared = false;

	@Override
	public void reset() {
		this.isPrepared = false;
	}

	public IVRPSimulationModelStructureElementWithStorage getLoadingPartner() {
		return loadingPartner;
	}

	public StorableParameters getStorableParameters() {
		return storableParameters;
	}

	public int getNumber() {
		return number;
	}

	private final boolean isCustomerInteraction;
	private double serviceTime = 0D;

	private IActivity successor;

	public LoadActivity(final StorableParameters storeableParameters, final int number,
			IVRPSimulationModelStructureElementWithStorage loadingPartner, boolean isCustomerInteraction) {
		this(storeableParameters, number, loadingPartner, isCustomerInteraction, new NoServiceTimePolicy());
	}

	public LoadActivity(final StorableParameters storeableParameters, final int number,
			IVRPSimulationModelStructureElementWithStorage loadingPartner, boolean isCustomerInteraction, IServiceTimePolicy policy) {
		this.storableParameters = storeableParameters;
		this.number = number;
		this.loadingPartner = loadingPartner;
		this.isCustomerInteraction = isCustomerInteraction;
		this.policy = policy;
	}

	@Override
	public ActivityPrepareActionResult prepareAction(IClock clock, TourContext context) throws BehaviourException {

		logger.debug("Prepare to load {} goods of {} from {} to {}.", number, storableParameters,
				context.getVehicle().getVRPSimulationModelElementParameters().getId(),
				this.loadingPartner.getVRPSimulationModelElementParameters().getId());

		this.validate(context);

		boolean canUnload = this.loadingPartner.getCanStoreManager().canUnload(this.storableParameters, this.number);
		boolean canLoad = context.getVehicle().getCanStoreManager().canLoad(this.storableParameters, this.number);
		boolean isPartnerAvailable = this.loadingPartner.isAvailableForInteractionInAllocation(clock, this);
		boolean isVehicleAvailable = context.getVehicle().isAvailableForInteractionInAllocation(clock, this);

		ActivityPrepareActionResult activityDoActionResult = null;
		if (!canUnload) {
			activityDoActionResult = new ActivityPrepareActionResult(false, "Can not unload from loading partner.");
			activityDoActionResult.setResponsibleElement(context.getVehicle());
		}

		if (activityDoActionResult == null && !canLoad) {
			activityDoActionResult = new ActivityPrepareActionResult(false, "Can not load at vehicle.");
			activityDoActionResult.setResponsibleElement(this.loadingPartner);
		}

		if (activityDoActionResult == null && !isPartnerAvailable) {
			activityDoActionResult = new ActivityPrepareActionResult(false, "Loading partner not available for interaction.");
			activityDoActionResult.setResponsibleElement(this.loadingPartner);
		}

		if (activityDoActionResult == null && !isVehicleAvailable) {
			activityDoActionResult = new ActivityPrepareActionResult(false, "Vehicle not available for interaction.");
			activityDoActionResult.setResponsibleElement(context.getVehicle());
		}

		if (activityDoActionResult == null) {
			activityDoActionResult = new ActivityPrepareActionResult(true, "Loading possible");
			this.serviceTime = this.policy.getServiceTime(context.getDriver(), context.getVehicle(), this.loadingPartner, this.number,
					clock);
			activityDoActionResult.setReletaiveTimeTillDoAction(this.serviceTime);
		}

		return activityDoActionResult;
	}

	@Override
	public void doAction(IClock clock, TourContext context) throws StorageException {
		Cost cost = new Cost(0L, 0L, this.serviceTime, 0L);
		List<IStorable> storables = this.loadingPartner.getCanStoreManager().unload(this.storableParameters, number);
		context.getVehicle().getCanStoreManager().load(storables);
		context.addElementsUpdated(this.loadingPartner);
		context.addElementsUpdated(context.getVehicle());
		context.updateTourContextElementsAndCosts(null, cost);

		String vehicle = context.getVehicle().getVRPSimulationModelElementParameters().getId();
		String loadingPartner = this.loadingPartner.getVRPSimulationModelElementParameters().getId();

		this.serviceTime = 0d;
		logger.info("Loaded {} of typ {} from {} to {}.", this.number, this.storableParameters, loadingPartner, vehicle);
	}

	@Override
	public String toString() {
		return "Load " + this.number + " - " + this.loadingPartner.getVRPSimulationModelElementParameters().getId();
	}

	@Override
	public IActivity getSuccessor() {
		return this.successor;
	}

	public void setSuccessor(IActivity successor) {
		this.successor = successor;
	}

	@Override
	public void allocatedElementStateChanged(IVRPSimulationModelElement element) throws BehaviourException {
	}

	@Override
	public void validate(TourContext context) throws BehaviourException {
		String currentPlace = context.getVehicle().getCurrentPlace().getVRPSimulationModelElementParameters().getId();
		String placeOfLoadingPartner = null;
		if (this.loadingPartner instanceof IVRPSimulationModelStructureElementMovable) {
			placeOfLoadingPartner = ((IVRPSimulationModelStructureElementMovable) this.loadingPartner).getCurrentPlace()
					.getVRPSimulationModelElementParameters().getId();
		} else {
			placeOfLoadingPartner = this.loadingPartner.getVRPSimulationModelStructureElementParameters().getHome()
					.getVRPSimulationModelElementParameters().getId();
		}

		if (!currentPlace.equals(placeOfLoadingPartner)) {
			throw new BehaviourException("Current place is unequal with place of loading partner.");
		}
	}

	@Override
	public List<IVRPSimulationModelElement> getToAllocate() {
		List<IVRPSimulationModelElement> result = new ArrayList<>();
		result.add(this.loadingPartner);
		return result;
	}

	@Override
	public boolean isCustomerInteraction() {
		return this.isCustomerInteraction;
	}

	@Override
	public IVRPSimulationModelNetworkElement getLocation() {
		return this.loadingPartner.getVRPSimulationModelStructureElementParameters().getHome();
	}

	@Override
	public boolean isPrepared() {
		return this.isPrepared;
	}

	public void setPrepared(boolean isPrepared) {
		this.isPrepared = isPrepared;
	}

}
