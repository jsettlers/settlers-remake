package jsettlers.network.infrastructure.channel.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface ISocket {

	OutputStream getOutputStream() throws IOException;

	InputStream getInputStream() throws IOException;

	boolean isClosed();

	void close() throws IOException;

	@Override
	String toString();
}
