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
package vrpsim.core.model.solution;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.IVRPSimulationElement;
import vrpsim.core.model.behaviour.Behaviour;
import vrpsim.core.model.network.NetworkService;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.simulator.EventListService;

/**
 * A {@link SolutionManager} represents the static solution, {@link Behaviour}
 * in form of {@link IInitialBehaviourProvider} and the dynamic solution in form
 * of {@link IDynamicBehaviourProvider}, implementation is e.g. the
 * {@link OrderManager}.
 * 
 * {@link IDynamicBehaviourProvider} represents the solution interface for
 * solving dynamic VRP.
 * 
 * @author mayert
 */
public class SolutionManager implements IVRPSimulationSolutionElement {

	private static Logger logger = LoggerFactory.getLogger(SolutionManager.class);

	private final IInitialBehaviourProvider staticBehaviourProvider;
	private IDynamicBehaviourProvider dynamicBehaviourProvider;
	private Behaviour staticBehaviour;

	public SolutionManager(IInitialBehaviourProvider staticBehaviourProvider) {
		this.staticBehaviourProvider = staticBehaviourProvider;
	}

	/**
	 * Sets the {@link IDynamicBehaviourProvider}.
	 * 
	 * @param dynamicBehaviourProvider
	 */
	public void setDynamicBehaviourProvider(IDynamicBehaviourProvider dynamicBehaviourProvider) {
		this.dynamicBehaviourProvider = dynamicBehaviourProvider;
	}

	public Behaviour getStaticBehaviourFromStaticBehaviourProvider() {
		return this.staticBehaviour;
	}

	/**
	 * Initializes the {@link IInitialBehaviourProvider} and the
	 * {@link IDynamicBehaviourProvider} and returns the
	 * {@link IVRPSimulationElement}s.
	 * 
	 * @param eventListService
	 * @param structure
	 * @param network
	 * @return
	 */
	public List<IVRPSimulationElement> initalizeAndReturnSolutionBehaviourSimulationElements(
			EventListService eventListService, StructureService structureService, NetworkService networkService) {

		this.staticBehaviour = this.staticBehaviourProvider.provideBehavior(networkService, structureService);
		List<IVRPSimulationElement> elements = new ArrayList<>();
		elements.addAll(this.staticBehaviour.getAllSimulationElements());

		if (this.dynamicBehaviourProvider != null) {
			this.dynamicBehaviourProvider.initialize(eventListService, structureService, networkService,
					staticBehaviour.getBehaviourService());
			elements.add(this.dynamicBehaviourProvider);
			logger.info("Behavior Provider initialized.");
		} else {
			logger.info("Behavior Provider initialized, no IDynamicBehaviourProvider exists.");
		}
		return elements;
	}

	/**
	 * Returns the {@link IDynamicBehaviourProvider}, e.g. an
	 * {@link OrderManager}. Note: can return null.
	 * 
	 * @return null possible.
	 */
	public IDynamicBehaviourProvider getDynamicBehaviourProvider() {
		return dynamicBehaviourProvider;
	}

}
