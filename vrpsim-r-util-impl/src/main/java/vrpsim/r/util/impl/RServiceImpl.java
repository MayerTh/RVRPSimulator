package vrpsim.r.util.impl;

import java.util.List;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vrpsim.r.util.api.IRServiceAPI;
import vrpsim.r.util.api.util.EvaluationException;

public class RServiceImpl implements IRServiceAPI {

	private static Logger logger = LoggerFactory.getLogger(RServiceImpl.class);

	// private final String standardAdress = "127.0.0.1";
	// private final int standardPort = 6311;

	@Override
	public RConnection establishRConnection() {
		return this.establishRConnection(standardAdress, standardPort);
	}

	@Override
	public RConnection establishRConnection(String adress) {
		return this.establishRConnection(adress, standardPort);
	}

	@Override
	public RConnection establishRConnection(int port) {
		return this.establishRConnection(standardAdress, port);
	}

	int connectionCounter = 0;

	@Override
	public RConnection establishRConnection(String adress, int port) {
		RConnection rConnection = null;
		try {
			rConnection = new RConnection(adress, port);
		} catch (Throwable e) {
			e.printStackTrace();
			connectionCounter++;
			if (connectionCounter > 10) {
				logger.error("Can not establish R connection. Connection counter is {}, abort trying. {}", connectionCounter,
						e.getMessage());
				throw new RuntimeException("Can not establish R connection.");
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				logger.error("Can not establish R connection. Try again, connection counter is {}. {}", connectionCounter, e.getMessage());
				return establishRConnection();
			}
		}
		connectionCounter = 0;
		return rConnection;
	}

	@Override
	public RConnection evaluate(RConnection connection, List<String> toEvaluate) {
		String currentCommand = "";
		for (String cmd : toEvaluate) {
			currentCommand = cmd;
			try {
				this.evaluate(connection, currentCommand);
			} catch (EvaluationException e) {
				logger.error("Can not eval command {} due to {}. Script to evaluate was:", currentCommand, e.getMessage());
				System.out.println("Can not eval command "+currentCommand+" due to " + e.getMessage() + ". Script to evaluate was: ");
				for(String s : toEvaluate) {
					logger.error("\t {}", s);
					System.out.println("\t " + s);
				}
				throw new RuntimeException("Can not eval command:\"" + currentCommand + "\".");
			}
		}
		return connection;
	}

	@Override
	public RConnection evaluate(RConnection connection, String toEvaluate) throws EvaluationException {
		String currentCommand = toEvaluate;
		try {
			connection.eval(currentCommand);
		} catch (RserveException e) {
			e.printStackTrace();
			logger.error("Can not eval command {} due to {}", currentCommand, e.getMessage());
			throw new EvaluationException("Can not eval command:\"" + currentCommand + "\".");
		}
		return connection;
	}

}
