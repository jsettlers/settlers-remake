package networklib.infrastructure.channel.socket;

import java.net.Socket;

import networklib.infrastructure.channel.socket.delayed.DelayedSocketFactory;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface ISocketFactory {
	// public static ISocketFactory DEFAULT_FACTORY = new JavaSocketFactory();

	public static ISocketFactory DEFAULT_FACTORY = new DelayedSocketFactory();

	ISocket generateSocket(String host, int port) throws SocketConnectException;

	ISocket generateSocket(Socket socket) throws SocketConnectException;

}
