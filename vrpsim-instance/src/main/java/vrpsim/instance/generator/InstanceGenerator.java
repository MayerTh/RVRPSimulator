package vrpsim.instance.generator;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vrprep.model.instance.Instance;
import org.vrprep.model.instance.Instance.Requests;
import org.vrprep.model.instance.Instance.Requests.Request;

import vrpsim.instance.dynamicvrpmodel.AdditionalRequestInformation;
import vrpsim.instance.dynamicvrpmodel.ExtendedDimensionsType;
import vrpsim.instance.dynamicvrpmodel.ExtendedDynamicVRPREPModel;
import vrpsim.instance.generator.config.InstanceGeneratorConfig;
import vrpsim.instance.generator.model.PropertyPeriod;

public class InstanceGenerator implements IInstanceGenerator {

	private static Logger logger = LoggerFactory.getLogger(InstanceGenerator.class);

	public ExtendedDynamicVRPREPModel generateRandomInstance(InstanceGeneratorConfig config) {

		Instance instance = new Instance();
		Requests requests = new Requests();
		instance.setRequests(requests);

		Map<BigInteger, AdditionalRequestInformation> requestInformations = new HashMap<BigInteger, AdditionalRequestInformation>();

		for (int i = 0; i < config.getNumberRequests(); i++) {
			
			Request generatedrequest = getSimpleRequest(config.getIdGenerator().generateId());
			requests.getRequest().add(generatedrequest);

			double pVArrival = config.getRandom().nextDouble();
			logger.debug("Arrival, pVArrival = {}", pVArrival);
			int arrivalTime = getValueFromPeriod(config.getRandom(), config.getPropertyConfig().getArrivalTimeManager().getPeriod(pVArrival));
			double pVDimension = config.getRandom().nextDouble();
			logger.debug("Dimension, pVDimension = {}", pVDimension);
			int dimension = getValueFromPeriod(config.getRandom(), config.getPropertyConfig().getWeightManager().getPeriod(pVDimension));
			double pVWeight = config.getRandom().nextDouble();
			logger.debug("Weight, pVWeight = {}", pVWeight);
			int weight = getValueFromPeriod(config.getRandom(), config.getPropertyConfig().getWeightManager().getPeriod(pVWeight));
			
			logger.debug("Created request with following attributes: arrival time = {}, weight = {}, dimension = {}", arrivalTime, weight, dimension);
			
			AdditionalRequestInformation dri = new AdditionalRequestInformation(arrivalTime, weight, new ExtendedDimensionsType(dimension));
			requestInformations.put(generatedrequest.getId(), dri);
		}
		
		return new ExtendedDynamicVRPREPModel(instance, requestInformations);
	}

	private int getValueFromPeriod(Random random, PropertyPeriod period) {
		int min = period.getPropertyMinValue();
		int max = period.getPropertyMaxValue();
		logger.debug("min = {}, max = {}", min, max);
		return random.nextInt((max - min) + 1) + min;
	}

	private Request getSimpleRequest(BigInteger reqestId) {
		Request result = new Request();
		result.setId(reqestId);
		return result;
	}

}
