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
package vrpsim.vrprep.util.impl.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VRPREPInstanceProviderUtil {

	private static Logger logger = LoggerFactory.getLogger(VRPREPInstanceProviderUtil.class);

	/**
	 * Returns a list of internal paths of files representing a VRP instances by
	 * the given folder. Note, plase
	 * 
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public List<Path> getAvailablePathsToInstances(Path path) throws IOException {

		List<Path> availableInstances = new ArrayList<>();
		final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

		if (jarFile.isFile()) {

			final JarFile jar = new JarFile(jarFile);
			final Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				Path pathInFile = Paths.get(entries.nextElement().getName());
				if (pathInFile.startsWith(path) && pathInFile.getNameCount() - 1 == path.getNameCount()) {
					availableInstances
							.add(Paths.get(path.toString(), pathInFile.getName(path.getNameCount()).toString()));
				}
			}
			jar.close();
		} else {

			InputStream is = VRPREPInstanceProviderUtil.class.getResourceAsStream("/" + path.toString());
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String name = "";
			while ((name = br.readLine()) != null) {
				availableInstances.add(Paths.get(path.toString(), name));
			}

		}

		return availableInstances;
	}

	/**
	 * Return the given instance as file.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public File loadInstance(Path path) throws IOException, URISyntaxException {

		String pathStr = "/";
		for (int i = 0; i < path.getNameCount(); i++) {
			pathStr += path.getName(i);
			if (i < path.getNameCount() - 1) {
				pathStr += "/";
			}
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(pathStr)));

		File file = new File("tmp_instance");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		String line = "";
		while ((line = br.readLine()) != null) {
			bw.write(line);
			bw.newLine();
		}
		bw.close();

		logger.debug("File {} loaded from given path={}, transformed path={}", file.toString(), path.toString(), pathStr);
		
		return file;

	}

}
