package vrpsim.instance.dynamicvrpmodel;

import java.math.BigInteger;
import java.util.Map;

import org.vrprep.model.instance.Instance;

public class ExtendedDynamicVRPREPModel {

	private final Instance instance;

	private final Map<BigInteger, AdditionalRequestInformation> requestInformations;

	public ExtendedDynamicVRPREPModel(Instance instance, Map<BigInteger, AdditionalRequestInformation> requestInformations) {
		super();
		this.instance = instance;
		this.requestInformations = requestInformations;
	}

	public Instance getInstance() {
		return instance;
	}

	public Map<BigInteger, AdditionalRequestInformation> getRequestInformations() {
		return requestInformations;
	}

}
