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
package vrpsim.core.model.solution.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.IVRPSimulationElement;
import vrpsim.core.model.behaviour.Behaviour;
import vrpsim.core.model.network.impl.NetworkService;
import vrpsim.core.model.solution.IDynamicBehaviourProvider;
import vrpsim.core.model.solution.IInitialBehaviourProvider;
import vrpsim.core.model.solution.IVRPSimulationSolutionElement;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.simulator.EventListService;

/**
 * A {@link BehaviourProvider} represents the static solution, {@link Behaviour}
 * in form of {@link IInitialBehaviourProvider} and the dynamic solution in form
 * of {@link IDynamicBehaviourProvider}, implementation is e.g. the
 * {@link ExampleDynamicBehaviourProvider}.
 * 
 * {@link IDynamicBehaviourProvider} represents the solution interface for
 * solving dynamic VRP.
 * 
 * @author mayert
 */
public class BehaviourProvider implements IVRPSimulationSolutionElement {

	private static Logger logger = LoggerFactory.getLogger(BehaviourProvider.class);

	private final IInitialBehaviourProvider initialBehaviourProvider;
	private IDynamicBehaviourProvider dynamicBehaviourProvider;
	private Behaviour behaviour;

	public void setBehaviour(Behaviour behaviour) {
		this.behaviour = behaviour;
	}

	public BehaviourProvider(IInitialBehaviourProvider initialBehaviourProvider) {
		this.initialBehaviourProvider = initialBehaviourProvider;
	}

	/**
	 * Sets the {@link IDynamicBehaviourProvider}.
	 * 
	 * @param dynamicBehaviourProvider
	 */
	public void setDynamicBehaviourProvider(IDynamicBehaviourProvider dynamicBehaviourProvider) {
		this.dynamicBehaviourProvider = dynamicBehaviourProvider;
	}

	public Behaviour getBehaviourFromInitialBehaviourProvider() {
		return this.behaviour;
	}

	public Behaviour getBehaviourBeforeInitialization(StructureService structureService, NetworkService networkService) {
		if (this.behaviour == null) {
			logger.debug("Behaviour Created.");
			this.behaviour = this.initialBehaviourProvider.provideBehavior(networkService, structureService);
		} else {
			logger.debug("No behaviour had to be created.");
		}
		return this.behaviour;
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
	public List<IVRPSimulationElement> initalizeAndReturnSolutionBehaviourSimulationElements(EventListService eventListService,
			StructureService structureService, NetworkService networkService) {

		if (this.behaviour == null) {
			logger.debug("Behaviour Created.");
			this.behaviour = this.initialBehaviourProvider.provideBehavior(networkService, structureService);
		} else {
			logger.debug("No behaviour had to be created.");
		}

		this.behaviour.init();
		List<IVRPSimulationElement> elements = new ArrayList<>();
		elements.addAll(this.behaviour.getAllSimulationElements());

		if (this.dynamicBehaviourProvider != null) {
			this.dynamicBehaviourProvider.initialize(eventListService, structureService, networkService, behaviour.getBehaviourService());
			elements.add(this.dynamicBehaviourProvider);
			logger.debug("Behavior Provider initialized.");
		} else {
			logger.debug("Behavior Provider initialized, no IDynamicBehaviourProvider exists.");
		}
		return elements;
	}

	/**
	 * Returns the {@link IDynamicBehaviourProvider}, e.g. an
	 * {@link ExampleDynamicBehaviourProvider}. Note: can return null.
	 * 
	 * @return null possible.
	 */
	public IDynamicBehaviourProvider getDynamicBehaviourProvider() {
		return dynamicBehaviourProvider;
	}

}
