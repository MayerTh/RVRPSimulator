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
package vrpsim.examples.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.behaviour.Behaviour;
import vrpsim.core.model.behaviour.activities.IActivity;
import vrpsim.core.model.behaviour.activities.LoadActivity;
import vrpsim.core.model.behaviour.activities.TransportActivity;
import vrpsim.core.model.behaviour.activities.UnloadActivity;
import vrpsim.core.model.behaviour.activities.util.LoadUnloadJob;
import vrpsim.core.model.behaviour.activities.util.TransportJob;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.behaviour.tour.Tour;
import vrpsim.core.model.behaviour.tour.TourContext;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElement;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorageMovable;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.depot.IDepot;
import vrpsim.core.model.structure.util.storage.StorableParameters;
import vrpsim.core.model.util.exceptions.BehaviourException;
import vrpsim.core.model.util.exceptions.NetworkException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.core.simulator.IClock;

public class SimpleBehaviorGenerator {

	public Behaviour createRandomBehaviour(VRPSimulationModel model, IClock clock)
			throws NetworkException, BehaviourException, VRPArithmeticException {
		
		List<String> customerIds = new ArrayList<>();
		for(ICustomer customer : model.getStructure().getCustomers()) {
			customerIds.add(customer.getVRPSimulationModelElementParameters().getId());
		}
		
		Collections.shuffle(customerIds);
		List<ITour> tours = new ArrayList<ITour>();
		tours.add(this.generateTour(customerIds, model, clock));
		return new Behaviour(tours);
	}
	
	public Behaviour createBehaviour(VRPSimulationModel model, List<String> customerIds, IClock clock)
			throws NetworkException, BehaviourException, VRPArithmeticException {
		List<ITour> tours = new ArrayList<ITour>();
		tours.add(this.generateTour(customerIds, model, clock));
		return new Behaviour(tours);
	}
	
	private ITour generateTour(List<String> customerIds, VRPSimulationModel model, IClock clock)
			throws NetworkException, BehaviourException, VRPArithmeticException {

		TourContext context = new TourContext(clock.getCurrentSimulationTime().createTimeFrom(0.0),
				model.getStructure().getVehicles().get(0), model.getStructure().getDrivers().get(0));

		IActivity startActivity = createActivities(customerIds, model, context, clock, model.getStructure().getStorableParameters().get(0));
		Tour tour = new Tour(context, startActivity);
		return tour;
	}

	private IActivity createActivities(List<String> costomerIds, VRPSimulationModel model,
			TourContext context, IClock clock, StorableParameters storeableParameters) throws BehaviourException, VRPArithmeticException {

		List<IActivity> activities = new ArrayList<IActivity>();

		// Get max for vehicle at the depot
		IActivity alwaysFirst = this.createStorableExchangeBetweenDepotAndVehicle(
				model.getStructure().getDepots().get(0), context.getVehicle(), storeableParameters);
		activities.add(alwaysFirst);

		int storablesFix = context.getVehicle()
				.getFreeCapacity(context.getVehicle().getAllCanStoreTypes().get(0)).getValue().intValue();
		int storables = storablesFix;

		for (String customerId : costomerIds) {

			ICustomer customer = this.getCustomer(customerId, model.getStructure().getCustomers());
			IActivity driverToCustomer = this.createStorableTransportActivity(customer, context.getVehicle());
			IActivity unloadAtCustomer = this.createStorableExchangeBetweenVehicleAndCustomer(
					context.getVehicle(), customer, model.getStructure().getDepots().get(0), storeableParameters);

			if (storables - ((LoadUnloadJob) unloadAtCustomer.getJob()).getNumber() < 0) {

				IActivity driveToDepot = this.createStorableTransportActivity(model.getStructure().getDepots().get(0),
						context.getVehicle());
				IActivity loadAtDepot = this.createStorableExchangeBetweenDepotAndVehicle(
						model.getStructure().getDepots().get(0), context.getVehicle(), storables, storeableParameters);

				activities.add(driveToDepot);
				activities.add(loadAtDepot);
				storables = storablesFix;

			}

			storables -= ((LoadUnloadJob) unloadAtCustomer.getJob()).getNumber();
			activities.add(driverToCustomer);
			activities.add(unloadAtCustomer);

		}

		// Always last
		IActivity driveToDepot = this.createStorableTransportActivity(model.getStructure().getDepots().get(0),
				context.getVehicle());
		activities.add(driveToDepot);

		for(int i = 0; i < activities.size()-1; i++) {
			activities.get(i).setSuccessor(activities.get(i+1));
		}

		return activities.get(0);
	}

	private LoadActivity createStorableExchangeBetweenDepotAndVehicle(IDepot depot, IVRPSimulationModelStructureElementWithStorageMovable vehicle, StorableParameters storeableParameters)
			throws BehaviourException, VRPArithmeticException {

//		depot.getStorableGenerator().resetStorableGenerationCounter();
		Integer number = vehicle.getFreeCapacity(storeableParameters.getStorableType().getCanStoreTypes().get(0)).getValue().intValue();

		LoadUnloadJob job = new LoadUnloadJob(storeableParameters, number, depot);
		LoadActivity storableExchangeActivity = new LoadActivity(job);

		return storableExchangeActivity;
	}

	private LoadActivity createStorableExchangeBetweenDepotAndVehicle(IDepot depot, IVRPSimulationModelStructureElementWithStorageMovable vehicle,
			int stillInVehicle, StorableParameters storeableParameters) throws BehaviourException, VRPArithmeticException {

//		depot.getStorableGenerator().resetStorableGenerationCounter();
		Integer number = vehicle.getFreeCapacity(storeableParameters.getStorableType().getCanStoreTypes().get(0)).getValue().intValue()
				- stillInVehicle;

		LoadUnloadJob job = new LoadUnloadJob(storeableParameters, number, depot);
		LoadActivity storableExchangeActivity = new LoadActivity(job);

		return storableExchangeActivity;
	}

	private UnloadActivity createStorableExchangeBetweenVehicleAndCustomer(IVRPSimulationModelStructureElementWithStorageMovable vehicle,
			ICustomer customer, IDepot depot, StorableParameters storeableParameters) throws BehaviourException, VRPArithmeticException {

//		depot.getStorableGenerator().resetStorableGenerationCounter();
		Integer number = customer.getUncertainParameters().getParameter().get(0).getNumber().getNumber().intValue();

		LoadUnloadJob job = new LoadUnloadJob(storeableParameters, number, customer);
		UnloadActivity storableExchangeActivity = new UnloadActivity(job);

		return storableExchangeActivity;
	}

	private TransportActivity createStorableTransportActivity(IVRPSimulationModelStructureElement target,
			IVRPSimulationModelStructureElementWithStorageMovable vehicle) {
		TransportJob job = new TransportJob(
				target.getVRPSimulationModelStructureElementParameters().getHome());
		TransportActivity storableExchangeActivity = new TransportActivity(job);
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
