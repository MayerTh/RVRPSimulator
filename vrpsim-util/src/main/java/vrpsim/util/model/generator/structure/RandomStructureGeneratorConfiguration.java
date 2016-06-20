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
package vrpsim.util.model.generator.structure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.network.NetworkService;
import vrpsim.core.model.structure.util.storage.CanStoreType;
import vrpsim.core.model.structure.util.storage.Capacity;
import vrpsim.core.model.structure.util.storage.StorableParameters;
import vrpsim.core.model.structure.util.storage.StorableType;
import vrpsim.util.model.generator.GeneratorConfigurationInitializationException;

public class RandomStructureGeneratorConfiguration {

	// TODO Occasional Driver

	private static Logger logger = LoggerFactory.getLogger(RandomStructureGeneratorConfiguration.class);

	private NetworkService networkService;

	private String capacityString;
	private Capacity capacity;
	private String canStoreTypeString;
	private CanStoreType canStoreType;
	private String storableTypeString;
	private StorableType storableType;
	private StorableParameters storableParameters;

	private Integer numberOfStaticCustomers;
	private Double capacityOfStaticCustomers;
	private Double minConsumptionCycleOfStaticCustomer;
	private Double maxConsumptionCycleOfStaticCustomer;
	private Double minConsumptionAmountOfStaticCustomer;
	private Double maxConsumptionAmountOfStaticCustomer;

	private Integer numberOfDynamicCustomers;
	private Double capacityOfDynamicCustomers;
	private Double minOrderCycleOfDynamicCustomer;
	private Double maxOrderCycleOfDynamicCustomer;
	private Double minOrderAmountOfDynamicCustomer;
	private Double maxOrderAmountOfDynamicCustomer;

	private Integer numberOfDefaultDepot;
	private Double capacityOfDefaultDepot;
	private Integer numberStorablesInDefaultDepot;

	private Integer numberOfSourceDepot;
	private Double capacityOfSourceDepot;

	private Integer numberOfVehicles;
	private Double capacityOfVehicles;
	private Double speedOfVehicles;

	private Integer numberOfDriver;

