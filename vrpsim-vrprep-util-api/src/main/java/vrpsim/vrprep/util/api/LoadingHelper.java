package vrpsim.vrprep.util.api;

import java.util.Set;

import org.reflections.Reflections;

public class LoadingHelper {

	@SuppressWarnings("unchecked")
	protected static IVRPREPInstanceProviderAPI load(Reflections reflections) throws InstantiationException, IllegalAccessException {
		Set<Class<? extends IVRPREPInstanceProviderAPI>> allClasses = reflections.getSubTypesOf(IVRPREPInstanceProviderAPI.class);
		if (allClasses.size() > 0) {
			return ((Class<? extends IVRPREPInstanceProviderAPI>) allClasses.toArray()[0]).newInstance();
		} else {
			return null;
		}
	}

}
