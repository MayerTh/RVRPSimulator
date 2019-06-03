package vrpsim.simulationmodel.initialbehaviour.simpleapi.impl.spiral;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;

import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.IInitialBehaviourProviderHandler;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.CustomerAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DepotAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.DriverAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.IJob;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.LocationAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.TourAPI;
import vrpsim.simulationmodel.initialbehaviour.simpleapi.api.model.VehicleAPI;

public class StaubsaugerHandler_v3 implements IInitialBehaviourProviderHandler {

	private Raster raster;
	private int rasterSize;
	private double maxNumberInCluster;
	private double medianNumberInClutser;
	private double meanNumberInCluster;

	private double maximumNumberInRasterCellPercentage;

	/**
	 * @param maximumNumberInRasterCellPercentage
	 *            is element of [0,1]
	 */
	public StaubsaugerHandler_v3(double maximumNumberInRasterCellPercentage) {
		this.maximumNumberInRasterCellPercentage = maximumNumberInRasterCellPercentage;
	}

	public StaubsaugerHandler_v3(int rasterSize) {
		this.rasterSize = rasterSize;
		this.maximumNumberInRasterCellPercentage = -1;
	}

	public void setMaximumNumberInRasterCellPercentage(double maximumNumberInRasterCellPercentage) {
		this.maximumNumberInRasterCellPercentage = maximumNumberInRasterCellPercentage;
	}

	public void setRasterSize(int rasterSize) {
		this.rasterSize = rasterSize;
	}

	public int getRasterSize() {
		return rasterSize;
	}

	public Raster getRaster() {
		return this.raster;
	}

	public double getMeanNumberInCluster() {
		return meanNumberInCluster;
	}

	public double getMedianNumberInClutser() {
		return medianNumberInClutser;
	}

	public double getMaxNumberInCluster() {
		return this.maxNumberInCluster;
	}

	public double determinePlannedMaximumNumberInRasterCell(List<CustomerAPI> customers) {
		return customers.size() * this.maximumNumberInRasterCellPercentage;
	}

