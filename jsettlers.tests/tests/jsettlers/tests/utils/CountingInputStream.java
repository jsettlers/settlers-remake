package jsettlers.tests.utils;

import java.io.IOException;
import java.io.InputStream;

public class CountingInputStream extends InputStream {

	private final InputStream in;

	private int byteCounter = 0;

	public CountingInputStream(InputStream in) {
		super();
		this.in = in;
	}

	@Override
	public int read() throws IOException {
		byteCounter++;
		return in.read();
	}

	@Override
	public void close() throws IOException {
		super.close();
		in.close();
	}

	public int getByteCounter() {
		return byteCounter;
	}
}