	public void initialize() throws GeneratorConfigurationInitializationException {

		// Network
		if (this.networkService == null) {
			throw new GeneratorConfigurationInitializationException("No network is set");
		}

		// Capacity
		logger.info((this.capacityString == null) ? "No capacity unit set, default value is 'amount'."
				: ("Capacity created with unit " + this.capacityString + " and value 1."));
		this.capacityString = (this.capacityString == null) ? "amount" : this.capacityString;
		this.capacity = new Capacity(capacityString, 1.0);

		// Storable parameters
		logger.info((this.canStoreTypeString == null) ? "No CanStoreType is set, default value is 'compartment'."
				: ("CanStoreType " + this.canStoreTypeString + "created.); "));
		this.canStoreTypeString = (this.canStoreTypeString == null) ? "compartment" : this.canStoreTypeString;
		this.canStoreType = new CanStoreType(this.canStoreTypeString);
		
		logger.info((this.storableTypeString == null) ? "No StorableType is set, default value is 'package'."
				: ("StorableType " + this.storableTypeString + "created.); "));
		this.storableType = new StorableType((this.storableTypeString == null) ? "package" : this.storableTypeString,
				canStoreType);
		this.storableParameters = new StorableParameters(1, this.capacity, this.storableType);

		// Static customer
		logger.info((this.numberOfStaticCustomers == null) ? "No number of static customers set, default value is 2."
				: ("Number of static customers " + this.numberOfStaticCustomers + "."));
		this.numberOfStaticCustomers = (this.numberOfStaticCustomers == null) ? new Integer(2)
				: this.numberOfStaticCustomers;
		logger.info((this.capacityOfStaticCustomers == null)
				? "No capacity amount of static customers set, default value is 50."
				: ("Number of static customers " + this.capacityOfStaticCustomers + ". "));
		this.capacityOfStaticCustomers = (this.capacityOfStaticCustomers == null) ? new Double(50)
				: this.capacityOfStaticCustomers;
		logger.info((this.minConsumptionAmountOfStaticCustomer == null)
				? "No min cosumption of static customers set, default value is 1."
				: ("Min cosumption of static customers is " + this.minConsumptionAmountOfStaticCustomer + "."));
		this.minConsumptionAmountOfStaticCustomer = (this.minConsumptionAmountOfStaticCustomer == null) ? new Double(1)
				: this.minConsumptionAmountOfStaticCustomer;
		logger.info((this.maxConsumptionAmountOfStaticCustomer == null)
				? "No max cosumption of static customers set, default value is 15."
				: ("Max cosumption of static customers is " + this.maxConsumptionAmountOfStaticCustomer + "."));
		this.maxConsumptionAmountOfStaticCustomer = (this.maxConsumptionAmountOfStaticCustomer == null) ? new Double(15)
				: this.maxConsumptionAmountOfStaticCustomer;
		logger.info((this.minConsumptionCycleOfStaticCustomer == null)
				? "No min cosumption cycle of static customers set, default value is 28800 (20 days)."
				: ("Min cosumption cycle of static customers is " + this.minConsumptionCycleOfStaticCustomer + "."));
		this.minConsumptionCycleOfStaticCustomer = (this.minConsumptionCycleOfStaticCustomer == null) ? new Double(28800)
				: this.minConsumptionCycleOfStaticCustomer;
		logger.info((this.maxConsumptionCycleOfStaticCustomer == null)
				? "No max cosumption cycle of static customers set, default value is 43200 (30 days)."
				: ("Max cosumption cycle of static customers is " + this.maxConsumptionCycleOfStaticCustomer + "."));
		this.maxConsumptionCycleOfStaticCustomer = (this.maxConsumptionCycleOfStaticCustomer == null) ? new Double(43200)
				: this.maxConsumptionCycleOfStaticCustomer;

		// Dynamic Customer
		logger.info((this.numberOfDynamicCustomers == null) ? "No number of dynamic customers set, default value is 2."
				: ("Number of static customers " + this.numberOfDynamicCustomers + "."));
		this.numberOfDynamicCustomers = (this.numberOfDynamicCustomers == null) ? new Integer(2)
				: this.numberOfDynamicCustomers;
		logger.info((this.capacityOfDynamicCustomers == null)
				? "No capacity amount of dynamic customers set, default value is 50."
				: ("Number of static customers " + this.capacityOfDynamicCustomers + "."));
		this.capacityOfDynamicCustomers = (this.capacityOfDynamicCustomers == null) ? new Double(50)
				: this.capacityOfDynamicCustomers;
		logger.info((this.minOrderAmountOfDynamicCustomer == null)
				? "No min order amount of dynamic customers set, default value is 1."
				: ("Min order amount of dynamic customers is " + this.minOrderAmountOfDynamicCustomer + "."));
		this.minOrderAmountOfDynamicCustomer = (this.minOrderAmountOfDynamicCustomer == null) ? new Double(1)
				: this.minOrderAmountOfDynamicCustomer;
		logger.info((this.maxOrderAmountOfDynamicCustomer == null)
				? "No max order amount of dynamic customers set, default value is 15."
				: ("Max order amount of dynamic customers is " + this.maxOrderAmountOfDynamicCustomer + "."));
		this.maxOrderAmountOfDynamicCustomer = (this.maxOrderAmountOfDynamicCustomer == null) ? new Double(15)
				: this.maxOrderAmountOfDynamicCustomer;
		logger.info((this.minOrderCycleOfDynamicCustomer == null)
				? "No min order cycle of dynamic customers set, default value is 28800 (20 days)."
				: ("Min order cycle of dynamic customers is " + this.minOrderCycleOfDynamicCustomer + "."));
		this.minOrderCycleOfDynamicCustomer = (this.minOrderCycleOfDynamicCustomer == null) ? new Double(28800)
				: this.minOrderCycleOfDynamicCustomer;
		logger.info((this.maxOrderCycleOfDynamicCustomer == null)
				? "No max order cycle of dynamic customers set, default value is 43200 (30 days)."
				: ("Max order cycle of dynamic customers is " + this.maxOrderCycleOfDynamicCustomer + "."));
		this.maxOrderCycleOfDynamicCustomer = (this.maxOrderCycleOfDynamicCustomer == null) ? new Double(43200)
				: this.maxOrderCycleOfDynamicCustomer;
		
		// Depot
		logger.info((this.numberOfDefaultDepot == null) ? "No number of default depot set, default value is 0."
				: "Number of default depot is " + this.numberOfDefaultDepot);
		this.numberOfDefaultDepot = this.numberOfDefaultDepot == null ? new Integer(0) : this.numberOfDefaultDepot;

		if (Integer.compare(this.numberOfDefaultDepot, 0) > 0) {
			logger.info((this.capacityOfDefaultDepot == null) ? "No capacity for default depot set, value is 1000."
					: "Capacity of default depot is " + this.capacityOfDefaultDepot + ".");
			this.capacityOfDefaultDepot = this.capacityOfDefaultDepot == null ? new Double(1000)
					: this.capacityOfDefaultDepot;
			logger.info((this.numberStorablesInDefaultDepot == null)
					? "No number of storable for default depot set, value is 1000."
					: "Number of storable for default depot is " + this.numberStorablesInDefaultDepot + ".");
			this.numberStorablesInDefaultDepot = this.numberStorablesInDefaultDepot == null ? new Integer(1000)
					: this.numberStorablesInDefaultDepot;

			if (this.numberStorablesInDefaultDepot > this.capacityOfDefaultDepot) {
				throw new GeneratorConfigurationInitializationException(
						"Capacity of default depot is smaller than number inside. Please adapt configuration.");
			}
		}

		// Source Depot
		logger.info((this.numberOfSourceDepot == null) ? "No number of source depot set, default value is 1."
				: "Number of source depot is " + this.numberOfSourceDepot);
		this.numberOfSourceDepot = this.numberOfSourceDepot == null ? new Integer(1) : this.numberOfSourceDepot;
		logger.info((this.capacityOfSourceDepot == null) ? "No capacity for source depot set, value is 1000."
				: "Capacity of default depot is " + this.capacityOfSourceDepot + ".");
		this.capacityOfSourceDepot = this.capacityOfSourceDepot == null ? new Double(1000) : this.capacityOfSourceDepot;

		// Vehicle
		logger.info((this.numberOfVehicles == null) ? "No number of vehicle set, default value is 1."
				: "Number of vehicle is " + this.numberOfVehicles);
		this.numberOfVehicles = this.numberOfVehicles == null ? new Integer(1) : this.numberOfVehicles;
		logger.info((this.capacityOfVehicles == null) ? "No capacity for vehicle set, value is 10."
				: "Capacity of vehicle depot is " + this.capacityOfVehicles + ".");
		this.capacityOfVehicles = this.capacityOfVehicles == null ? new Double(10) : this.capacityOfVehicles;
		logger.info((this.speedOfVehicles == null) ? "No max speed of vehicle set, default value is 80."
				: "Max speed of vehicle is " + this.speedOfVehicles);
		this.speedOfVehicles = this.speedOfVehicles == null ? new Double(80) : this.speedOfVehicles;

		// Driver
		logger.info((this.numberOfDriver == null) ? "No number of driver set, default value is 1."
				: "Number of driver is " + this.numberOfDriver);
		this.numberOfDriver = this.numberOfDriver == null ? new Integer(1) : this.numberOfDriver;

	}

