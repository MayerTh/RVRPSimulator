package vrpsim.util.model.instances.generator.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import vrpsim.util.model.instances.generator.bent.BentInstanceLoader;

public class InstanceLoaderUtil {

	/**
	 * Returns a list of internal paths of files representing a VRP instances
	 * by the given folder. Note, plase
	 * 
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public List<String> getAvailablePathsToInstances(String folder) throws IOException {

		if (!folder.startsWith("/")) {
			folder = "/" + folder;
		}

		if (!folder.endsWith("/")) {
			folder = folder + "/";
		}

		List<String> availableInstances = new ArrayList<>();
		final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

		if (jarFile.isFile()) {
			final JarFile jar = new JarFile(jarFile);
			final Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				final String name = entries.nextElement().getName();
				if (name.startsWith(folder) && !name.endsWith("/")) {
					availableInstances.add("/" + name);
				}
			}
			jar.close();
		} else {
			BufferedReader br = new BufferedReader(new InputStreamReader(BentInstanceLoader.class.getResourceAsStream(folder)));
			String name = "";
			while ((name = br.readLine()) != null) {
				availableInstances.add(folder + name);
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
	public File loadInstance(String path) throws IOException, URISyntaxException {

		BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(path)));

		File file = new File("instance");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		String line = "";
		while ((line = br.readLine()) != null) {
			bw.write(line);
			bw.newLine();
		}
		bw.close();

		return file;
	}

}
