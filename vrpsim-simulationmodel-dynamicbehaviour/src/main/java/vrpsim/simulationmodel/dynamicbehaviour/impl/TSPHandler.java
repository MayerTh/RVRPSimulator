package vrpsim.simulationmodel.dynamicbehaviour.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.network.INode;
import vrpsim.core.model.network.impl.Location;
import vrpsim.core.model.network.impl.NetworkService;
import vrpsim.core.model.structure.StructureService;
import vrpsim.core.simulator.IClock;
import vrpsim.simulationmodel.dynamicbehaviour.DynamicHandlerException;
import vrpsim.simulationmodel.dynamicbehaviour.IDynamicBehaviourProviderHandler;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourActivity;
import vrpsim.simulationmodel.dynamicbehaviour.model.TourState;

/**
 * Re-planning of the rest of the Tour.
 * 
 * For using this TSPHandler:
 * 
 * 1. Install R 2. Install R package tspmeta with command:
 * install.packages("tspmeta") 3. Install R package Rserve with command:
 * install.packages("Rserve") 4. Load library Rserve with command:
 * library(Rserve) 5. Start R service with command: Rserve()
 * 
 * @author mayert
 */
public class TSPHandler implements IDynamicBehaviourProviderHandler {

	private static Logger logger = LoggerFactory.getLogger(TSPHandler.class);
	private final TSPMetaSolvingMethod method;
	private final Random random;

	private final int numberMaxTriesBeforeExeption = 500;

	private final String host = "127.0.0.1";
	private int port = 6311;

	public TSPHandler(TSPMetaSolvingMethod method, long seed) {
		this.method = method;
		this.random = new Random(seed);
	}

	public TSPHandler(TSPMetaSolvingMethod method, long seed, int port) {
		this.method = method;
		this.random = new Random(seed);
		this.port = port;
	}

	@Override
	public void hanldeOrder(List<TourState> currentTourStates, TourActivity newOrder, IClock clock, StructureService structureService,
			NetworkService networkService) throws DynamicHandlerException {

		logger.debug("Start handling dynamic request.");

		if (allEmpty(currentTourStates)) {
			currentTourStates.get(0).getTourActivities().add(newOrder);
		} else if (!moreThanOneCustomerInTourStates(currentTourStates)) {
			for (TourState ts : currentTourStates) {
				if (ts.getTourActivities().size() > 0) {
					ts.getTourActivities().add(newOrder);
					break;
				}
			}
		} else {

			RConnection rConnection = establishRConnection();
			if (rConnection.isConnected()) {
				String errorLine = "";
				try {

					double minResult = Double.MAX_VALUE;
					int[] minTour = new int[1];
					int indexBestTourState = 0;
					boolean inserted = false;

					for (int i = 0; i < currentTourStates.size(); i++) {
						TourState tourState = currentTourStates.get(i);
						if (moreThanOneCustomerInTourState(tourState)) {
							if (tourState.getTourActivities().size() > 0) {

								String[] dataArray = buildXY(tourState, newOrder);
								int indexStart = 1;
								int indexDepot = tourState.getTourActivities().size() + 1;
								logger.debug("Index start is {} and index depot is {}", indexStart, indexDepot);
								boolean isTourValid = false;
								double result = 0;
								int[] tour = new int[1];
								int solutionCounter = 0;
								while (!isTourValid) {

									for (String scriptLine : buildScript(method.getValue(), dataArray, indexStart, indexDepot,
											this.random.nextInt())) {
										errorLine = scriptLine;
										rConnection.eval(scriptLine);
									}

									result = rConnection.eval("attr(tour, \"tour_length\")").asDouble();
									tour = rConnection.eval("as.integer(tour)").asIntegers();
									logger.debug("Min Tour to validate: " + print(tour));
									isTourValid = isTourValid(tour, indexStart, indexDepot);
									logger.debug("Validation result: " + isTourValid);

									solutionCounter++;

									if (solutionCounter > 50) {
										logger.warn("TSPHandler with method {} could not generate a valid solution within {} tries.",
												this.method.toString(), solutionCounter);
									}

									if (solutionCounter > numberMaxTriesBeforeExeption) {
										logger.error(
												"TSPHandler with method {} could not generate a valid solution within {} tries. Solution generation will be terminated.",
												this.method.toString(), solutionCounter);
										String message = "TSPHandler with method " + this.method.toString()
												+ " could not generate a valid solution within " + solutionCounter
												+ " tries. Solution generation will be terminated.";
										rConnection.close();
										throw new DynamicHandlerException(message);
									}
								}

								if (result < minResult) {
									minResult = result;
									minTour = tour;
									indexBestTourState = i;
									inserted = true;
								}
							}
						}
					}

					if (inserted) {
						// Insert
						sort(currentTourStates.get(indexBestTourState), newOrder, minTour);
					} else {
						// No tour with at least one customer.
						currentTourStates.get(0).getTourActivities().add(newOrder);
					}

				} catch (RserveException | REXPMismatchException e) {
					rConnection.close();
					e.printStackTrace();
					logger.error("Problems during evaluation of R commands, line {}. {}", errorLine, e.getMessage());
					throw new RuntimeException("Problems during evaluation of R commands, line " + errorLine + ".");
				}

			} else {
				rConnection.close();
				logger.error("Can not establish R connection.");
				throw new RuntimeException("Can not establish R connection.");
			}

			rConnection.close();
		}

		logger.debug("End handling dynamic request.");

	}

