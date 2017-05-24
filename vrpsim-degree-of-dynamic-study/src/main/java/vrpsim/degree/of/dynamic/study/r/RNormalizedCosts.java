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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import vrpsim.degree.of.dynamic.study.statistics.RunStatistics;

public class RNormalizedCosts {

	public void exportTo(String folder, HashMap<Double, List<RunStatistics.Point>> normalizedCosts, double dod)
			throws IOException {

		for (Double key : normalizedCosts.keySet()) {

			String fileName = "F=" + key + "_Results_over_all_instances(CostsVsLDOD)";
			BufferedWriter bw = new BufferedWriter(
					new FileWriter(new File(folder + File.separatorChar + fileName + ".r")));

			List<RunStatistics.Point> workWith = normalizedCosts.get(key);
			Collections.sort(workWith);

			bw.write("x <- c(");
			for (int i = 0; i < workWith.size(); i++) {
				bw.write(workWith.get(i).xLDOD + "");
				if (i < workWith.size() - 1) {
					bw.write(",");
				}
			}
			bw.write(")");
			bw.newLine();

			bw.write("y <- c(");
			for (int i = 0; i < workWith.size(); i++) {
				bw.write(workWith.get(i).yCosts + "");
				if (i < workWith.size() - 1) {
					bw.write(",");
				}
			}
			bw.write(")");
			bw.newLine();

			bw.write("plot(x, y, xlim=c(" + RunStatistics.getMinLDOD(workWith) + ","
					+ RunStatistics.getMaxLDOD(workWith) + "), ylim=c(" + RunStatistics.getMinCost(workWith) + ","
					+ RunStatistics.getMaxCost(workWith)
					+ "), pch=20, main=\"Normalized results with trend lines over all instances with dod=" + dod
					+ "and timehorzion=" + key + "\", col=\"black\", xlab=\"LDOD\", ylab=\"Solution Costs\")");

			writeTrendLines(bw);

			bw.close();

		}

	}

	private static void writeTrendLines(BufferedWriter bw) throws IOException {

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
}
