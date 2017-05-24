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
package vrpsim.degree.of.dynamic.study.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vrprep.model.instance.Instance;

import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.degree.of.dynamic.study.DynamicStudyMain;
import vrpsim.dynamicvrprep.model.api.DynamicVRPREPModel;
import vrpsim.dynamicvrprep.model.impl.degree.DOD;
import vrpsim.dynamicvrprep.model.impl.degree.EDOD;
import vrpsim.dynamicvrprep.model.impl.degree.LDOD;
import vrpsim.r.util.api.IRExporterAPI;
import vrpsim.r.util.api.model.RConfig;
import vrpsim.r.util.api.util.CannotCreateRModelException;
import vrpsim.r.util.api.util.RModelTransformator;
import vrpsim.r.util.impl.RExporterImpl;

public class RunStatistics {

	private static Logger logger = LoggerFactory.getLogger(RunStatistics.class);

	private final String statisticsRootFolder;
	private final String instanceName;
	private final double planningHorizon;
	private final Instance globalStaticInstance;
	private final VehicleRoutingProblemSolution globalStaticInstanceSolution;

	private final HashMap<Long, DynamicVRPREPModel> dynamicModels = new HashMap<>();
	private final HashMap<VRPSimulationModel, Long> simulationModels = new HashMap<>();
	private final HashMap<VRPSimulationModel, Double> costForStaticPartOfVRPSimulationModel = new HashMap<>();

	public HashMap<VRPSimulationModel, Double> getCostForStaticPartOfVRPSimulationModel() {
		return costForStaticPartOfVRPSimulationModel;
	}

	public RunStatistics(String instanceName, double planningHorizon, Instance globalStaticInstance, VehicleRoutingProblemSolution globalStaticInstanceSolution, String statisticsRootFolder) {
		this.instanceName = instanceName;
		this.planningHorizon = planningHorizon;
		this.globalStaticInstance = globalStaticInstance;
		this.globalStaticInstanceSolution = globalStaticInstanceSolution;
		this.statisticsRootFolder = statisticsRootFolder;
	}

	public HashMap<Long, DynamicVRPREPModel> getDynamicModels() {
		return this.dynamicModels;
	}

	public void addDynamicModels(DynamicVRPREPModel dynamicModel) {
		this.dynamicModels.put(dynamicModel.getId(), dynamicModel);
	}

	public double getLDOD(DynamicVRPREPModel dynamicModel) {
		return new LDOD().calculateDegree(dynamicModel);
	}

	public double getDOD(DynamicVRPREPModel dynamicModel) {
		return new DOD().calculateDegree(dynamicModel);
	}

	public double getEDOD(DynamicVRPREPModel dynamicModel) {
		return new EDOD().calculateDegree(dynamicModel);
	}

	public void addSimulationModel(DynamicVRPREPModel dynamicModelToSimulate, VRPSimulationModel simulationModel) {
		this.simulationModels.put(simulationModel, dynamicModelToSimulate.getId());
	}

	public void setCostSimulatedVRPSimulationModel(VRPSimulationModel simulationModel, double cost) {
		this.costForStaticPartOfVRPSimulationModel.put(simulationModel, cost);
	}

