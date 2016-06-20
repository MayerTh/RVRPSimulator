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
package vrpsim.examples.christofides;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import vrpsim.examples.support.CustomerTour;

public class ConvertBestSolutionsToCustomerTour {

	private static String workWith = "CMT12";
	
	public static void main(String[] args) throws IOException, JAXBException {
		BufferedReader br = new BufferedReader(new FileReader(new File("Christofides1979\\best_solutions\\"+workWith+".xml.txt")));
		
		String line = br.readLine();
		List<String> customerIds = new ArrayList<>();
		while(line != null) {
			if(line.contains("CUSTOMER")) {
				/*example line: (Exchange 15 from VEHICLE-0 to CUSTOMER-5 at NODE-6)*/
				customerIds.add(line.split("to ")[1].split(" at")[0]);
			}
			line = br.readLine();
		}
		br.close();
		
		CustomerTour ct = new CustomerTour(customerIds);
		JAXBContext context = JAXBContext.newInstance(CustomerTour.class);
	    Marshaller m = context.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    m.marshal(ct, new File("Christofides1979\\best_solutions\\"+workWith+"_solution.xml"));
	    System.out.println("Done.");
		
	}

}
