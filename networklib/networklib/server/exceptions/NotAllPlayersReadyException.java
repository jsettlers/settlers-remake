package networklib.server.exceptions;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class NotAllPlayersReadyException extends Exception {
	private static final long serialVersionUID = -4442557133757056985L;

	public NotAllPlayersReadyException() {
		super();
	}

	public NotAllPlayersReadyException(String message, Throwable cause, boolean arg2, boolean arg3) {
		super(message, cause, arg2, arg3);
	}

	public NotAllPlayersReadyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotAllPlayersReadyException(String message) {
		super(message);
	}

	public NotAllPlayersReadyException(Throwable cause) {
		super(cause);
	}
}
