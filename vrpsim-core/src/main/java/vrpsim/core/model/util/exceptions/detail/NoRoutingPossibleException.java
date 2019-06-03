package vrpsim.core.model.util.exceptions.detail;

import vrpsim.core.model.util.exceptions.NetworkException;

public class NoRoutingPossibleException extends NetworkException {

	private static final long serialVersionUID = 9034049514607815037L;

	public NoRoutingPossibleException() {
		super();
	}

	public NoRoutingPossibleException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public NoRoutingPossibleException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public NoRoutingPossibleException(String arg0) {
		super(arg0);
	}

	public NoRoutingPossibleException(Throwable arg0) {
		super(arg0);
	}

}
