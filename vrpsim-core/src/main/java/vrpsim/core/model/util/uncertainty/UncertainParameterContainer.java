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
package vrpsim.core.model.util.uncertainty;

import java.util.ArrayList;
import java.util.List;

import vrpsim.core.model.structure.util.storage.StorableParameters;

public class UncertainParameterContainer {

	private final StorableParameters storableParameters;

	private final IDistributionFunction startDistributionFunction;
	private Double startInstance;
	private List<Double> startInstances = new ArrayList<>();

	private final IDistributionFunction numberDistributionFunction;
	private Double numberInstance;
	private List<Double> numberInstances = new ArrayList<>();

	private final IDistributionFunction cycleDistributionFunction;
	private Double cycleInstance;
	private List<Double> cycleInstances = new ArrayList<>();

	private final IDistributionFunction earliestDueDateDistributionFunction;
	private Double earliestDueDateInstance;
	private List<Double> earliestDueDateInstances = new ArrayList<>();

	private final IDistributionFunction latestDueDateDistributionFunction;
	private Double latestDueDateInstance;
	private List<Double> latestDueDateInstances = new ArrayList<>();

	private final boolean adaptDueDatesToSimulationTime;

	/**
	 * @param storableParameters
	 *            - defining the storable which are consumed/ordered/...
	 * @param number
	 *            - amount of storables which are consumed/ordered/...
	 * @param start
	 *            - the start when the defined amount the first time are
	 *            consumed/ordered/...
	 */
	public UncertainParameterContainer(final StorableParameters storableParameters, final IDistributionFunction number, final IDistributionFunction start) {
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
	public UncertainParameterContainer(final StorableParameters storableParameters, final IDistributionFunction number, final IDistributionFunction start,
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
	 *            see {@link UncertainParameterContainer#getNewRealizationOfCycleDistributionFunction()}
	 * @param latestDueDate
	 *            - latest delivery of the order, after order is created,
	 *            see {@link UncertainParameterContainer#getNewRealizationOfCycleDistributionFunction()}
	 * @param adaptDueDatesToSimulationTime
	 *            - define if the due dates have to be adapted to simulation
	 *            time.
	 */
	public UncertainParameterContainer(final StorableParameters storableParameters, final IDistributionFunction number, final IDistributionFunction earliestDueDate,
			final IDistributionFunction latestDueDate, final boolean adaptDueDatesToSimulationTime) {
		this(storableParameters, number, null, null, earliestDueDate, latestDueDate, adaptDueDatesToSimulationTime);
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
	 *            see {@link UncertainParameterContainer#getNewRealizationOfCycleDistributionFunction()}
	 * @param latestDueDate
	 *            - latest delivery of the order, after order is created,
	 *            see {@link UncertainParameterContainer#getNewRealizationOfCycleDistributionFunction()}
	 * @param adaptDueDatesToSimulationTime
	 *            - define if the due dates have to be adapted to simulation
	 *            time.
	 */
	public UncertainParameterContainer(final StorableParameters storableParameters, final IDistributionFunction number, final IDistributionFunction start,
			final IDistributionFunction cycle, final IDistributionFunction earliestDueDate, final IDistributionFunction latestDueDate,
			final boolean adaptDueDatesToSimulationTime) {
		this.startDistributionFunction = start;
		this.numberDistributionFunction = number;
		this.cycleDistributionFunction = cycle;
		this.storableParameters = storableParameters;
		this.earliestDueDateDistributionFunction = earliestDueDate;
		this.latestDueDateDistributionFunction = latestDueDate;
		this.adaptDueDatesToSimulationTime = adaptDueDatesToSimulationTime;
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
	 * Returns true if the container is cyclic, means if a value for
	 * {@link UncertainParameterContainer#getNewRealizationOfCycleDistributionFunction()} is defined.
	 * 
	 * @return
	 */
	public boolean isCyclic() {
		return this.cycleDistributionFunction != null;
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
	 * Returns earliest delivery of the order, after order is created, see
	 * {@link UncertainParameterContainer#getNewRealizationOfCycleDistributionFunction()}. Theoretical
	 * background are time windows in the context of VRP.
	 * 
	 * @return
	 */
	public Double getNewRealizationFromEarliestDueDateDistributionFunction() {
		if (this.earliestDueDateDistributionFunction != null) {
			this.earliestDueDateInstance = this.earliestDueDateDistributionFunction.getNumber();
			this.earliestDueDateInstances.add(this.earliestDueDateInstance);
			return this.earliestDueDateInstance;
		} else {
			return null;
		}
	}

	/**
	 * Returns latest delivery of the order, after order is created, see
	 * {@link UncertainParameterContainer#getNewRealizationOfCycleDistributionFunction()}. Theoretical
	 * background are time windows in the context of VRP.
	 * 
	 * @return
	 */
	public Double getNewRealizationFromLatestDueDateDistributionFunction() {
		if (this.latestDueDateDistributionFunction != null) {
			this.latestDueDateInstance = this.latestDueDateDistributionFunction.getNumber();
			this.latestDueDateInstances.add(this.latestDueDateInstance);
			return this.latestDueDateInstance;
		} else {
			return null;
		}

	}

	/**
	 * Returns the number of storables which are consumed/ordered/...
	 * 
	 * @return
	 */
	public Double getNewRealizationFromNumberDistributionFunction() {
		if (this.numberDistributionFunction != null) {
			this.numberInstance = this.numberDistributionFunction.getNumber();
			this.numberInstances.add(this.numberInstance);
			return this.numberInstance;
		} else {
			return null;
		}
	}

	/**
	 * Returns the cycle when defined amount of defined storables are
	 * consumed/ordered/...
	 * 
	 * Can return null.
	 * 
	 * @return
	 */
	public Double getNewRealizationOfCycleDistributionFunction() {
		if (this.cycleDistributionFunction != null) {
			this.cycleInstance = this.cycleDistributionFunction.getNumber();
			this.cycleInstances.add(this.cycleInstance);
			return this.cycleInstance;
		} else {
			return null;
		}
	}

	/**
	 * Returns the start when the defined amount the first time are
	 * consumed/ordered/...
	 * 
	 * @return
	 */
	public Double getNewRealizationFromStartDistributionFunction() {

		if (this.startDistributionFunction != null) {
			this.startInstance = this.startDistributionFunction.getNumber();
			this.startInstances.add(this.startInstance);
			return this.startInstance;
		} else {
			return null;
		}
	}

	public Double getStartInstance() {
		return startInstance;
	}

	public List<Double> getStartInstances() {
		return startInstances;
	}

	public Double getNumberInstance() {
		return numberInstance;
	}

	public List<Double> getNumberInstances() {
		return numberInstances;
	}

	public Double getCycleInstance() {
		return cycleInstance;
	}

	public List<Double> getCycleInstances() {
		return cycleInstances;
	}

	public Double getEarliestDueDateInstance() {
		return earliestDueDateInstance;
	}

	public List<Double> getEarliestDueDateInstances() {
		return earliestDueDateInstances;
	}

	public Double getLatestDueDateInstance() {
		return latestDueDateInstance;
	}

	public List<Double> getLatestDueDateInstances() {
		return latestDueDateInstances;
	}

}
