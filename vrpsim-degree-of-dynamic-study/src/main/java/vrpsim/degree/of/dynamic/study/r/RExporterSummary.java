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
package vrpsim.degree.of.dynamic.study.r;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import vrpsim.dynamicvrprep.model.generator.api.IDynamicVRPREPModelGenerationPosibilities;
import vrpsim.dynamicvrprep.model.generator.api.TmpRequest;
import vrpsim.dynamicvrprep.model.generator.api.TmpRequests;

public class RExporterSummary {

	public void exportTo(String folder, List<IDynamicVRPREPModelGenerationPosibilities> statCollectors)
			throws IOException {

//		folder = folder + File.separatorChar + statCollectors.get(0).getRunInfo().getInstanceName();
//		File file = new File(folder);
//		if (!file.exists()) {
//			file.mkdirs();
//		}

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.BASIC_ISO_DATE;
		String sNow = now.format(dtf);
		String fileName = sNow + "_SummuryReport";
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(folder + File.separatorChar + fileName + ".r")));

		Collections.sort(statCollectors, new Comparator<IDynamicVRPREPModelGenerationPosibilities>() {
			@Override
			public int compare(IDynamicVRPREPModelGenerationPosibilities o1,
					IDynamicVRPREPModelGenerationPosibilities o2) {
				return Double.compare(o1.getRunInfo().getDod(), o2.getRunInfo().getDod());
			}
		});

		String yMeanValues = "yMeanValues <- c(";
		String yMinValues = "yMinValues <- c(";
		String yMaxValues = "yMaxValues <- c(";
		String xValues = "xValues <- c(";

		for (int i = 0; i < statCollectors.size(); i++) {

			xValues += statCollectors.get(i).getRunInfo().getDod();
			yMinValues += statCollectors.get(i).getSmallest().calculateLDOD(statCollectors.get(i).getTotalDistance());
			yMaxValues += statCollectors.get(i).getBiggest().calculateLDOD(statCollectors.get(i).getTotalDistance());
			yMeanValues += calculateMeanLDOD(statCollectors.get(i).getCombinations(),
					statCollectors.get(i).getTotalDistance());

			if (i < statCollectors.size() - 1) {
				yMeanValues += ",";
				yMinValues += ",";
				yMaxValues += ",";
				xValues += ",";
			}
		}

		yMeanValues += ")";
		yMinValues += ")";
		yMaxValues += ")";
		xValues += ")";

		bw.write(yMeanValues);
		bw.newLine();
		bw.write(yMinValues);
		bw.newLine();
		bw.write(yMaxValues);
		bw.newLine();
		bw.write(xValues);
		bw.newLine();

		bw.write("plot(xValues, yMeanValues, xlim=c(0.0,1.0), type=\"l\", main=\"LDOD Study for "
				+ statCollectors.get(0).getRunInfo().getInstanceName()
				+ "\", ylim=c(0,1.0), col=\"blue\", xlab=\"DOD\", ylab=\"LDOD\")");
		bw.newLine();
		bw.write("lines(xValues, yMinValues, col=\"green\")");
		bw.newLine();
		bw.write("lines(xValues, yMaxValues, col=\"red\")");
		bw.newLine();
		bw.write("legend(0,1.0,c(\"Max\", \"Mean\", \"Min\"), col=c(\"red\", \"blue\", \"green\"), lty=1)");

		bw.close();

		String fn = sNow + "_Visualization_" + statCollectors.get(0).getRunInfo().getInstanceName() + "";
		plotInstanceMostDynamic(statCollectors, folder, "", fn);

	}

	private double calculateMeanLDOD(List<TmpRequests> requests, double totalDistance) {
		double sum = 0.0;
		for (TmpRequests rs : requests) {
			sum += rs.calculateLDOD(totalDistance);
		}
		return sum / requests.size();
	}

	private void plotInstanceMostDynamic(List<IDynamicVRPREPModelGenerationPosibilities> statCollectors,
			String outputFolder, String name, String filName) throws IOException {

		for (IDynamicVRPREPModelGenerationPosibilities collector : statCollectors) {

			List<TmpRequest> dynamic = collector.getBiggest().getRequests();

			String dynX = "dynX <- c(";
			String dynY = "dynY <- c(";
			String statX = "statX <- c(";
			String statY = "statY <- c(";

			double minX = Double.MAX_VALUE;
			double maxX = Double.MIN_VALUE;
			double minY = Double.MAX_VALUE;
			double maxY = Double.MIN_VALUE;

			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFolder + File.separatorChar + filName
					+ "dod(" + collector.getRunInfo().getDod() + ")" + ".r")));
			for (int i = 0; i < statCollectors.get(0).getAllRequests().size(); i++) {

				TmpRequest request = statCollectors.get(0).getAllRequests().get(i);

				minX = Math.min(request.getX(), minX);
				maxX = Math.max(request.getX(), maxX);
				minY = Math.min(request.getY(), minY);
				maxY = Math.max(request.getY(), maxY);

				if (dynamic.contains(request)) {

					dynX += request.getX();
					dynY += request.getY();

					if (i < statCollectors.get(0).getAllRequests().size() - 1) {
						dynX += ",";
						dynY += ",";
					}

				} else {

					statX += request.getX();
					statY += request.getY();

					if (i < statCollectors.get(0).getAllRequests().size() - 1) {
						statX += ",";
						statY += ",";
					}

				}

			}

			if (dynX.endsWith(",")) {
				dynX = dynX.substring(0, dynX.length() - 2);
				dynY = dynY.substring(0, dynY.length() - 2);
			}

			if (statX.endsWith(",")) {
				statX = statX.substring(0, statX.length() - 2);
				statY = statY.substring(0, statY.length() - 2);
			}

			dynX += ")";
			dynY += ")";
			statX += ")";
			statY += ")";

			bw.write(dynX);
			bw.newLine();
			bw.write(dynY);
			bw.newLine();
			bw.write(statX);
			bw.newLine();
			bw.write(statY);
			bw.newLine();

			bw.write("plot(dynX, dynY, type=\"p\", col=\"red\", ylim=c(" + minY + "," + (maxY + 10) + "), xlim=c("
					+ minX + "," + maxX + "))");
			bw.newLine();
			bw.write("lines(statX, statY, type=\"p\", col=\"green\")");
			bw.newLine();
			bw.write("legend(" + (minX) + "," + (maxY + 10)
					+ ", c(\"Dynamic Requests\", \"Static Requests\"), col=c(\"red\", \"green\"), lty=1)");
			bw.close();

		}

	}

	// private void plotInstance(List<TmpRequest> allRequests, String
	// outputFolder, String name, String filName) {
	// final int WEIGHT_INDEX = 0;
	// VehicleTypeImpl.Builder vehicleTypeBuilder =
	// VehicleTypeImpl.Builder.newInstance("vehicleType")
	// .addCapacityDimension(WEIGHT_INDEX, 2);
	// VehicleType vehicleType = vehicleTypeBuilder.build();
	//
	// Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
	// vehicleBuilder.setStartLocation(Location.newInstance(10, 10));
	// vehicleBuilder.setType(vehicleType);
	// VehicleImpl vehicle = vehicleBuilder.build();
	// VehicleRoutingProblem.Builder vrpBuilder =
	// VehicleRoutingProblem.Builder.newInstance();
	// vrpBuilder.addVehicle(vehicle);
	//
	// for (TmpRequest request : allRequests) {
	// vrpBuilder.addJob(Service.Builder.newInstance(request.getId() + "")
	// .addSizeDimension(WEIGHT_INDEX, new
	// Double(request.getTotalDistanceToOthers()).intValue())
	// .setLocation(Location.newInstance(request.getX(),
	// request.getY())).build());
	// }
	//
	// VehicleRoutingProblem problem = vrpBuilder.build();
	// new Plotter(problem).plot(outputFolder + File.separatorChar + filName +
	// ".png", name);
	// }

}
