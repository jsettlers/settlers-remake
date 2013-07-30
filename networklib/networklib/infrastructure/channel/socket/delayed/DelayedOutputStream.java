package networklib.infrastructure.channel.socket.delayed;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class DelayedOutputStream extends OutputStream {

	private static final int BUFFER_LENGTH = 2000;

	private final Thread thread;
	private final ByteArrayOutputStream[] buffers = new ByteArrayOutputStream[BUFFER_LENGTH];
	private final OutputStream out;

	private boolean closed;

	private int currWriteSlot = 200;
	private int currReadSlot = 0;

	public DelayedOutputStream(OutputStream out) {
		this.out = out;
		for (int i = 0; i < buffers.length; i++) {
			buffers[i] = new ByteArrayOutputStream();
		}

		thread = new DelayedOutStreamWriter();
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void close() throws IOException {
		closed = true;
		thread.interrupt();
		out.close();
	}

	@Override
	public void write(int b) throws IOException {
		buffers[currWriteSlot].write(b);
	}

	private final class DelayedOutStreamWriter extends Thread {
		private static final int MIN_DELAY = 50;
		private static final int MAX_DELAY = 500;
		private static final int MAX_DELAY_STEP = 200;

		private DelayedOutStreamWriter() {
			super("delayedOutStreamWriter");
		}

		@Override
		public void run() {
			while (!closed) {

				currWriteSlot = (currWriteSlot + 1) % BUFFER_LENGTH;

				int currDist = (BUFFER_LENGTH + currWriteSlot - currReadSlot) % BUFFER_LENGTH;
				int targetDistance = Math.min(MAX_DELAY,
						Math.max(MIN_DELAY, 50 + (int) (currDist + Math.random() * MAX_DELAY_STEP * 2 - MAX_DELAY_STEP)));

				// System.out.println("ssdsf");
				if (targetDistance >= currDist) {
					currReadSlot = (BUFFER_LENGTH + currReadSlot - (targetDistance - currDist)) % BUFFER_LENGTH;
				} else {
					for (int i = targetDistance; i < currDist; i++) {
						writeNextSlot();
					}
				}

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
			}
		}

		private void writeNextSlot() {
			ByteArrayOutputStream currBuffer = buffers[currReadSlot];
			currReadSlot = (currReadSlot + 1) % BUFFER_LENGTH;
			try {
				currBuffer.writeTo(DelayedOutputStream.this.out);
				currBuffer.reset();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
