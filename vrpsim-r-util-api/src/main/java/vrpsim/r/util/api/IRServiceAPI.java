package vrpsim.r.util.api;

import java.util.List;

import org.reflections.Reflections;
import org.rosuda.REngine.Rserve.RConnection;

import vrpsim.r.util.api.util.EvaluationException;

public interface IRServiceAPI {

	public final String standardAdress = "127.0.0.1";
	public final int standardPort = 6311;

	public RConnection establishRConnection();

	public RConnection establishRConnection(String adress);

	public RConnection establishRConnection(int port);

	public RConnection establishRConnection(String adress, int port);

	public RConnection evaluate(RConnection connection, List<String> toEvaluate);

	public RConnection evaluate(RConnection connection, String toEvaluate) throws EvaluationException;

	public static IRServiceAPI load() throws InstantiationException, IllegalAccessException {
		Reflections reflections = new Reflections();
		return LoadingHelper.loadIRService(reflections);
	}

	public static IRServiceAPI load(String package_) throws InstantiationException, IllegalAccessException {
		Reflections reflections = new Reflections(package_);
		return LoadingHelper.loadIRService(reflections);
	}

}