	public void exportToR() throws IOException {

		String currentFolder = this.statisticsRootFolder;
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.BASIC_ISO_DATE;
		String sNow = now.format(dtf);
		String fileName = instanceName + "(F="+planningHorizon+")_" + sNow + "overview";
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(currentFolder + File.separatorChar + fileName + ".r")));

		double dod = 0.0;
		double edod = 0.0;

		List<Point> points = new ArrayList<>();
		for (VRPSimulationModel simulationModel : this.simulationModels.keySet()) {
			DynamicVRPREPModel dynamicModel = this.dynamicModels.get(this.simulationModels.get(simulationModel));
			dod = this.getDOD(dynamicModel);
			edod = this.getEDOD(dynamicModel);
			double ldod = this.getLDOD(dynamicModel);
			double cost = this.costForStaticPartOfVRPSimulationModel.get(simulationModel);
			points.add(new Point(ldod, cost));

			int iTour = 1;
			for (ITour tour : simulationModel.getBehaviourService().getBehaviour().getTours()) {
				IRExporterAPI exporter = new RExporterImpl();
				try {
					exporter.export(currentFolder,
							new RConfig(
									"Result_for_tour_" + iTour + "_ldod_" + DynamicStudyMain.round(ldod, 2) + "_with_costs_"
											+ +DynamicStudyMain.round(tour.getCurrentTourCosts(), 2),
									"Result for tour " + iTour + " ldod " + ldod + " with costs " + tour.getCurrentTourCosts(), "", ""),
							RModelTransformator.transformTourContextTo(tour.getTourContext(), tour.getStartActivity()));
				} catch (IOException | CannotCreateRModelException e) {
					logger.error("Can not create R model. {}", e.getMessage());
				}
				iTour++;
			}

		}
		Collections.sort(points);

		bw.write("x <- c(");
		for (int i = 0; i < points.size(); i++) {
			bw.write(points.get(i).xLDOD + "");
			if (i < points.size() - 1) {
				bw.write(",");
			}
		}
		bw.write(")");
		bw.newLine();

		bw.write("y <- c(");
		for (int i = 0; i < points.size(); i++) {
			bw.write(points.get(i).yCosts + "");
			if (i < points.size() - 1) {
				bw.write(",");
			}
		}
		bw.write(")");
		bw.newLine();

		bw.write("plot(x, y, xlim=c(" + getMinLDOD(points) + "," + getMaxLDOD(points) + "), ylim=c(" + getMinCost(points) + "," + getMaxCost(points)
				+ "),  pch=20, main=\"Dynamic Study for " + instanceName + " dod=" + dod + " edod=" + edod + " with trend lines\", col=\"black\", xlab=\"LDOD\", ylab=\"Solution Costs\")");

		writeTrendLines(bw);
		bw.close();

	}
	
	private void writeTrendLines(BufferedWriter bw) throws IOException {

		bw.newLine();
		bw.write("# basic straight line of fit");
		bw.newLine();
		bw.write("fit <- glm(y~x)");
		bw.newLine();
		bw.write("co <- coef(fit)");
		bw.newLine();
		bw.write("abline(fit, col=\"green\", lwd=2)");
		bw.newLine();

		bw.write("# legend");
		bw.newLine();
		bw.write("legend(\"topleft\", legend=c(\"linear\"), col=c(\"green\"), lwd=2)");
		bw.newLine();

		bw.write("# correlation");
		bw.write("print(cor.test(x, y, method=\"spearman\"))");
	}

	public List<Point> getNormalizedCostsXandLDODY() {
		List<Point> result = new ArrayList<>();
		List<Point> points = new ArrayList<>();
		for (VRPSimulationModel simulationModel : this.simulationModels.keySet()) {
			DynamicVRPREPModel dynamicModel = this.dynamicModels.get(this.simulationModels.get(simulationModel));
			double ldod = this.getLDOD(dynamicModel);
			double cost = simulationModel.getBehaviourService().getTourCosts();
//			double cost = this.costForStaticPartOfVRPSimulationModel.get(simulationModel);
			points.add(new Point(ldod, cost));
		}
		
		double minCost = getMinCost(points);
		double maxCost = getMaxCost(points);
		
		for(Point p : points) {
			double normalizedCosts = ((p.yCosts - minCost) / ((maxCost-minCost) / 100)) / 100;
			result.add(new Point(p.xLDOD, normalizedCosts));
		}
		
		return result;
	}
	
	public static double getMaxCost(List<Point> points) {
		double max = Double.MIN_VALUE;
		for (Point p : points) {
			if (p.yCosts > max) {
				max = p.yCosts;
			}
		}
		return max;
	}

	public static double getMinCost(List<Point> points) {
		double min = Double.MAX_VALUE;
		for (Point p : points) {
			if (p.yCosts < min) {
				min = p.yCosts;
			}
		}
		return min;
	}

	public static double getMaxLDOD(List<Point> points) {
		double max = Double.MIN_VALUE;
		for (Point p : points) {
			if (p.xLDOD > max) {
				max = p.xLDOD;
			}
		}
		return max;
	}

	public static double getMinLDOD(List<Point> points) {
		double min = Double.MAX_VALUE;
		for (Point p : points) {
			if (p.xLDOD < min) {
				min = p.xLDOD;
			}
		}
		return min;
	}

	public static class Point implements Comparable<Point> {
		public double xLDOD;
		public double yCosts;

		public Point(double x, double y) {
			super();
			this.xLDOD = x;
			this.yCosts = y;
		}

		@Override
		public int compareTo(Point o) {
			return Double.compare(xLDOD, o.xLDOD);
		}
	}

}