	/**
	 * If not set default value is 28800 (20 days).
	 * 
	 * @param minOrderCycleOfDynamicCustomer
	 */
	public RandomStructureGeneratorConfiguration setMinOrderCycleOfDynamicCustomer(Double minOrderCycleOfDynamicCustomer) {
		this.minOrderCycleOfDynamicCustomer = minOrderCycleOfDynamicCustomer;
		return this;
	}

	/**
	 * If not set default value is 43200 (30 days).
	 * 
	 * @param maxOrderCycleOfDynamicCustomer
	 */
	public RandomStructureGeneratorConfiguration setMaxOrderCycleOfDynamicCustomer(Double maxOrderCycleOfDynamicCustomer) {
		this.maxOrderCycleOfDynamicCustomer = maxOrderCycleOfDynamicCustomer;
		return this;
	}

	/**
	 * If not set default value is 1.
	 * 
	 * @param minOrderAmountOfDynamicCustomer
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setMinOrderAmountOfDynamicCustomer(Double minOrderAmountOfDynamicCustomer) {
		this.minOrderAmountOfDynamicCustomer = minOrderAmountOfDynamicCustomer;
		return this;
	}

	/**
	 * If not set default value is 15.
	 * 
	 * @param maxOrderAmountOfDynamicCustomer
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setMaxOrderAmountOfDynamicCustomer(Double maxOrderAmountOfDynamicCustomer) {
		this.maxOrderAmountOfDynamicCustomer = maxOrderAmountOfDynamicCustomer;
		return this;
	}

	/**
	 * If not set default value is 28800 (20 days).
	 * 
	 * @param minConsumptionCycleOfStaticCustomer
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setMinConsumptionCycleOfStaticCustomer(
			Double minConsumptionCycleOfStaticCustomer) {
		this.minConsumptionCycleOfStaticCustomer = minConsumptionCycleOfStaticCustomer;
		return this;
	}

	/**
	 * If not set default value is 43200 (30 days).
	 * 
	 * @param maxConsumptionCycleOfStaticCustomer
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setMaxConsumptionCycleOfStaticCustomer(
			Double maxConsumptionCycleOfStaticCustomer) {
		this.maxConsumptionCycleOfStaticCustomer = maxConsumptionCycleOfStaticCustomer;
		return this;
	}

	/**
	 * If not set default value is 1.
	 * 
	 * @param minConsumptionAmountOfStaticCustomer
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setMinConsumptionAmountOfStaticCustomer(
			Double minConsumptionAmountOfStaticCustomer) {
		this.minConsumptionAmountOfStaticCustomer = minConsumptionAmountOfStaticCustomer;
		return this;
	}

	/**
	 * If not set default value is 15.
	 * 
	 * @param maxConsumptionAmountOfStaticCustomer
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setMaxConsumptionAmountOfStaticCustomer(
			Double maxConsumptionAmountOfStaticCustomer) {
		this.maxConsumptionAmountOfStaticCustomer = maxConsumptionAmountOfStaticCustomer;
		return this;
	}

	/**
	 * If not set default value is 80.
	 * 
	 * @param maxSpeed
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setSpeedOfVehicle(Double maxSpeed) {
		this.speedOfVehicles = maxSpeed;
		return this;
	}

	/**
	 * Set the {@link NetworkService}, used to configure all home locations. No
	 * default value available.
	 * 
	 * @param network
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setNetwork(NetworkService networkService) {
		this.networkService = networkService;
		return this;
	}

	/**
	 * If not set default value is 1.
	 * 
	 * @param numberOfDriver
	 */
	public RandomStructureGeneratorConfiguration setNumberOfDriver(Integer numberOfDriver) {
		this.numberOfDriver = numberOfDriver;
		return this;
	}