	private boolean moreThanOneCustomerInTourStates(List<TourState> currentTourStates) {
		boolean result = false;
		for (TourState st : currentTourStates) {
			if (moreThanOneCustomerInTourState(st)) {
				result = true;
				break;
			}
		}
		return result;
	}

	private boolean moreThanOneCustomerInTourState(TourState currentTourState) {
		return currentTourState.getTourActivities().size() > 2;
	}

	private boolean isTourValid(int[] tour, int indexStart, int indexDepot) {

		int first = indexStart;
		int depot = indexDepot;
		boolean isTourValid = false;

		for (int i = 0; i < tour.length; i++) {
			if (tour[i] == first) {
				logger.debug("Index first = " + i + ", depot = " + depot);
				if (tour.length > i + 1) {
					int rightBeside = i + 1;
					if (tour[rightBeside] == depot) {
						isTourValid = true;
						break;
					}
				} else {
					int rightBeside = 0;
					if (tour[rightBeside] == depot) {
						isTourValid = true;
						break;
					}
				}

				if (0 <= i - 1) {
					int leftBeside = i - 1;
					if (tour[leftBeside] == depot) {
						isTourValid = true;
						break;
					}
				} else {
					int leftBeside = tour.length - 1;
					if (tour[leftBeside] == depot) {
						isTourValid = true;
						break;
					}
				}

				isTourValid = false;
				break;
			}
		}

		return isTourValid;
	}

	private void sort(TourState tourState, TourActivity newOrder, int[] minTour) {

		logger.debug("Input: " + print(tourState.getTourActivities()));
		logger.debug("R Min tour, by input indexes: " + print(minTour));

		int first = 1;
		int depot = tourState.getTourActivities().size() + 1;
		int minTourFirstIndex = 0;
		boolean goRight = false;

		for (int i = 0; i < minTour.length; i++) {
			if (minTour[i] == first) {
				minTourFirstIndex = i;
				if (minTour.length > i + 1) {
					int goRightIndex = i + 1;
					goRight = minTour[goRightIndex] != depot;
				} else {
					int goRightIndex = 0;
					goRight = minTour[goRightIndex] != depot;
				}
				break;
			}
		}

		logger.debug("Go right: " + goRight + ", firstIndex = " + minTourFirstIndex);

		List<TourActivity> acs = new ArrayList<>();
		int startIndex = minTourFirstIndex;
		int run = minTour[startIndex];
		while (run != depot) {

			if (tourState.getTourActivities().size() < run - 1) {
				acs.add(newOrder);
			} else {
				acs.add(tourState.getTourActivities().get(run - 1));
			}

			if (goRight) {
				startIndex++;
				if (startIndex == minTour.length) {
					startIndex = 0;
				}
				run = minTour[startIndex];
			} else {
				startIndex--;
				if (startIndex < 0) {
					startIndex = minTour.length - 1;
				}
				run = minTour[startIndex];
			}

		}

		logger.debug("Output: " + print(acs));
		tourState.setTourActivities(acs);
	}

