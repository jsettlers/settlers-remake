/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.network.infrastructure.channel.socket.delayed;

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
		private static final int MIN_DELAY = 80;
		private static final int MAX_DELAY = 120;
		private static final int MAX_DELAY_STEP = 10;

		private DelayedOutStreamWriter() {
			super("delayedOutStreamWriter");
		}

		@Override
		public void run() {
			while (!closed) {

				currWriteSlot = (currWriteSlot + 1) % BUFFER_LENGTH;

				int currDist = (BUFFER_LENGTH + currWriteSlot - currReadSlot) % BUFFER_LENGTH;
				int targetDistance = Math.min(MAX_DELAY,
						Math.max(MIN_DELAY, (int) (currDist + Math.random() * MAX_DELAY_STEP * 2 - MAX_DELAY_STEP)));

				// System.out.println("ssdsf");
				if (targetDistance >= currDist) {
					currReadSlot = (BUFFER_LENGTH + currReadSlot - (targetDistance - currDist)) % BUFFER_LENGTH;
				} else {
					for (int i = targetDistance; i < currDist; i++) {
						writeNextSlot();
					}
				}

				try {
					Thread.sleep(1L);
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
