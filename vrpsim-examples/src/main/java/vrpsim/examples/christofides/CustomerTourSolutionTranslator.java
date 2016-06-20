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
/**
 * 
 */
package vrpsim.examples.christofides;

import java.util.ArrayList;
import java.util.List;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.behaviour.Behaviour;
import vrpsim.core.model.behaviour.IActivity;
import vrpsim.core.model.behaviour.ITour;
import vrpsim.core.model.behaviour.Tour;
import vrpsim.core.model.behaviour.TourContext;
import vrpsim.core.model.behaviour.activities.StorableExchangeActivity;
import vrpsim.core.model.behaviour.activities.StorableTransportActivity;
import vrpsim.core.model.behaviour.activities.util.StorableExchangeJob;
import vrpsim.core.model.behaviour.activities.util.StorableTransportJob;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElement;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.depot.IDepot;
import vrpsim.core.model.structure.util.storage.StorableParameters;
import vrpsim.core.model.structure.vehicle.IVehicle;
import vrpsim.core.model.util.exceptions.JobException;
import vrpsim.core.model.util.exceptions.NetworkException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.simulator.IClock;
import vrpsim.examples.support.CustomerTour;

/**
 * @date 25.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class CustomerTourSolutionTranslator {

	public Behaviour translate(CustomerTour customerTour, VRPSimulationModel model, IClock clock)
			throws NetworkException, JobException, VRPArithmeticException {
		List<ITour> tours = new ArrayList<ITour>();
		tours.add(this.generateTour(customerTour, model, clock));
		return new Behaviour(tours);
	}

	private ITour generateTour(CustomerTour customerTour, VRPSimulationModel model, IClock clock)
			throws NetworkException, JobException, VRPArithmeticException {

		TourContext context = new TourContext(clock.getCurrentSimulationTime().createTimeFrom(0.0),
				model.getStructure().getVehicles().get(0), model.getStructure().getDrivers().get(0));

		List<IActivity> activities = createActivities(customerTour, model, context, clock,
				model.getStructure().getStorableParameters().get(0));
		Tour tour = new Tour(context, activities);
		return tour;
	}

	private List<IActivity> createActivities(CustomerTour customerTour, VRPSimulationModel model, TourContext context,
			IClock clock, StorableParameters storeableParameters) throws JobException, VRPArithmeticException {

		List<IActivity> activities = new ArrayList<IActivity>();

		// Get max for vehicle at the depot
		IActivity alwaysFirst = this.createStorableExchangeBetweenDepotAndVehicle(
				model.getStructure().getDepots().get(0), context.getCurrentVehicle(), storeableParameters);
		activities.add(alwaysFirst);

		int storablesFix = context.getCurrentVehicle()
				.getFreeCapacity(context.getCurrentVehicle().getAllCanStoreTypes().get(0)).getValue().intValue();
		int storables = storablesFix;

		for (String customerId : customerTour.getCustomerIds()) {

			ICustomer customer = this.getCustomer(customerId, model.getStructure().getCustomers());
			IActivity driverToCustomer = this.createStorableTransportActivity(customer, context.getCurrentVehicle());
			IActivity unloadAtCustomer = this.createStorableExchangeBetweenVehicleAndCustomer(
					context.getCurrentVehicle(), customer, model.getStructure().getDepots().get(0),
					storeableParameters);

			if (storables - ((StorableExchangeJob) unloadAtCustomer.getJob()).getNumber() < 0) {

				IActivity driveToDepot = this.createStorableTransportActivity(model.getStructure().getDepots().get(0),
						context.getCurrentVehicle());
				IActivity loadAtDepot = this.createStorableExchangeBetweenDepotAndVehicle(
						model.getStructure().getDepots().get(0), context.getCurrentVehicle(), storables,
						storeableParameters);

				activities.add(driveToDepot);
				activities.add(loadAtDepot);
				storables = storablesFix;

			}

			storables -= ((StorableExchangeJob) unloadAtCustomer.getJob()).getNumber();
			activities.add(driverToCustomer);
			activities.add(unloadAtCustomer);

		}

		// Always last
		IActivity driveToDepot = this.createStorableTransportActivity(model.getStructure().getDepots().get(0),
				context.getCurrentVehicle());
		activities.add(driveToDepot);

		return activities;
	}

	private StorableExchangeActivity createStorableExchangeBetweenDepotAndVehicle(IDepot depot, IVehicle vehicle,
			StorableParameters storeableParameters) throws JobException, VRPArithmeticException {

		// depot.getStorableGenerator().resetStorableGenerationCounter();
		Integer number = vehicle.getFreeCapacity(storeableParameters.getStorableType().getCanStoreTypes().get(0))
				.getValue().intValue();

		StorableExchangeJob job = new StorableExchangeJob(storeableParameters, number, depot, vehicle);
		StorableExchangeActivity storableExchangeActivity = new StorableExchangeActivity(job);

		return storableExchangeActivity;
	}

	private StorableExchangeActivity createStorableExchangeBetweenDepotAndVehicle(IDepot depot, IVehicle vehicle,
			int stillInVehicle, StorableParameters storeableParameters) throws JobException, VRPArithmeticException {

		// depot.getStorableGenerator().resetStorableGenerationCounter();
		Integer number = vehicle.getFreeCapacity(storeableParameters.getStorableType().getCanStoreTypes().get(0))
				.getValue().intValue() - stillInVehicle;

		StorableExchangeJob job = new StorableExchangeJob(storeableParameters, number, depot, vehicle);
		StorableExchangeActivity storableExchangeActivity = new StorableExchangeActivity(job);

		return storableExchangeActivity;
	}

	private StorableExchangeActivity createStorableExchangeBetweenVehicleAndCustomer(IVehicle vehicle,
			ICustomer customer, IDepot depot, StorableParameters storeableParameters)
			throws JobException, VRPArithmeticException {

		// depot.getStorableGenerator().resetStorableGenerationCounter();
		Integer number = customer.getUncertainParameters().getParameter().get(0).getNumber().getNumber().intValue();

		StorableExchangeJob job = new StorableExchangeJob(storeableParameters, number, vehicle, customer);
		StorableExchangeActivity storableExchangeActivity = new StorableExchangeActivity(job);

		return storableExchangeActivity;
	}

	private StorableTransportActivity createStorableTransportActivity(IVRPSimulationModelStructureElement target,
			IVehicle vehicle) {
		StorableTransportJob job = new StorableTransportJob(
				target.getVRPSimulationModelStructureElementParameters().getHome(), vehicle);
		StorableTransportActivity storableExchangeActivity = new StorableTransportActivity(job);
		return storableExchangeActivity;
	}

	private ICustomer getCustomer(String customerId, List<ICustomer> customers) {
		ICustomer result = null;
		for (ICustomer customer : customers) {
			if (customer.getVRPSimulationModelElementParameters().getId().equals(customerId)) {
				result = customer;
				break;
			}
		}
		return result;
	}

}