	private String print(List<TourActivity> acs) {
		String line = "";
		for (int i = 0; i < acs.size(); i++) {
			line += acs.get(i).getElement().getVRPSimulationModelElementParameters().getId();
			if (i < acs.size() - 1) {
				line += ",";
			}
		}
		return line;
	}

	private String print(int[] is) {
		String line = "";
		for (int i = 0; i < is.length; i++) {
			line += is[i];
			if (i < is.length - 1) {
				line += ",";
			}
		}
		return line;
	}

	private String[] buildXY(TourState currentTourState, TourActivity newOrder) {

		String xLine = "";
		String yLine = "";
		for (TourActivity ta : currentTourState.getTourActivities()) {
			double x = ((INode) ta.getElement().getVRPSimulationModelStructureElementParameters().getHome()).getLocation().getX();
			double y = ((INode) ta.getElement().getVRPSimulationModelStructureElementParameters().getHome()).getLocation().getY();
			xLine += x + ",";
			yLine += y + ",";
		}

		Location depotLocation = ((INode) currentTourState.getVehicle().getVRPSimulationModelStructureElementParameters().getHome())
				.getLocation();
		if (!depotLocation.equals(((INode) currentTourState.getTourActivities().get(currentTourState.getTourActivities().size() - 1)
				.getElement().getVRPSimulationModelStructureElementParameters().getHome()).getLocation())) {
			xLine += depotLocation.getX() + ",";
			yLine += depotLocation.getY() + ",";
		}

		xLine += ((INode) newOrder.getElement().getVRPSimulationModelStructureElementParameters().getHome()).getLocation().getX();
		yLine += ((INode) newOrder.getElement().getVRPSimulationModelStructureElementParameters().getHome()).getLocation().getY();

		logger.debug("xline: {}", xLine);
		logger.debug("yline: {}", yLine);

		String[] result = new String[2];
		result[0] = xLine;
		result[1] = yLine;
		return result;
	}

	private List<String> buildScript(String method, String[] data, int indexStart, int indexDepot, int seed) {
		List<String> result = new ArrayList<>();
		result.add("library(tspmeta)");
		result.add("set.seed(" + seed + ")");
		result.add("data.x  <- c(" + data[0] + ")");
		result.add("data.y  <- c(" + data[1] + ")");
		result.add("indexCurrent <- " + indexStart);
		result.add("indexDepot <- " + indexDepot);
		result.add("coords.df <- data.frame(long=data.x, lat=data.y)");
		result.add("coords.mx <- as.matrix(coords.df)");
		result.add("dist.mx <- dist(coords.mx)");
		result.add("mat <- as.matrix(dist.mx)");
		result.add("mat[indexCurrent, indexDepot] <- 0.000001");
		result.add("mat[indexDepot, indexCurrent] <- 0.000001");
		result.add("tsp.ins <- tsp_instance(coords.mx, mat )");
		result.add("tour <- run_solver(tsp.ins, method=\"" + method + "\")");
		return result;
	}

	int connectionCounter = 0;

	private RConnection establishRConnection() {
		RConnection rConnection = null;
		try {
			rConnection = new RConnection(this.host, this.port);
		} catch (Throwable e) {
			e.printStackTrace();
			connectionCounter++;
			if (connectionCounter > 10) {
				logger.error("Can not establish R connection. Connection counter is {}, abort trying. {}", connectionCounter, e.getMessage());
				throw new RuntimeException("Can not establish R connection.");
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				logger.error("Can not establish R connection. Try again, connection counter is {}. {}", connectionCounter, e.getMessage());
				return establishRConnection();
			}
		}
		
		connectionCounter = 0;
		return rConnection;
	}

	private boolean allEmpty(List<TourState> currentTourStates) {
		boolean allEmpty = true;
		for (TourState tourState : currentTourStates) {
			if (!tourState.getTourActivities().isEmpty()) {
				allEmpty = false;
				break;
			}
		}
		return allEmpty;
	}

	public enum TSPMetaSolvingMethod {

		NEAREST_INSERTION("nearest_insertion"), FARTHEST_INSERTION("farthest_insertion"), CHEAPEST_INSERTION(
				"cheapest_insertion"), ARBITRARY_INSERTION("arbitrary_insertion"), NN("nn"), REPETIVE_NN("repetitive_nn"), TWO_OPT("2-opt");

		private String value;

		TSPMetaSolvingMethod(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

}