	@Override
	public List<TourAPI> handleOrder(List<VehicleAPI> vehicles, List<CustomerAPI> customers, List<DepotAPI> depots,
			List<DriverAPI> drivers) {
		List<TourAPI> res = new ArrayList<>();
		List<IJob> order = new ArrayList<>();
		order.add(depots.get(0)); // start at depot

		// Optimale Grenzen des Rasters ermitteln
		double lowestX = Double.MAX_VALUE;
		double lowestY = Double.MAX_VALUE;
		double highestX = Double.MIN_VALUE;
		double highestY = Double.MIN_VALUE;
		for (CustomerAPI customer : customers) {
			LocationAPI loc = customer.getLocation();
			if (loc.getxCoord() > highestX) {
				highestX = loc.getxCoord();
			} else if (loc.getxCoord() < lowestX) {
				lowestX = loc.getxCoord();
			}
			if (loc.getyCoord() > highestY) {
				highestY = loc.getyCoord();
			} else if (loc.getyCoord() < lowestY) {
				lowestY = loc.getyCoord();
			}
		}
		LocationAPI lowerLeftCorner = new LocationAPI(lowestX, lowestY);
		LocationAPI upperRightCorner = new LocationAPI(highestX, highestY);

		if (this.maximumNumberInRasterCellPercentage == -1) {
			this.raster = new Raster(this.rasterSize, this.rasterSize, lowerLeftCorner, upperRightCorner);
		} else {
			double plannedMaximumNumberInRasterCell = determinePlannedMaximumNumberInRasterCell(customers);
			this.raster = determineRaster(lowerLeftCorner, upperRightCorner, plannedMaximumNumberInRasterCell, customers);
		}

		int columns = this.raster.getColumns();
		int rows = this.raster.getRows();

		// save stats
		double[] maxAndMedian = determineMaxJobsInCell(columns, rows, customers, this.raster);
		this.maxNumberInCluster = maxAndMedian[0];
		this.medianNumberInClutser = maxAndMedian[1];
		this.meanNumberInCluster = maxAndMedian[2];
		this.rasterSize = columns;

		// ermittel Mittelpunkt des Rasters und setze als Startpunkt des Roboters
		int centreRow;
		int centreColumn;

		if ((rows % 2) == 1) {
			centreRow = ((rows - 1) / 2);
		} else {
			centreRow = (rows / 2) - 1;
		}

		if ((columns % 2) == 1) {
			centreColumn = ((columns - 1) / 2);
		} else {
			centreColumn = (columns / 2) - 1;
		}

		// LocationAPI center = new LocationAPI((lowerLeftCorner.getxCoord() +
		// upperRightCorner.getxCoord()) / 2,
		// (lowerLeftCorner.getyCoord() + upperRightCorner.getyCoord()) / 2);
		// System.out.println(center.getxCoord()+" "+center.getyCoord());
		// System.out.println(raster.getCellColumn(center)+"
		// "+raster.getCellRow(center));
		Staubsauger staubi = new Staubsauger(centreColumn, centreRow, raster);

		order = addJobsInCellToOrder(raster, staubi, customers, order);

		// first step
		staubi.moveRight();

		List<CustomerAPI> customersInCell2 = raster.getJobsInCell(staubi.getCurrentColumn(), staubi.getCurrentRow(), customers);
		for (CustomerAPI customer : customersInCell2) {
			order.add(customer);
		}

		// rule-based behavior
		while (staubi.hasUnvisitedCell()) {

			// wenn startzelle über staubi, versuche erst oben, dann rechts, dann unten
			if (staubi.getStartRow() > staubi.getCurrentRow()) {
				if (staubi.isCellUnvisited(staubi.getCurrentColumn(), staubi.getCurrentRow() + 1)) {
					staubi.moveUp();
					order = addJobsInCellToOrder(raster, staubi, customers, order);
				} else if (staubi.isCellUnvisited(staubi.getCurrentColumn() + 1, staubi.getCurrentRow())) {
					staubi.moveRight();
					order = addJobsInCellToOrder(raster, staubi, customers, order);
				} else if (staubi.isCellUnvisited(staubi.getCurrentColumn(), staubi.getCurrentRow() - 1)) {
					staubi.moveDown();
					order = addJobsInCellToOrder(raster, staubi, customers, order);
				} else {
					// hier abfangen was passiert, wenn um die Zelle herum alle schon besucht wurden
				}
			}

			// wenn startzelle unter staubi, versuche erst unten, dann links, dann oben
			else if (staubi.getStartRow() < staubi.getCurrentRow()) {
				if (staubi.isCellUnvisited(staubi.getCurrentColumn(), staubi.getCurrentRow() - 1)) {
					staubi.moveDown();
					order = addJobsInCellToOrder(raster, staubi, customers, order);
				} else if (staubi.isCellUnvisited(staubi.getCurrentColumn() - 1, staubi.getCurrentRow())) {
					staubi.moveLeft();
					order = addJobsInCellToOrder(raster, staubi, customers, order);
				} else if (staubi.isCellUnvisited(staubi.getCurrentColumn(), staubi.getCurrentRow() + 1)) {
					staubi.moveUp();
					order = addJobsInCellToOrder(raster, staubi, customers, order);
				} else {
					// hier abfangen was passiert, wenn um die Zelle herum alle schon besucht wurden
				}
			}
			// wenn startzelle links staubi, versuche erst links, dann oben, dann rechts
			else if (staubi.getStartColumn() < staubi.getCurrentColumn()) {
				if (staubi.isCellUnvisited(staubi.getCurrentColumn() - 1, staubi.getCurrentRow())) {
					staubi.moveLeft();
					order = addJobsInCellToOrder(raster, staubi, customers, order);
				} else if (staubi.isCellUnvisited(staubi.getCurrentColumn(), staubi.getCurrentRow() + 1)) {
					staubi.moveUp();
					order = addJobsInCellToOrder(raster, staubi, customers, order);
				} else if (staubi.isCellUnvisited(staubi.getCurrentColumn() + 1, staubi.getCurrentRow())) {
					staubi.moveRight();
					order = addJobsInCellToOrder(raster, staubi, customers, order);
				} else {
					// hier abfangen was passiert, wenn um die Zelle herum alle schon besucht wurden
				}
			}
			// wenn startzelle rechts staubi, versuche erst rechts, dann unten, dann links
			else if (staubi.getStartColumn() > staubi.getCurrentColumn()) {
				if (staubi.isCellUnvisited(staubi.getCurrentColumn() + 1, staubi.getCurrentRow())) {
					staubi.moveRight();
					order = addJobsInCellToOrder(raster, staubi, customers, order);
				} else if (staubi.isCellUnvisited(staubi.getCurrentColumn(), staubi.getCurrentRow() - 1)) {
					staubi.moveDown();
					order = addJobsInCellToOrder(raster, staubi, customers, order);
				} else if (staubi.isCellUnvisited(staubi.getCurrentColumn() - 1, staubi.getCurrentRow())) {
					staubi.moveLeft();
					order = addJobsInCellToOrder(raster, staubi, customers, order);
				} else {
					// hier abfangen was passiert, wenn um die Zelle herum alle schon besucht wurden
				}
			}
		}
		order = improveOrder(order);
		order.add(depots.get(0)); // end at depot
		TourAPI tour = new TourAPI(order, vehicles.get(0), drivers.get(0));
		res.add(tour);
		return res;
	}