	/**
	 * If not set default value is 1.
	 * 
	 * @param numberOfVehicles
	 */
	public RandomStructureGeneratorConfiguration setNumberOfVehicles(Integer numberOfVehicles) {
		this.numberOfVehicles = numberOfVehicles;
		return this;
	}

	/**
	 * If not set default value is 10.
	 * 
	 * @param capacityOfVehicles
	 */
	public RandomStructureGeneratorConfiguration setCapacityOfVehicles(Double capacityOfVehicles) {
		this.capacityOfVehicles = capacityOfVehicles;
		return this;
	}

	/**
	 * If not set default value is 0.
	 * 
	 * @param numberOfDefaultDepot
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setNumberOfDefaultDepot(Integer numberOfDefaultDepot) {
		this.numberOfDefaultDepot = numberOfDefaultDepot;
		return this;
	}

	/**
	 * If not set default value is 1000.
	 * 
	 * @param capacityOfDefaultDepot
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setCapacityOfDefaultDepot(Double capacityOfDefaultDepot) {
		this.capacityOfDefaultDepot = capacityOfDefaultDepot;
		return this;
	}

	/**
	 * If not set default value is 1000.
	 * 
	 * @param numberStorablesInDefaultDepot
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setNumberStorablesInDefaultDepot(
			Integer numberStorablesInDefaultDepot) {
		this.numberStorablesInDefaultDepot = numberStorablesInDefaultDepot;
		return this;
	}

	/**
	 * If not set default value is 1.
	 * 
	 * @param numberOfSourceDepot
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setNumberOfSourceDepot(Integer numberOfSourceDepot) {
		this.numberOfSourceDepot = numberOfSourceDepot;
		return this;
	}

	/**
	 * If not set default value is 1000.
	 * 
	 * @param capacityOfSourceDepot
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setCapacityOfSourceDepot(Double capacityOfSourceDepot) {
		this.capacityOfSourceDepot = capacityOfSourceDepot;
		return this;
	}

	/**
	 * If not set default value is 50.
	 * 
	 * @param capacityAmountOfDynamicCustomers
	 */
	public RandomStructureGeneratorConfiguration setCapacityOfDynamicCustomers(
			Double capacityAmountOfDynamicCustomers) {
		this.capacityOfDynamicCustomers = capacityAmountOfDynamicCustomers;
		return this;
	}

