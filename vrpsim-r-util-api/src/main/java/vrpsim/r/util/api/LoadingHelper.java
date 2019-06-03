package vrpsim.r.util.api;

import java.util.Set;

import org.reflections.Reflections;

public class LoadingHelper {

	@SuppressWarnings("unchecked")
	protected static IRExporterAPI loadRExporter(Reflections reflections) throws InstantiationException, IllegalAccessException {
		Set<Class<? extends IRExporterAPI>> allClasses = reflections.getSubTypesOf(IRExporterAPI.class);
		if (allClasses.size() > 0) {
			return ((Class<? extends IRExporterAPI>) allClasses.toArray()[0]).newInstance();
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected static IRServiceAPI loadIRService(Reflections reflections) throws InstantiationException, IllegalAccessException {
		Set<Class<? extends IRServiceAPI>> allClasses = reflections.getSubTypesOf(IRServiceAPI.class);
		if (allClasses.size() > 0) {
			return ((Class<? extends IRServiceAPI>) allClasses.toArray()[0]).newInstance();
		} else {
			return null;
		}
	}

}
