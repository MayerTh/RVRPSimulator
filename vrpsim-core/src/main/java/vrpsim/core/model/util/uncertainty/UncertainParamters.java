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

	public List<UncertainParameterContainer> getParameter() {
		return new ArrayList<UncertainParameterContainer>(this.consumparameter);
	}

	public static class UncertainParameterContainer {

		private final StorableParameters storableParameters;
		private final IDistributionFunction start;
		private final IDistributionFunction number;
		private final IDistributionFunction cycle;
		private final boolean isCyclic;

		private final IDistributionFunction earliestDueDate;
		private final IDistributionFunction latestDueDate;

		/**
		 * Creates an {@link UncertainParameterContainer} which is not cyclic
		 * and where {@link UncertainParameterContainer#getCycle()},
		 * {@link UncertainParameterContainer#getEarliestDueDate()} and
		 * {@link UncertainParameterContainer#getLatestDueDate()} returns null.
		 * 
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
			this(storableParameters, number, start, null, null, null);
		}

		/**
		 * Creates an {@link UncertainParameterContainer} where
		 * {@link UncertainParameterContainer#getEarliestDueDate()} and
		 * {@link UncertainParameterContainer#getLatestDueDate()} returns null.
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
		 */
		public UncertainParameterContainer(final StorableParameters storableParameters,
				final IDistributionFunction number, final IDistributionFunction start,
				final IDistributionFunction cycle) {
			this(storableParameters, number, start, cycle, null, null);
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
		 *            - cycle when defined amount of defined storables are
		 *            consumed/ordered/...
		 * @param earliestDueDate
		 *            - earliest delivery of the order, after order is created,
		 *            see {@link UncertainParameterContainer#getCycle()}
		 * @param latestDueDate
		 *            - latest delivery of the order, after order is created,
		 *            see {@link UncertainParameterContainer#getCycle()}
		 */
		public UncertainParameterContainer(final StorableParameters storableParameters,
				final IDistributionFunction number, final IDistributionFunction start,
				final IDistributionFunction cycle, final IDistributionFunction earliestDueDate,
				final IDistributionFunction latestDueDate) {
			this.start = start;
			this.number = number;
			this.cycle = cycle;
			this.isCyclic = (this.cycle != null);
			this.storableParameters = storableParameters;
			this.earliestDueDate = earliestDueDate;
			this.latestDueDate = latestDueDate;
		}

		/**
		 * Returns earliest delivery of the order, after order is created, see
		 * {@link UncertainParameterContainer#getCycle()}. Theoretical
		 * background are time windows in the context of VRP.
		 * 
		 * @return
		 */
		public IDistributionFunction getEarliestDueDate() {
			return earliestDueDate;
		}

		/**
		 * Returns latest delivery of the order, after order is created, see
		 * {@link UncertainParameterContainer#getCycle()}. Theoretical
		 * background are time windows in the context of VRP.
		 * 
		 * @return
		 */
		public IDistributionFunction getLatestDueDate() {
			return latestDueDate;
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
		public IDistributionFunction getNumber() {
			return number;
		}

		/**
		 * Returns the cycle when defined amount of defined storables are
		 * consumed/ordered/...
		 * 
		 * Can return null.
		 * 
		 * @return
		 */
		public IDistributionFunction getCycle() {
			return cycle;
		}

		/**
		 * Returns the start when the defined amount the first time are
		 * consumed/ordered/...
		 * 
		 * @return
		 */
		public IDistributionFunction getStart() {
			return start;
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

	}

}
