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
package vrpsim.core.model.behaviour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.behaviour.activities.IActivity;
import vrpsim.core.model.behaviour.activities.impl.LoadActivity;
import vrpsim.core.model.behaviour.activities.impl.TransportActivity;
import vrpsim.core.model.behaviour.activities.impl.UnloadActivity;
import vrpsim.core.model.behaviour.tour.Cost;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.behaviour.tour.Tour;
import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.impl.Location;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElement;
import vrpsim.core.model.structure.IVRPSimulationModelStructureElementWithStorage;
import vrpsim.core.model.structure.storage.impl.StorableParameters;
import vrpsim.core.model.util.policies.IRoutingPolicy;
import vrpsim.core.model.util.policies.impl.EuclideanNoWayDistanceIsTimeRouting;
import vrpsim.core.simulator.EventListService;
import vrpsim.core.simulator.IClock;

/**
 * Service for the {@link Behaviour}. Provides all helper functions.
 * 
 * @author mayert
 *
 */
public class BehaviourService {

	private static Logger logger = LoggerFactory.getLogger(BehaviourService.class);

	private final Behaviour behaviour;

	public BehaviourService(Behaviour behaviour) {
		this.behaviour = behaviour;
	}

	public Behaviour getBehaviour() {
		return behaviour;
	}

	public Cost getTourCosts() {
		Cost cost = new Cost();
		for (ITour tour : behaviour.getTours()) {
			cost = Cost.addCosts(tour.getTourContext().getCurrentTourCost(), cost);
		}
		return cost;
	}

	public List<ITour> getActiveTours() {
		List<ITour> activeTours = new ArrayList<>();
		for (ITour tour : this.behaviour.getTours()) {
			if (tour.getTourContext().isTourActive()) {
				activeTours.add(tour);
			}
		}
		return activeTours;
	}

	public void registerNewTour(ITour tour, EventListService eventListService, IClock clock) {
		logger.info("Register new tour.");
		eventListService.introduceTourToEventList(clock, tour);
		this.behaviour.getTours().add(tour);
	}

	/**
	 * Returns all unloading partners beginning with the successor of the given
	 * {@link IActivity};
	 * 
	 * @param activity
	 * @return
	 */
	public List<LoadOrUnloadAmountSpDummy> getCustomersOnTour(IActivity activity) {
		List<LoadOrUnloadAmountSpDummy> result = new ArrayList<>();
		IActivity workWith = activity.getSuccessor();
		while (workWith != null) {
			if (workWith instanceof UnloadActivity) {
				UnloadActivity ua = (UnloadActivity) workWith;
				result.add(new LoadOrUnloadAmountSpDummy(ua.getLoadingPartner(), (-1) * ua.getNumber(), ua.getStorableParameters()));
			}
			if (workWith instanceof LoadActivity) {
				LoadActivity la = (LoadActivity) workWith;
				result.add(new LoadOrUnloadAmountSpDummy(la.getLoadingPartner(), la.getNumber(), la.getStorableParameters()));
			}
			workWith = workWith.getSuccessor();
		}
		return result;
	}

	public void reverse(Behaviour behaviour) {
		for (ITour tour : behaviour.getTours()) {
			this.reverse(tour, new EuclideanNoWayDistanceIsTimeRouting());
		}
	}

	public void reverse(ITour tour, IRoutingPolicy routingPolicy) {

//		logger.debug("Start reverse.");
		
		// DEBUG
//		IActivity test2 = tour.getStartActivity();
//		while (test2 != null) {
//			logger.trace("Before reverse: {}", test2.toString());
//			test2 = test2.getSuccessor();
//
//		}

		IActivity startActivity = tour.getStartActivity();
		if (startActivity instanceof LoadActivity) {
			logger.trace("Start reverse.");

			List<LoadOrUnloadAmountSpDummy> lua = this.getCustomersOnTour(startActivity);
			// logger.trace("Before revers: {}" + lua.toString());
			Collections.reverse(lua);
			// logger.trace("After revers: {}" + lua.toString());

			IActivity workWith = startActivity;
			for (LoadOrUnloadAmountSpDummy lud : lua) {
				logger.trace("Work with: " + workWith);

				Location activityLocation = ((INode) lud.getElement().getVRPSimulationModelStructureElementParameters().getHome())
						.getLocation();
				Location workWithLoaction = ((INode) workWith.getLocation()).getLocation();
				if (!activityLocation.equals(workWithLoaction)) {
					// Transport
					TransportActivity ta = this.get(lud.getElement(), routingPolicy);
					workWith.setSuccessor(ta);
					workWith = ta;
//					logger.trace("Work with: " + workWith);
				}

				// Load or unload
				IActivity a = this.get(lud.getElement(), lud.getSp(), lud.getAmount());
				workWith.setSuccessor(a);
				workWith = a;
			}

			Location startActivityLocation = ((INode) startActivity.getLocation()).getLocation();
			Location workWithLoaction = ((INode) workWith.getLocation()).getLocation();
			if (!startActivityLocation.equals(workWithLoaction)) {
				TransportActivity ta = this.get(((LoadActivity) startActivity).getLoadingPartner(), routingPolicy);
				workWith.setSuccessor(ta);
//				logger.trace("Added successor to work with: " + ta);
			}

			tour = new Tour(tour.getTourContext(), startActivity);

		}

		// DEBUG
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// IActivity test = tour.getStartActivity();
		// while (test != null) {
		// logger.trace("After reverse: {}", test.toString());
		// test = test.getSuccessor();
		//
		// }

	}

	// private void print(ITour tour) {
	// String str = "";
	// IActivity currentActivity = tour.getStartActivity();
	// while (currentActivity != null) {
	// if (currentActivity instanceof LoadActivity || currentActivity instanceof
	// UnloadActivity) {
	// IVRPSimulationModelStructureElement se = null;
	// if (currentActivity instanceof LoadActivity) {
	// se = ((LoadActivity) currentActivity).getLoadingPartner();
	// } else {
	// se = ((UnloadActivity) currentActivity).getLoadingPartner();
	// }
	// str += se.getVRPSimulationModelElementParameters().getId() + ",";
	// }
	// currentActivity = currentActivity.getSuccessor();
	// }
	// System.out.println(str);
	// }

	public TransportActivity get(IVRPSimulationModelStructureElement se, IRoutingPolicy routingPolicy) {
		INode transportTo = (INode) se.getVRPSimulationModelStructureElementParameters().getHome();
		return new TransportActivity(transportTo, routingPolicy);
	}

	public IActivity get(IVRPSimulationModelStructureElementWithStorage element, StorableParameters sp, int amount) {
		IActivity result = null;
		if (amount > 0) {
			result = new LoadActivity(sp, amount, element, true);
		} else {
			result = new UnloadActivity(sp, (-1) * amount, element, true);
		}
		return result;
	}

	public class LoadOrUnloadAmountSpDummy {
		private final IVRPSimulationModelStructureElementWithStorage element;
		private final int amount;
		private final StorableParameters sp;

		public LoadOrUnloadAmountSpDummy(IVRPSimulationModelStructureElementWithStorage element, int amount, StorableParameters sp) {
			super();
			this.element = element;
			this.amount = amount;
			this.sp = sp;
		}

		public IVRPSimulationModelStructureElementWithStorage getElement() {
			return element;
		}

		/**
		 * Negative if unloading, positive if loading.
		 * 
		 * @return
		 */
		public int getAmount() {
			return amount;
		}

		public StorableParameters getSp() {
			return sp;
		}

		@Override
		public String toString() {
			return "Load or unload at " + element.getVRPSimulationModelElementParameters().getId();
		}

	}

}
