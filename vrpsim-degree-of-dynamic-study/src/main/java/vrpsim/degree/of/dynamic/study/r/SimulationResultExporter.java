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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.behaviour.tour.ITour;
import vrpsim.r.util.api.IRExporterAPI;
import vrpsim.r.util.api.model.RConfig;
import vrpsim.r.util.api.util.CannotCreateRModelException;
import vrpsim.r.util.api.util.RModelTransformator;
import vrpsim.r.util.impl.RExporterImpl;

public class SimulationResultExporter {

	private static Logger logger = LoggerFactory.getLogger(SimulationResultExporter.class);

	public void exportSimulationResults(VRPSimulationModel model, String outputFolder) throws IOException {

		int iTour = 1;
		for (ITour tour : model.getBehaviourService().getBehaviour().getTours()) {
			IRExporterAPI exporter = new RExporterImpl();
			try {
				exporter.export(outputFolder, new RConfig("Result_for_tour_" + iTour + "_with_costs_" + tour.getCurrentTourCosts(),
						"Result for tour " + iTour + " with costs " + tour.getCurrentTourCosts(), "", ""), RModelTransformator.transformTourContextTo(tour.getTourContext(), tour.getStartActivity()));
			} catch (IOException | CannotCreateRModelException e) {
				logger.error("Can not create R model. {}", e.getMessage());
			}
			iTour++;
		}

	}

}
