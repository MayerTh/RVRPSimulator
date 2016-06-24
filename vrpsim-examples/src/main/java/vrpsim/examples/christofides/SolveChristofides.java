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
/**
 * 
 */
package vrpsim.examples.christofides;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import de.terministic.serein.api.EvolutionEnvironment;
import de.terministic.serein.api.Mutation;
import de.terministic.serein.api.Population;
import de.terministic.serein.api.TerminationCondition;
import de.terministic.serein.core.AlgorithmFactory;
import de.terministic.serein.core.BasicIndividual;
import de.terministic.serein.core.Populations;
import de.terministic.serein.core.StatsListener;
import de.terministic.serein.core.genome.PermutationGenome;
import de.terministic.serein.core.genome.mutation.CombinationMutation;
import de.terministic.serein.core.genome.mutation.NeighborSwapMutation;
import de.terministic.serein.core.genome.mutation.RotateMutation;
import de.terministic.serein.core.genome.mutation.ShiftMutation;
import de.terministic.serein.core.genome.mutation.SwapMutation;
import de.terministic.serein.core.termination.TerminationConditionGenerations;
import vrpsim.core.model.util.exceptions.BehaviourException;
import vrpsim.core.model.util.exceptions.NetworkException;
import vrpsim.core.model.util.exceptions.StorageException;
import vrpsim.core.model.util.exceptions.VRPArithmeticException;
import vrpsim.examples.support.CustomerTour;

/**
 * @date 24.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class SolveChristofides {

	private static Logger logger = LoggerFactory.getLogger(SolveChristofides.class);

	public static void main(String[] args) throws JAXBException, VRPArithmeticException, StorageException,
			NetworkException, BehaviourException, IOException, URISyntaxException {

		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		StatusPrinter.print(lc);

		int popSize = 30;
		int genSize = 100;
		String inputFolderName = "Christofides1979";

//		try {
//			popSize = Integer.parseInt(args[0]); // 100
//			genSize = Integer.parseInt(args[1]); // 1000
//		} catch (IndexOutOfBoundsException e) {
//			System.out.println(
//					"Inizilize with 'java -jar *.jar 'population Size' 'maximum generations''. Consider: popSize = Integer.parseInt(args[0]) and genSize = Integer.parseInt(args[1]).");
//			throw new RuntimeException();
//		}

		List<String> files = getFiles();
		logger.info("Following instances will be solved: " + files);
//		new File("Christofides1979-Solutions\\").mkdirs();
//		logger.info("Solution directory created and set to Christofides1979-Solutions\\");

		for (String file : getFiles()) {

			String file_name = file;
			logger.info("Start with instance: " + file_name);

			// Metaheuristic Parameters
			Random random = new Random(4321);
			int populationSize = popSize;

			List<Mutation<PermutationGenome<String>>> mutationOperators = new ArrayList<>();
			mutationOperators.add(new RotateMutation<PermutationGenome<String>>());
			mutationOperators.add(new SwapMutation<PermutationGenome<String>>());
			mutationOperators.add(new ShiftMutation<PermutationGenome<String>>());
			mutationOperators.add(new NeighborSwapMutation<PermutationGenome<String>>());
			CombinationMutation<PermutationGenome<String>> mutations = new CombinationMutation<>(mutationOperators);
			CustomerTourFitness fitness = new CustomerTourFitness(inputFolderName, file_name);
			TerminationCondition<CustomerTour> termination = new TerminationConditionGenerations<>(genSize);

			// Initial individual
			PermutationGenome<String> g = new PermutationGenome<String>(fitness.getInitialTour().getCustomerIds());
			BasicIndividual<CustomerTour, PermutationGenome<String>> initialIndividual = new BasicIndividual<CustomerTour, PermutationGenome<String>>(
					g, new CustomerTourTranslator());
			initialIndividual.setMutation(mutations);

			// startpopulation
			Population<CustomerTour> startPop = Populations.generatePopulation(initialIndividual, populationSize,
					random);

			// assembling the metaheuristic
			AlgorithmFactory<CustomerTour> factory = new AlgorithmFactory<CustomerTour>();
			factory.termination = termination;
			EvolutionEnvironment<CustomerTour> algo = factory.createReferenceEvolutionaryAlgorithm(fitness, startPop,
					random);

			StatsListener<CustomerTour> listener = new StatsListener<CustomerTour>(fitness, 10);
			algo.addListener(listener);

			// run optimization
			algo.evolve();

			// result
			CustomerTour fittest = algo.getFittest().getPhenotype();
			JAXBContext context = JAXBContext.newInstance(CustomerTour.class);
		    Marshaller m = context.createMarshaller();
		    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		    m.marshal(fittest, new File(inputFolderName + "\\"  + file_name + "_tour.xml"));
		}

	}

	private static List<String> getFiles() {
		List<String> files = new ArrayList<>();
		files.add("CMT01.xml");
		files.add("CMT02.xml");
		files.add("CMT03.xml");
		files.add("CMT04.xml");
		files.add("CMT05.xml");
		files.add("CMT06.xml");
		files.add("CMT07.xml");
		files.add("CMT08.xml");
		files.add("CMT09.xml");
		files.add("CMT10.xml");
		files.add("CMT11.xml");
		files.add("CMT12.xml");
		files.add("CMT13.xml");
		files.add("CMT14.xml");
		return files;
	}

}
