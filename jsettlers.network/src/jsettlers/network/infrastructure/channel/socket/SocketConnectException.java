package jsettlers.network.infrastructure.channel.socket;

import java.io.IOException;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class SocketConnectException extends IOException {
	private static final long serialVersionUID = -165355378907879855L;

	public SocketConnectException() {
		super();
	}

	public SocketConnectException(String message, Throwable cause) {
		super(message, cause);
	}

	public SocketConnectException(String message) {
		super(message);
	}

	public SocketConnectException(Throwable cause) {
		super(cause);
	}
}
