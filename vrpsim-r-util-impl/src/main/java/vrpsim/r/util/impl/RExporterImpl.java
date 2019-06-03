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
package vrpsim.r.util.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vrpsim.r.util.api.IRExporterAPI;
import vrpsim.r.util.api.model.RConfig;
import vrpsim.r.util.api.model.RModel;
import vrpsim.r.util.api.model.RModelAdvanced;
import vrpsim.r.util.api.model.RPoint;
import vrpsim.r.util.api.model.RRaster;
import vrpsim.r.util.api.model.RRect;

public class RExporterImpl implements IRExporterAPI {

	private final String pchDepot = "pch=12";
	private final String pchStatic = "pch=19";
	private final String pchDynamic = "pch=6";

	private final int ipchDepot = 12;
	private final int ipchStatic = 19;
	private final int ipchDynamic = 6;
	
	@Override
	public void export(String exportFolder, RConfig config, RModel model) throws IOException {

		new File(exportFolder).mkdirs();

		String exportTo = exportFolder + "/" + config.getrFileName() + ".r";
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(exportTo)));

		if (model.isPointsAreConnected()) {
			exportWay(exportFolder, config, model, writer);
		} else {
			exportOnlyPointsToR(exportFolder, config, model, writer);
		}

		writer.flush();
		writer.close();

	}

	private void exportWay(String exportFolder, RConfig config, RModel model, BufferedWriter writer) throws IOException {

		// MinMax minMax = new MinMax(model);
		// double minX = minMax.minX;
		// double maxX = minMax.maxX;
		// double minY = minMax.minY;
		// double maxY = minMax.maxY;

		double minX = model.getMin().getX();
		double maxX = model.getMax().getX();
		double minY = model.getMin().getY();
		double maxY = model.getMax().getY();

		boolean startToEnd = model.getListOfPoints().isEmpty() || model.getListOfPoints().size() < 2;

		// DRAW START
		String fLineX = "";
		String fLineY = "";
		if (!startToEnd) {
			if (model.getStart().equals(model.getListOfPoints().get(0))) {
				fLineX = "fLineX <- c(" + model.getListOfPoints().get(0).getX() + ", " + model.getListOfPoints().get(1).getX() + ")";
				fLineY = "fLineY <- c(" + model.getListOfPoints().get(0).getY() + ", " + model.getListOfPoints().get(1).getY() + ")";
			} else {
				fLineX = "fLineX <- c(" + model.getStart().getX() + ", " + model.getListOfPoints().get(0).getX() + ")";
				fLineY = "fLineY <- c(" + model.getStart().getY() + ", " + model.getListOfPoints().get(0).getY() + ")";
			}
		} else {
			fLineX = "fLineX <- c(" + model.getStart().getX() + ", " + model.getEnd().getX() + ") #startToEnd";
			fLineY = "fLineY <- c(" + model.getStart().getY() + ", " + model.getEnd().getY() + ") #startToEnd";
		}

		writer.write(fLineX);
		writer.newLine();
		writer.write(fLineY);
		writer.newLine();
		writer.write("plot(fLineX, fLineY, type=\"b\", col=\"blue\", ylim=c(" + minY + "," + (maxY + 10) + "), xlim=c(" + minX + "," + maxX
				+ "), main=\"" + config.getDiagramMainTitel() + "\")");
		writer.newLine();

		for (int i = 0; i < model.getListOfPoints().size() - 1; i++) {

			String color = "col=\"green\")";
			if (model.getListOfPoints().get(i).isHighlight()) {
				color = "col=\"brown\")";
			}

			String lineX = "lineX" + i + " <- c(" + model.getListOfPoints().get(i).getX() + ", " + model.getListOfPoints().get(i + 1).getX()
					+ ")";
			String lineY = "lineY" + i + " <- c(" + model.getListOfPoints().get(i).getY() + ", " + model.getListOfPoints().get(i + 1).getY()
					+ ")";

			writer.write(lineX);
			writer.newLine();
			writer.write(lineY);
			writer.newLine();

			writer.write("lines(lineX" + i + ", lineY" + i + ", type=\"b\", " + color + "");
			writer.newLine();
		}

		// DRAW END
		if (!startToEnd) { // && !model.getStart().equals(model.getEnd())) {
			String lLineX = "lLineX <- c(" + model.getListOfPoints().get(model.getListOfPoints().size() - 1).getX() + ", "
					+ model.getEnd().getX() + ")";
			String lLineY = "lLineY <- c(" + model.getListOfPoints().get(model.getListOfPoints().size() - 1).getY() + ", "
					+ model.getEnd().getY() + ")";

			writer.write(lLineX);
			writer.newLine();
			writer.write(lLineY);
			writer.newLine();
			writer.write("lines(lLineX, lLineY, type=\"b\", col=\"yellow\")");
			writer.newLine();

			if (!startToEnd) {
				if (Double.compare(model.getStart().getX(), model.getListOfPoints().get(0).getX()) == 0
						&& Double.compare(model.getStart().getY(), model.getListOfPoints().get(0).getY()) == 0) {
					fLineX = "fLineX <- c(" + model.getListOfPoints().get(0).getX() + ", " + model.getListOfPoints().get(1).getX() + ")";
					fLineY = "fLineY <- c(" + model.getListOfPoints().get(0).getY() + ", " + model.getListOfPoints().get(1).getY() + ")";
				} else {
					fLineX = "fLineX <- c(" + model.getStart().getX() + ", " + model.getListOfPoints().get(0).getX() + ")";
					fLineY = "fLineY <- c(" + model.getStart().getY() + ", " + model.getListOfPoints().get(0).getY() + ")";
				}
			} else {
				fLineX = "fLineX <- c(" + model.getStart().getX() + ", " + model.getEnd().getX() + ")";
				fLineY = "fLineY <- c(" + model.getStart().getY() + ", " + model.getEnd().getY() + ")";
			}

			writer.write("lines(fLineX, fLineY, type=\"b\", col=\"blue\")");
		}

	}

	private void exportOnlyPointsToR(String exportFolder, RConfig config, RModel model, BufferedWriter writer) throws IOException {

		// MinMax minMax = new MinMax(model);
		// double minX = minMax.minX;
		// double maxX = minMax.maxX;
		// double minY = minMax.minY;
		// double maxY = minMax.maxY;

		double minX = model.getMin().getX();
		double maxX = model.getMax().getX();
		double minY = model.getMin().getY();
		double maxY = model.getMax().getY();

		String dynX = "dynX <- c(";
		String dynY = "dynY <- c(";
		String statX = "statX <- c(" + model.getStart().getX() + ", ";
		String statY = "statY <- c(" + model.getStart().getY() + ", ";
		String depotX = "depotX <- c(";
		String depotY = "depotY <- c(";

		List<RPoint> depotPoints = new ArrayList<>();
		for (int i = 0; i < model.getListOfPoints().size(); i++) {
			RPoint point = model.getListOfPoints().get(i);

			if (point.isHighlight()) {
				dynX += point.getX();
				dynY += point.getY();
				if (i < model.getListOfPoints().size() - 1) {
					dynX += ",";
					dynY += ",";
				}
			} else {
				if (point.isDepot()) {
					depotPoints.add(point);
					depotX += point.getX();
					depotY += point.getY();
					if (i < model.getListOfPoints().size() - 1) {
						depotX += ",";
						depotY += ",";
					}
				} else {
					statX += point.getX();
					statY += point.getY();
					if (i < model.getListOfPoints().size() - 1) {
						statX += ",";
						statY += ",";
					}
				}
			}
		}

		if (dynX.endsWith(",")) {
			dynX = dynX.substring(0, dynX.length() - 2);
		}
		if (dynY.endsWith(",")) {
			dynY = dynY.substring(0, dynY.length() - 2);
		}
		if (statX.endsWith(",")) {
			statX = statX.substring(0, statX.length() - 2);
		}
		if (statY.endsWith(",")) {
			statY = statY.substring(0, statY.length() - 2);
		}
		if (depotX.endsWith(",")) {
			depotX = depotX.substring(0, depotX.length() - 2);
		}
		if (depotY.endsWith(",")) {
			depotY = depotY.substring(0, depotY.length() - 2);
		}

		dynX += ")";
		dynY += ")";
		depotX += ")";
		depotY += ")";
		statX += "," + model.getEnd().getX() + ")";
		statY += "," + model.getEnd().getY() + ")";

		writer.write(dynX);
		writer.newLine();
		writer.write(dynY);
		writer.newLine();
		writer.write(statX);
		writer.newLine();
		writer.write(statY);
		writer.newLine();
		writer.write(depotX);
		writer.newLine();
		writer.write(depotY);
		writer.newLine();

		writer.write("plot(statX, statY, type=\"p\", "+pchStatic+", ylim=c(" + minY + "," + (maxY) + "), xlim=c(" + minX + "," + maxX
				+ "), main=\"\", xlab=\"" + config.getDiagramTitelX() + "\", ylab=\""
				+ config.getDiagramTitelY() + "\", cex.lab=1.5, cex.axis=1.5, cex.main=1.5, cex.sub=1.5, xaxt=\"n\", yaxt=\"n\")");
		writer.newLine();
		writer.write("title(main=\""+ config.getDiagramMainTitel() +"\", font.main=1, cex.main=2.2)");
		writer.newLine();
		writer.write("lines(dynX, dynY, type=\"p\", "+pchDynamic+")");
		writer.newLine();
		writer.write("lines(depotX, depotY, type=\"p\", "+pchDepot+")");

		for (RPoint depotPoint : depotPoints) {
			writer.newLine();
			writer.write("text(" + (depotPoint.getX() + 1) + ", " + (depotPoint.getY() + 1) + ", label=\"Depot\", cex=1.5)");
		}

		writer.newLine();
		// writer.write("legend(" + (minX) + "," + (maxY + 10)
		// + ", c(\"Dynamic Requests\", \"Static Requests\", \"Depots\"), col=c(\"red\",
		// \"green\", \"blue\"), pch=1)");

		if (config.isDrawLegend()) {
			writer.write("legend(" + (minX) + "," + (maxY + 10)
					+ ", c(\"Static Requests\", \"Dynamic Request\"), pch=c("+ipchStatic+","+ipchDynamic+"), lty=c(-1,-1), cex=1.5)");
		}

	}

	@Override
	public void export(String exportFolder, RConfig config, RModelAdvanced model) throws IOException {

		new File(exportFolder).mkdirs();
		String exportTo = exportFolder + "/" + config.getrFileName() + ".r";
//		String exportTo = config.getrFileName() + ".r";
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(exportTo)));
		write(writer, config, model);
		writer.flush();
		writer.close();

	}

	private void write(BufferedWriter writer, RConfig config, RModelAdvanced model) throws IOException {

		writer.write("main_titel <- enc2utf8(\"" + config.getDiagramMainTitel() + "\")");
		writer.newLine();
		writer.write("x_titel <- enc2utf8(\"" + config.getDiagramTitelX() + "\")");
		writer.newLine();
		writer.write("y_titel <- enc2utf8(\"" + config.getDiagramTitelY() + "\")");
		writer.newLine();

		RPoint depot = getDepot(model);
		writer.write("xd <- c(" + depot.getX() + ")");
		writer.newLine();
		writer.write("yd <- c(" + depot.getY() + ")");
		writer.newLine();

		String xlim = "xlim=c(" + model.getMinX() + "," + model.getMaxX() + ")";
		String ylim = "ylim=c(" + model.getMinY() + "," + model.getMaxY() + ")";

		writer.write("plot(xd, yd, type=\"p\", " + pchDepot + ", " + xlim + ", " + ylim
				+ ", main=\"\", xlab=x_titel, ylab=y_titel, cex.lab=1.5, cex.axis=1.5, cex.main=1.5, cex.sub=1.5, xaxt=\"n\", yaxt=\"n\")");
		writer.newLine();
		writer.write("title(main=main_titel, font.main=1, cex.main=2.2)");
		writer.newLine();

		int startIndex = 0;
		if (model.getConnectedPoints() != null) {
			for (List<RPoint> points : model.getConnectedPoints()) {
				startIndex = writePoints(writer, points, startIndex, true, depot);
			}
		}

		if (model.getUnconnectedPoints() != null) {
			writePoints(writer, model.getUnconnectedPoints(), startIndex, false, null);
		}

		if (model.getRaster() != null) {
			writeRaster(writer, model.getRaster());
		}

	}

	private int writePoints(BufferedWriter writer, List<RPoint> points, int startIndex, boolean connect, RPoint depot) throws IOException {

		int startPointCount = startIndex + 1;
		int pointCount = startPointCount;
		for (RPoint point : points) {
			String lineX = "xn" + pointCount + " <- c(" + point.getX() + ")";
			String lineY = "yn" + pointCount + " <- c(" + point.getY() + ")";
			String lineLines = "lines(xn" + pointCount + ", yn" + pointCount + ", type=\"p\", "
					+ (point.isHighlight() ? this.pchDynamic : this.pchStatic) + ")";

			writer.write(lineX);
			writer.newLine();
			writer.write(lineY);
			writer.newLine();
			writer.write(lineLines);
			writer.newLine();

			pointCount++;
		}

		if (connect) {
			String x = "xr1 <- c(" + depot.getX() + ", ";
			String y = "yr1 <- c(" + depot.getY() + ", ";
			for (int i = startPointCount; i < pointCount; i++) {
				x += "xn" + i + ", ";
				y += "yn" + i + ", ";
			}
			x += depot.getX() + ")";
			y += depot.getY() + ")";

			String route = "lines(xr1, yr1, type=\"l\", lty=2, pch=16)";

			writer.write(x);
			writer.newLine();
			writer.write(y);
			writer.newLine();
			writer.write(route);
			writer.newLine();
		}

		return pointCount;

	}

	private void writeRaster(BufferedWriter writer, RRaster raster) throws IOException {
		for (RRect rect : raster.getRects()) {
			String str = "rect(" + rect.getButtonLeft().getX() + ", " + rect.getButtonLeft().getY() + ", " + rect.getTopRight().getX()
					+ ", " + rect.getTopRight().getY() + ", border = \"red\")";
			writer.write(str);
			writer.newLine();
		}
	}

	private RPoint getDepot(RModelAdvanced model) {
		RPoint depot = new RPoint(0, 0, false, true);
		for (RPoint point : getAllPoints(model)) {
			if (point.isDepot()) {
				depot = point;
				break;
			}
		}
		return depot;
	}

	private List<RPoint> getAllPoints(RModelAdvanced model) {
		List<RPoint> allpoints = new ArrayList<>();
		if (model.getUnconnectedPoints() != null) {
			allpoints.addAll(model.getUnconnectedPoints());
		}

		if (model.getConnectedPoints() != null) {
			for (List<RPoint> points : model.getConnectedPoints()) {
				allpoints.addAll(points);
			}
		}
		return allpoints;
	}

}
