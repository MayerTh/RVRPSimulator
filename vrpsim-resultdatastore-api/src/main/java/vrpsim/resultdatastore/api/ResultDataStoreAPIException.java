package vrpsim.resultdatastore.api;

public class ResultDataStoreAPIException extends Exception {

	private static final long serialVersionUID = -4670531714645121277L;

	public ResultDataStoreAPIException() {
		super();
	}

	public ResultDataStoreAPIException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public ResultDataStoreAPIException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ResultDataStoreAPIException(String arg0) {
		super(arg0);
	}

	public ResultDataStoreAPIException(Throwable arg0) {
		super(arg0);
	}

}
