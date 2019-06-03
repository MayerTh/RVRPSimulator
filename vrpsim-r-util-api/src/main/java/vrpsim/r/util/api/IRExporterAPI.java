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
package vrpsim.r.util.api;

import java.io.IOException;

import org.reflections.Reflections;

import vrpsim.r.util.api.model.RConfig;
import vrpsim.r.util.api.model.RModel;
import vrpsim.r.util.api.model.RModelAdvanced;

public interface IRExporterAPI {

	public void export(String exportFolder, RConfig config, RModel model) throws IOException;
	
	public void export(String exportFolder, RConfig config, RModelAdvanced model) throws IOException;

	public static IRExporterAPI load() throws InstantiationException, IllegalAccessException {
		Reflections reflections = new Reflections();
		return LoadingHelper.loadRExporter(reflections);
	}

	public static IRExporterAPI load(String package_) throws InstantiationException, IllegalAccessException {
		Reflections reflections = new Reflections(package_);
		return LoadingHelper.loadRExporter(reflections);
	}

}
