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
package jsettlers.network.infrastructure.watchdog;

/**
 * 
 */
public class WatchdogTimer implements Runnable {
	private final int timeout;
	private final IWatchdogObserver observer;
	private long until;

	private boolean canceled = false;

	public WatchdogTimer(final int timeout, IWatchdogObserver observer) {
		this.observer = observer;
		if (timeout < 1) {
			throw new IllegalArgumentException("timeout must not be less than 1.");
		}
		this.timeout = timeout;
	}

	public synchronized void start() {
		canceled = false;
		Thread t = new Thread(this, "WATCHDOG");
		t.setDaemon(true);
		t.start();
	}

	public synchronized void cancel() {
		canceled = true;
		notifyAll();
	}

	public synchronized void reset() {
		until = System.currentTimeMillis() + timeout;
		notifyAll();
	}

	@Override
	public synchronized void run() {
		until = System.currentTimeMillis() + timeout;

		while (!canceled) {
			try {
				long delta = until - System.currentTimeMillis();
				if (delta > 0) {
					wait(delta);
				} else {
					wait();
				}
			} catch (InterruptedException e) {
			}

			if (!canceled && until <= System.currentTimeMillis()) {
				observer.timeoutOccured(this);
			}
		}
	}
}
