package jsettlers.tests.autoreplay;

import java.io.IOException;
import java.io.OutputStream;

public class DebugOutputStream extends OutputStream {

	private final OutputStream outputStream;
	private int byteCounter = 0;

	public DebugOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public void write(int b) throws IOException {
		byteCounter++;
		if (byteCounter >= 4233202 && byteCounter <= 4233212) {
			// System.out.println();
		}
		outputStream.write(b);
	}

	@Override
	public void close() throws IOException {
		outputStream.close();
	}

	@Override
	public void flush() throws IOException {
		outputStream.flush();
	}
}
