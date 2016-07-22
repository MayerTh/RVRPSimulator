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
package vrpsim.core.model.util.uncertainty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import vrpsim.core.model.structure.util.storage.StorableParameters;

/**
 * @date 02.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class UncertainParamters {

	private final HashSet<UncertainParameterContainer> consumparameter;

	public UncertainParamters(HashSet<UncertainParameterContainer> consumparameter) {
		this.consumparameter = consumparameter;
	}

	public UncertainParamters(UncertainParameterContainer consumparameterContainer) {
		this.consumparameter = new HashSet<UncertainParamters.UncertainParameterContainer>();
		this.consumparameter.add(consumparameterContainer);
	}

	public UncertainParamters() {
		this.consumparameter = new HashSet<UncertainParamters.UncertainParameterContainer>();
	}

	public void addContainer(UncertainParameterContainer consumparameterContainer) {
		this.consumparameter.add(consumparameterContainer);
	}

	public List<UncertainParameterContainer> getParameter() {
		return new ArrayList<UncertainParameterContainer>(this.consumparameter);
	}

	public static class UncertainParameterContainer {

		private final StorableParameters storableParameters;
		private final IDistributionFunction start;
		private final IDistributionFunction number;
		private final IDistributionFunction cycle;
		private boolean isCyclic;

		private final IDistributionFunction earliestDueDate;
		private final IDistributionFunction latestDueDate;
		private final boolean adaptDueDatesToSimulationTime;

		private Double startInstance;
		private Double numberInstance;
		private Double cycleInstance;
		private Double earliestDueDateInstance;
		private Double latestDueDateInstance;

		/**
		 * @param storableParameters
		 *            - defining the storable which are consumed/ordered/...
		 * @param number
		 *            - amount of storables which are consumed/ordered/...
		 * @param start
		 *            - the start when the defined amount the first time are
		 *            consumed/ordered/...
		 */
		public UncertainParameterContainer(final StorableParameters storableParameters,
				final IDistributionFunction number, final IDistributionFunction start) {
			this(storableParameters, number, start, null, null, null, false);
		}

		/**
		 * @param storableParameters
		 *            - defining the storable which are consumed/ordered/...
		 * @param number
		 *            - amount of storables which are consumed/ordered/...
		 * @param start
		 *            - the start when the defined amount the first time are
		 *            consumed/ordered/...
		 * @param cycle
		 *            - cycle where defined amount of defined storables are
		 *            consumed/ordered/...
		 */
		public UncertainParameterContainer(final StorableParameters storableParameters,
				final IDistributionFunction number, final IDistributionFunction start,
				final IDistributionFunction cycle) {
			this(storableParameters, number, start, cycle, null, null, false);
		}

		/**
		 * 
		 * @param storableParameters
		 *            - defining the storable which are consumed/ordered/...
		 * @param number
		 *            - amount of storables which are consumed/ordered/...
		 * @param earliestDueDate
		 *            - earliest delivery of the order, after order is created,
		 *            see {@link UncertainParameterContainer#getCycle()}
		 * @param latestDueDate
		 *            - latest delivery of the order, after order is created,
		 *            see {@link UncertainParameterContainer#getCycle()}
		 * @param adaptDueDatesToSimulationTime
		 *            - define if the due dates have to be adapted to simulation
		 *            time.
		 * @param isCyclic
		 *            - define if {@link UncertainParameterContainer} is cyclic.
		 */
		public UncertainParameterContainer(final StorableParameters storableParameters,
				final IDistributionFunction number, final IDistributionFunction earliestDueDate,
				final IDistributionFunction latestDueDate, final boolean adaptDueDatesToSimulationTime,
				final boolean isCyclic) {
			this(storableParameters, number, null, null, earliestDueDate, latestDueDate, adaptDueDatesToSimulationTime);
			this.isCyclic = isCyclic;
		}

		/**
		 * 
		 * @param storableParameters
		 *            - defining the storable which are consumed/ordered/...
		 * @param number
		 *            - amount of storables which are consumed/ordered/...
		 * @param start
		 *            - the start when the defined amount the first time are
		 *            consumed/ordered/...
		 * @param cycle
		 *            - cycle where defined amount of defined storables are
		 *            consumed/ordered/...
		 * @param earliestDueDate
		 *            - earliest delivery of the order, after order is created,
		 *            see {@link UncertainParameterContainer#getCycle()}
		 * @param latestDueDate
		 *            - latest delivery of the order, after order is created,
		 *            see {@link UncertainParameterContainer#getCycle()}
		 * @param adaptDueDatesToSimulationTime
		 *            - define if the due dates have to be adapted to simulation
		 *            time.
		 */
		public UncertainParameterContainer(final StorableParameters storableParameters,
				final IDistributionFunction number, final IDistributionFunction start,
				final IDistributionFunction cycle, final IDistributionFunction earliestDueDate,
				final IDistributionFunction latestDueDate, final boolean adaptDueDatesToSimulationTime) {
			this.start = start;
			this.number = number;
			this.cycle = cycle;
			this.isCyclic = (this.cycle != null);
			this.storableParameters = storableParameters;
			this.earliestDueDate = earliestDueDate;
			this.latestDueDate = latestDueDate;
			this.adaptDueDatesToSimulationTime = adaptDueDatesToSimulationTime;

			resetInstances();
		}

		/**
		 * Returns earliest delivery of the order, after order is created, see
		 * {@link UncertainParameterContainer#getCycle()}. Theoretical
		 * background are time windows in the context of VRP.
		 * 
		 * @return
		 */
		public Double getEarliestDueDate() {
			return earliestDueDateInstance;
		}

		/**
		 * Returns latest delivery of the order, after order is created, see
		 * {@link UncertainParameterContainer#getCycle()}. Theoretical
		 * background are time windows in the context of VRP.
		 * 
		 * @return
		 */
		public Double getLatestDueDate() {
			return latestDueDateInstance;
		}

		/**
		 * Returns {@link StorableParameters}, which defines the storables
		 * consumed/ordered/..
		 * 
		 * @return
		 */
		public StorableParameters getStorableParameters() {
			return storableParameters;
		}

		/**
		 * Returns the number of storables which are consumed/ordered/...
		 * 
		 * @return
		 */
		public Double getNumber() {
			return numberInstance;
		}

		/**
		 * Returns the cycle when defined amount of defined storables are
		 * consumed/ordered/...
		 * 
		 * Can return null.
		 * 
		 * @return
		 */
		public Double getCycle() {
			return cycleInstance;
		}

		/**
		 * Returns the start when the defined amount the first time are
		 * consumed/ordered/...
		 * 
		 * @return
		 */
		public Double getStart() {
			return startInstance;
		}

		/**
		 * Returns true if the container is cyclic, means if a value for
		 * {@link UncertainParameterContainer#getCycle()} is defined.
		 * 
		 * @return
		 */
		public boolean isCyclic() {
			return isCyclic;
		}

		/**
		 * Returns true, if you have to adapt the due dates to the current
		 * simulation time (the current simulation time is the time where you
		 * would like to create something dependent on this container).
		 * 
		 * @return
		 */
		public boolean isAdaptDueDatesToSimulationTime() {
			return adaptDueDatesToSimulationTime;
		}

		/**
		 * Resets the instances of all values of the
		 * {@link UncertainParameterContainer} created from the
		 * {@link IDistributionFunction}.
		 */
		public void resetInstances() {
			this.startInstance = (start != null) ? start.getNumber() : null;
			this.numberInstance = (number != null) ? number.getNumber() : null;
			this.cycleInstance = (cycle != null) ? cycle.getNumber() : null;
			this.earliestDueDateInstance = (earliestDueDate != null) ? earliestDueDate.getNumber() : null;
			this.latestDueDateInstance = (latestDueDate != null) ? latestDueDate.getNumber() : null;
		}

	}

}
