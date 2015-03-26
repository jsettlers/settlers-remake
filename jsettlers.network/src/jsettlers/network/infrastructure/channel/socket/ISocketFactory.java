package jsettlers.network.infrastructure.channel.socket;

import java.net.Socket;

import jsettlers.network.NetworkConstants;
import jsettlers.network.infrastructure.channel.socket.delayed.DelayedSocketFactory;
import jsettlers.network.infrastructure.channel.socket.standard.JavaSocketFactory;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface ISocketFactory {
	public static ISocketFactory DEFAULT_FACTORY = NetworkConstants.USE_DELAYED_SOCKETS ? new DelayedSocketFactory() : new JavaSocketFactory();

	ISocket generateSocket(String host, int port) throws SocketConnectException;

	ISocket generateSocket(Socket socket) throws SocketConnectException;
}
