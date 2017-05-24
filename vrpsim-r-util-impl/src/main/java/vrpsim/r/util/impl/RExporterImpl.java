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

import vrpsim.r.util.api.IRExporterAPI;
import vrpsim.r.util.api.model.RConfig;
import vrpsim.r.util.api.model.RModel;
import vrpsim.r.util.api.model.RPoint;

public class RExporterImpl implements IRExporterAPI {

	@Override
	public void export(String exportFolder, RConfig config, RModel model) throws IOException {

		if (model.isPointsAreConnected()) {
			exportWay(exportFolder, config, model);
		} else {
			exportOnlyPointsToR(exportFolder, config, model);
		}

	}

	private void exportWay(String exportFolder, RConfig config, RModel model) throws IOException {

		// MinMax minMax = new MinMax(model);
		// double minX = minMax.minX;
		// double maxX = minMax.maxX;
		// double minY = minMax.minY;
		// double maxY = minMax.maxY;

		double minX = 0;
		double maxX = model.getMax().getX();
		double minY = 0;
		double maxY = model.getMax().getY();

		String exportTo = exportFolder + "/" + config.getrFileName() + ".r";
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(exportTo)));

		boolean startToEnd = model.getListOfPoints().isEmpty();

		// DRAW START
		String fLineX = "";
		String fLineY = "";
		if (!startToEnd) {
			if(model.getStart().equals(model.getListOfPoints().get(0))) {
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
		writer.write("plot(fLineX, fLineY, type=\"b\", col=\"blue\", ylim=c(" + minY + "," + (maxY + 10) + "), xlim=c(" + minX + "," + maxX + "), main=\""
				+ config.getDiagramMainTitel() + "\")");
		writer.newLine();

		for (int i = 0; i < model.getListOfPoints().size() - 1; i++) {

			String color = "col=\"green\")";
			if (model.getListOfPoints().get(i).isHighlight()) {
				color = "col=\"brown\")";
			}

			String lineX = "lineX" + i + " <- c(" + model.getListOfPoints().get(i).getX() + ", " + model.getListOfPoints().get(i + 1).getX() + ")";
			String lineY = "lineY" + i + " <- c(" + model.getListOfPoints().get(i).getY() + ", " + model.getListOfPoints().get(i + 1).getY() + ")";

			writer.write(lineX);
			writer.newLine();
			writer.write(lineY);
			writer.newLine();

			writer.write("lines(lineX" + i + ", lineY" + i + ", type=\"b\", " + color + "");
			writer.newLine();
		}

		// DRAW END
		if (!startToEnd ){ // && !model.getStart().equals(model.getEnd())) {
			String lLineX = "lLineX <- c(" + model.getListOfPoints().get(model.getListOfPoints().size() - 1).getX() + ", " + model.getEnd().getX() + ")";
			String lLineY = "lLineY <- c(" + model.getListOfPoints().get(model.getListOfPoints().size() - 1).getY() + ", " + model.getEnd().getY() + ")";

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

		writer.flush();
		writer.close();

	}

	private void exportOnlyPointsToR(String exportFolder, RConfig config, RModel model) throws IOException {

		String exportTo = exportFolder + "/" + config.getrFileName() + ".r";
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(exportTo)));

		// MinMax minMax = new MinMax(model);
		// double minX = minMax.minX;
		// double maxX = minMax.maxX;
		// double minY = minMax.minY;
		// double maxY = minMax.maxY;

		double minX = 0;
		double maxX = model.getMax().getX();
		double minY = 0;
		double maxY = model.getMax().getY();

		String dynX = "dynX <- c(";
		String dynY = "dynY <- c(";
		String statX = "statX <- c(" + model.getStart().getX() + ", ";
		String statY = "statY <- c(" + model.getStart().getY() + ", ";

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
				statX += point.getX();
				statY += point.getY();
				if (i < model.getListOfPoints().size() - 1) {
					statX += ",";
					statY += ",";
				}
			}
		}
		
		if(dynX.endsWith(",")) {
			dynX = dynX.substring(0, dynX.length()-2);
		}
		if(dynY.endsWith(",")) {
			dynY = dynY.substring(0, dynY.length()-2);
		}
		if(statX.endsWith(",")) {
			statX = statX.substring(0, statX.length()-2);
		}
		if(statY.endsWith(",")) {
			statY = statY.substring(0, statY.length()-2);
		}

		dynX += ")";
		dynY += ")";
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

		writer.write("plot(statX, statY, type=\"p\", col=\"green\", ylim=c(" + minY + "," + (maxY + 10) + "), xlim=c(" + minX + "," + maxX + "), main=\""
				+ config.getDiagramMainTitel() + "\")");

		writer.newLine();
		writer.write("lines(dynX, dynY, type=\"p\", col=\"red\")");
		writer.newLine();
		writer.write("legend(" + (minX) + "," + (maxY + 10) + ", c(\"Dynamic Requests\", \"Static Requests\"), col=c(\"red\", \"green\"), lty=1)");

		writer.flush();
		writer.close();
	}

}
