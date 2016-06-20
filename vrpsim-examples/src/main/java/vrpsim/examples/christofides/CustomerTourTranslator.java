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

import de.terministic.serein.api.Translator;
import de.terministic.serein.core.genome.PermutationGenome;
import vrpsim.examples.support.CustomerTour;

/**
 * @date 25.02.2016
 * @author thomas.mayer@unibw.de
 *
 */
public class CustomerTourTranslator implements Translator<CustomerTour, PermutationGenome<String>>{

	/* (non-Javadoc)
	 * @see de.terministic.serein.api.Translator#translate(de.terministic.serein.api.Genome)
	 */
	public CustomerTour translate(PermutationGenome<String> genome) {
		return new CustomerTour(genome.getGenes());
	}

}
