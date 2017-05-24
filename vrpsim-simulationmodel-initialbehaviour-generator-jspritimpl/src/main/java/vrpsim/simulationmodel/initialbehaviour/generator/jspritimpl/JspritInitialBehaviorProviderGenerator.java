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
package vrpsim.simulationmodel.initialbehaviour.generator.jspritimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;

import vrpsim.core.model.VRPSimulationModel;
import vrpsim.core.model.solution.IInitialBehaviourProvider;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.simulationmodel.initialbehaviour.generator.api.IInitialBehaviourProviderGenerator;
import vrpsim.simulationmodel.initialbehaviour.generator.jspritimpl.util.JspritSimpleSolutionTranslater;
import vrpsim.simulationmodel.initialbehaviour.generator.jspritimpl.util.JspritSolver;

public class JspritInitialBehaviorProviderGenerator implements IInitialBehaviourProviderGenerator {

	private static Logger logger = LoggerFactory.getLogger(JspritInitialBehaviorProviderGenerator.class);
	private boolean createStatistics = true;

	@Override
	public IInitialBehaviourProvider generateInitialBehaviourProvider(String instanceName, VRPSimulationModel model, String statisticsOutpurFolder) {

		JspritSolver solver = new JspritSolver();
		try {

			VehicleRoutingProblemSolution bestSolution = solver.solve(model.getStructureService(), instanceName, statisticsOutpurFolder, this.createStatistics);
			return new JspritSimpleSolutionTranslater().translateSolution(bestSolution, model.getStructureService(), model.getNetworkService());

		} catch (VRPArithmeticException e) {
			e.printStackTrace();
			logger.error("Not able to solve the given Structure with Jsprit. Check implementation. VRPArithmeticException message: {}.", e.getLocalizedMessage());
			return null;
		}

	}

	@Override
	public void toggleCreateStatistics() {
		this.createStatistics = !this.createStatistics;
	}

}