	/**
	 * If not set default value is 50.
	 * 
	 * @param capacityAmountOfStaticCustomers
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setCapacityOfStaticCustomers(
			Double capacityAmountOfStaticCustomers) {
		this.capacityOfStaticCustomers = capacityAmountOfStaticCustomers;
		return this;
	}

	/**
	 * If not set default value is 2.
	 * 
	 * @param numberOfStaticCustomers
	 */
	public RandomStructureGeneratorConfiguration setNumberOfStaticCustomers(Integer numberOfStaticCustomers) {
		this.numberOfStaticCustomers = numberOfStaticCustomers;
		return this;
	}
	
	/**
	 * If not set default value is 2.
	 * 
	 * @param numberOfStaticCustomers
	 */
	public RandomStructureGeneratorConfiguration setNumberOfDynamicCustomers(Integer numberOfDynamicCustomers) {
		this.numberOfDynamicCustomers = numberOfDynamicCustomers;
		return this;
	}

	/**
	 * If not set default value is package.
	 * 
	 * @param storableTypeString
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setStorableType(String storableTypeString) {
		this.storableTypeString = storableTypeString;
		return this;
	}

	/**
	 * If not set, default value is amount.
	 * 
	 * @param capacityString
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setCapacityUnit(String capacityString) {
		this.capacityString = capacityString;
		return this;
	}

	/**
	 * If not set default value is compartment.
	 * 
	 * @param canStoreType
	 * @return
	 */
	public RandomStructureGeneratorConfiguration setCanStoreType(String canStoreTypeString) {
		this.canStoreTypeString = canStoreTypeString;
		return this;
	}

	public static Logger getLogger() {
		return logger;
	}

	public NetworkService getNetworkService() {
		return networkService;
	}

	public String getCapacityUnit() {
		return capacityString;
	}

	public Capacity getCapacity() {
		return capacity;
	}

	public String getCanStoreTypeString() {
		return canStoreTypeString;
	}

	public CanStoreType getCanStoreType() {
		return canStoreType;
	}

	public String getStorableTypeString() {
		return storableTypeString;
	}

	public StorableType getStorableType() {
		return storableType;
	}

	public StorableParameters getStorableParameters() {
		return storableParameters;
	}

	public Integer getNumberOfStaticCustomers() {
		return numberOfStaticCustomers;
	}

	public Double getCapacityOfStaticCustomers() {
		return capacityOfStaticCustomers;
	}

	public Integer getNumberOfDynamicCustomers() {
		return numberOfDynamicCustomers;
	}

	public Double getCapacityOfDynamicCustomers() {
		return capacityOfDynamicCustomers;
	}

	public Integer getNumberOfDefaultDepot() {
		return numberOfDefaultDepot;
	}

	public Double getCapacityOfDefaultDepot() {
		return capacityOfDefaultDepot;
	}

	public Integer getNumberStorablesInDefaultDepot() {
		return numberStorablesInDefaultDepot;
	}

	public Integer getNumberOfSourceDepot() {
		return numberOfSourceDepot;
	}

	public Double getCapacityOfSourceDepot() {
		return capacityOfSourceDepot;
	}

	public Integer getNumberOfVehicles() {
		return numberOfVehicles;
	}

	public Double getCapacityOfVehicles() {
		return capacityOfVehicles;
	}

	public Integer getNumberOfDriver() {
		return numberOfDriver;
	}

	public Double getSpeedOfVehicles() {
		return speedOfVehicles;
	}

	public Double getCapacityAmountOfStaticCustomers() {
		return capacityOfStaticCustomers;
	}

	public Double getMinConsumptionCycleOfStaticCustomer() {
		return minConsumptionCycleOfStaticCustomer;
	}

	public Double getMaxConsumptionCycleOfStaticCustomer() {
		return maxConsumptionCycleOfStaticCustomer;
	}

	public Double getMinConsumptionAmountOfStaticCustomer() {
		return minConsumptionAmountOfStaticCustomer;
	}

	public Double getMaxConsumptionAmountOfStaticCustomer() {
		return maxConsumptionAmountOfStaticCustomer;
	}

	public Double getMinOrderCycleOfDynamicCustomer() {
		return minOrderCycleOfDynamicCustomer;
	}

	public Double getMaxOrderCycleOfDynamicCustomer() {
		return maxOrderCycleOfDynamicCustomer;
	}

	public Double getMinOrderAmountOfDynamicCustomer() {
		return minOrderAmountOfDynamicCustomer;
	}

	public Double getMaxOrderAmountOfDynamicCustomer() {
		return maxOrderAmountOfDynamicCustomer;
	}

}
