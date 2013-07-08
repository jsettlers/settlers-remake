package networklib.infrastructure.channel.socket.delayed;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import networklib.infrastructure.channel.socket.ISocket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class DelayedSocket implements ISocket {

	private final Socket socket;
	private final DelayedOutputStream delayedOut;

	public DelayedSocket(Socket socket) throws IOException {
		this.socket = socket;
		this.delayedOut = new DelayedOutputStream(socket.getOutputStream());
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return delayedOut;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	@Override
	public boolean isClosed() {
		return socket.isClosed();
	}

	@Override
	public void close() throws IOException {
		delayedOut.close();
		socket.close();
	}

	@Override
	public String toString() {
		return socket.toString();
	}
}
