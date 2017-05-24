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
import java.util.List;

import vrpsim.dynamicvrprep.model.generator.api.IDynamicVRPREPModelGenerationPosibilities;
import vrpsim.dynamicvrprep.model.generator.api.IDynamicVRPREPModelGenerationPosibilities.RunInfo;
import vrpsim.dynamicvrprep.model.generator.api.TmpRequests;

public class RExporter {

	public void exportTo(String folder, IDynamicVRPREPModelGenerationPosibilities statCollector, IDynamicVRPREPModelGenerationPosibilities.RunInfo runInfo) throws IOException {

		statCollector.setRunInfo(runInfo);
		RunInfo info = statCollector.getRunInfo();

//		folder = folder + File.separatorChar + info.getInstanceName();
//		File file = new File(folder);
//		if (!file.exists()) {
//			file.mkdirs();
//		}

		double meanLDOD = calculateMeanLDOD(statCollector.getCombinations(), statCollector.getTotalDistance());

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.BASIC_ISO_DATE;
		String sNow = now.format(dtf);
		String fileName = sNow + "_Run_" + info.getInstanceName() + "_dod(" + info.getDod() + ")_mean(ldod)=" + meanLDOD;
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(folder + File.separatorChar + fileName + ".r")));

		bw.write("x <- c(");
		for (int i = 0; i < statCollector.getCombinations().size(); i++) {
			bw.write(statCollector.getCombinations().get(i).calculateLDOD(statCollector.getTotalDistance()) + "");
			if (i < statCollector.getCombinations().size() - 1) {
				bw.write(",");
			}
		}
		bw.write(")");
		bw.newLine();
		bw.write("hist(x, main=\"LDOD Histogram, based on " + info.getNumberInstances() + " instances (DOD=" + info.getDod()
				+ ")\", xlab=\"Location based DOD\", las=1)");
		bw.newLine();
		//		bw.write("plot(density(x),main=\"Density estimate of data\")");
		//		bw.newLine();
		bw.write("abline(v = " + statCollector.getSmallest().calculateLDOD(statCollector.getTotalDistance()) + ")");
		bw.newLine();
		bw.write("abline(v = " + statCollector.getBiggest().calculateLDOD(statCollector.getTotalDistance()) + ")");
		bw.close();

	}

	

	private double calculateMeanLDOD(List<TmpRequests> requests, double totalDistance) {
		double sum = 0.0;
		for (TmpRequests rs : requests) {
			sum += rs.calculateLDOD(totalDistance);
		}
		return sum / requests.size();
	}
}