	private Raster determineRaster(LocationAPI lowerLeftCorner, LocationAPI upperRightCorner, double maximumNumberInRasterCell2,
			List<CustomerAPI> customers) {
		int columAndRows = 3;
		Raster currentRaster = new Raster(columAndRows, columAndRows, lowerLeftCorner, upperRightCorner);
		double numberInRasterCell = determineMaxJobsInCell(columAndRows, columAndRows, customers, currentRaster)[0];
		while (maximumNumberInRasterCell2 < numberInRasterCell) {
			columAndRows++;
			currentRaster = new Raster(columAndRows, columAndRows, lowerLeftCorner, upperRightCorner);
			numberInRasterCell = determineMaxJobsInCell(columAndRows, columAndRows, customers, currentRaster)[0];
		}
		return currentRaster;
	}

	private double[] determineMaxJobsInCell(int columns, int rows, List<CustomerAPI> customers, Raster raster) {
		int max = Integer.MIN_VALUE;
		double[] values = new double[columns * rows];
		int index = 0;
		for (int c = 0; c < columns; c++) {
			for (int r = 0; r < rows; r++) {
				max = Integer.max(max, raster.getJobsInCell(c, r, customers).size());
				values[index++] = raster.getJobsInCell(c, r, customers).size();
			}
		}

		double[] result = new double[3];
		result[0] = max;
		result[1] = StatUtils.percentile(values, 50);
		result[2] = StatUtils.mean(values);
		return result;
	}

	private double getDistance(LocationAPI location1, LocationAPI location2) {
		double a = Math.abs(location1.getxCoord() - location2.getxCoord());
		double b = Math.abs(location1.getyCoord() - location2.getyCoord());
		double c = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
		return c;
	}

	private List<IJob> addJobsInCellToOrder(Raster raster, Staubsauger staubi, List<CustomerAPI> customers, List<IJob> order) {
		List<CustomerAPI> customersInCell = raster.getJobsInCell(staubi.getCurrentColumn(), staubi.getCurrentRow(), customers);
		order.addAll(greedySort(order.get(order.size() - 1), customersInCell));
		return order;
	}

	private List<CustomerAPI> greedySort(IJob currentJob, List<CustomerAPI> customers) {
		List<CustomerAPI> result = new ArrayList<CustomerAPI>();

		while (!customers.isEmpty()) {
			double minDistance = Double.MAX_VALUE;
			CustomerAPI bestCustomer = null;
			for (CustomerAPI customer : customers) {
				double distance = getDistance(currentJob.getLocation(), customer.getLocation());
				if (distance < minDistance) {
					minDistance = distance;
					bestCustomer = customer;
				}
			}
			result.add(bestCustomer);
			customers.remove(bestCustomer);
		}
		return result;
	}

	private List<IJob> improveOrder(List<IJob> orders) {
		double lowestCosts = calculateDistance(orders);
		double oldCosts = calculateDistance(orders);
		int bestIndex = 0;
		int ordersize = new Integer(orders.size());
		IJob workWith;
		IJob customerToChange = null;

		for (int k = 0; k < 15; k++) {
			for (int i = 1; i < ordersize - 2; i++) {
				workWith = orders.get(i);
				orders.remove(workWith);
				for (int j = 1; j < ordersize - 1; j++) {
					orders.add(j, workWith);
					double distance = calculateDistance(orders); // calculateDistance(orders.get(j-1).getLocation(), workWith.getLocation(),
																	// orders.get(j).getLocation()) < lowestCosts
					if (distance < lowestCosts) {
						lowestCosts = distance;
						bestIndex = j;
						customerToChange = workWith;
					}
					orders.remove(workWith);
				}
				orders.add(i, workWith);
			}
			if (customerToChange != null) {
				orders.remove(customerToChange);
				orders.add(bestIndex, customerToChange);
				// System.out.println("old costs: "+ oldCosts+ " new costs: "+ lowestCosts);
			}
		}

		return orders;
	}

	// private double calculateDistance (LocationAPI loc1, LocationAPI loc2,
	// LocationAPI loc3) {
	// return getDistance(loc1, loc2) + getDistance(loc2, loc3);
	// }

	private double calculateDistance(List<IJob> orders) {
		double res = 0;
		for (int i = 0; i < orders.size() - 1; i++) {
			res += getDistance(orders.get(i).getLocation(), orders.get(i + 1).getLocation());
		}
		return res;
	}
}