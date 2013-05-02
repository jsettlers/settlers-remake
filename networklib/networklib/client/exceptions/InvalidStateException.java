package networklib.client.exceptions;

/**
 * This {@link Exception} might be thrown when a request to the server can not be done in the current state.
 * 
 * @author Andreas Eberle
 * 
 */
public class InvalidStateException extends Exception {
	private static final long serialVersionUID = -7056034042985463389L;

	public InvalidStateException() {
		super();
	}

	public InvalidStateException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidStateException(String arg0) {
		super(arg0);
	}

	public InvalidStateException(Throwable arg0) {
		super(arg0);
	}

}
