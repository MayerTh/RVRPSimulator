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
package vrpsim.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import vrpsim.core.model.behaviour.Behaviour;
import vrpsim.core.model.behaviour.BehaviourService;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.core.model.network.impl.Network;
import vrpsim.core.model.network.impl.NetworkService;
import vrpsim.core.model.solution.IAmInterestedInPubishedOrders;
import vrpsim.core.model.solution.impl.BehaviourProvider;
import vrpsim.core.model.structure.Structure;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.model.structure.customer.ICustomer;
import vrpsim.core.model.structure.depot.IDepot;
import vrpsim.core.model.structure.occasionaldriver.impl.OccasionalDriver;
import vrpsim.core.simulator.EventListService;

/**
 * @date 03.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class VRPSimulationModel {

	private final VRPSimulationModelParameters vrpSimulationModelParameters;

	private final Structure structure;
	private final Network network;

	private BehaviourProvider behaviourProvider;

	public VRPSimulationModel(final VRPSimulationModelParameters vrpSimulationModelParameters, final Structure structure,
			final Network network) {

		this.vrpSimulationModelParameters = vrpSimulationModelParameters;

		this.structure = structure;
		this.network = network;
	}

	public StructureService getStructureService() {
		return this.structure.getStructureService();
	}

	public NetworkService getNetworkService() {
		return this.network.getNetworkService();
	}

	public BehaviourService getBehaviourService() {
		return this.behaviourProvider.getBehaviourFromInitialBehaviourProvider().getBehaviourService();
	}

	/**
	 * Sets the {@link BehaviourProvider} to the model in general a
	 * {@link VRPSimulationModel} can describe no solution, e.g. only the arrival at
	 * the {@link IDepot}, {@link ICustomer} consumption can be simulated.
	 * 
	 * @param behaviourProvider
	 */
	public void setBehaviourProvider(BehaviourProvider behaviourProvider) {
		this.behaviourProvider = behaviourProvider;
	}

	/**
	 * Publishes the {@link EventListService} to the {@link BehaviourProvider},
	 * which the order manager needs e.g. to remove {@link ITour}s from the list or
	 * deploy new {@link ITour}s. Also publishes the {@link EventListService} to all
	 * {@link OccasionalDriver}s. If no {@link BehaviourProvider} exists, only the
	 * elements from {@link Structure} and {@link Network} are returned.
	 * 
	 * Returns all existing {@link IVRPSimulationElement}.
	 * 
	 * @param eventListInterface
	 * @return
	 */
	public List<IVRPSimulationElement> initalizeAndReturnSimulationElements(EventListService eventListInterface) {

		List<IVRPSimulationElement> allElements = new ArrayList<>();
		allElements.addAll(this.structure.getAllSimulationElements());
		allElements.addAll(this.network.getAllSimulationElements());

		if (this.behaviourProvider != null) {
			allElements.addAll(this.behaviourProvider.initalizeAndReturnSolutionBehaviourSimulationElements(eventListInterface,
					structure.getStructureService(), network.getNetworkService()));

			if (this.behaviourProvider.getDynamicBehaviourProvider() != null) {
				for (IVRPSimulationElement element : allElements) {
					if (element instanceof IAmInterestedInPubishedOrders) {
						((IAmInterestedInPubishedOrders) element)
								.registerToObserve(this.behaviourProvider.getDynamicBehaviourProvider().getOrderBord());
						((IAmInterestedInPubishedOrders) element).registerServices(eventListInterface, structure.getStructureService(),
								network.getNetworkService());
					}
				}
			}
		}

		return allElements;
	}

	public VRPSimulationModelParameters getVrpSimulationModelParameters() {
		return vrpSimulationModelParameters;
	}

	public Network getNetwork() {
		return network;
	}

	public Structure getStructure() {
		return structure;
	}

	public BehaviourProvider getBehaviourProvider() {
		return this.behaviourProvider;
	}

	public void reset() {
		Behaviour b = this.behaviourProvider.getBehaviourBeforeInitialization(this.getStructureService(), this.getNetworkService());
		b.reset();
		this.getStructure().reset();
		this.getNetwork().reset();
	}

	public void reverseBehaviour() {
		Behaviour behaviour = behaviourProvider.getBehaviourBeforeInitialization(this.getStructureService(), this.getNetworkService());
		this.getBehaviourService().reverse(behaviour);
	}

	public void shuffelDynamicCustomerArrivals(Random rnd) {
		List<Double> orderedStartTimes = new ArrayList<>();
		for (ICustomer customer : this.getStructure().getCustomers()) {
			if (customer.isHasDynamicEvents()) {
				orderedStartTimes.add(customer.getOrderStrategy().getStartTime());
			}
		}
		Collections.shuffle(orderedStartTimes, rnd);
		int i = 0;
		for (ICustomer customer : this.getStructure().getCustomers()) {
			if (customer.isHasDynamicEvents()) {
				customer.getOrderStrategy().setStartTime(orderedStartTimes.get(i++));
			}
		}
	}

}
