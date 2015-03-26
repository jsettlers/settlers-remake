package jsettlers.network.infrastructure.channel.socket.standard;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import jsettlers.network.infrastructure.channel.socket.ISocket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class JavaSocketAdapter implements ISocket {

	private Socket socket;

	public JavaSocketAdapter(Socket socket) {
		this.socket = socket;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
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
		socket.close();
	}

	@Override
	public String toString() {
		return socket.toString();
	}
}
