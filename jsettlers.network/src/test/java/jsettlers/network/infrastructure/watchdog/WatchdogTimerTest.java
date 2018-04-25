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

import static org.junit.Assert.assertEquals;
import jsettlers.network.infrastructure.watchdog.IWatchdogObserver;
import jsettlers.network.infrastructure.watchdog.WatchdogTimer;

import org.junit.Test;

/**
 * Tests the class {@link WatchdogTimer}.
 * 
 * @author Andreas Eberle
 * 
 */
public class WatchdogTimerTest {

	WatchdogTestObserver observer = new WatchdogTestObserver();

	@Test(expected = IllegalArgumentException.class)
	public void testIncorrectConstructorArgument1() {
		new WatchdogTimer(0, observer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncorrectConstructorArgument2() {
		new WatchdogTimer(-1, observer);
	}

	@Test
	public void testTimeout() throws InterruptedException {
		assertEquals(0, observer.callCtr);

		WatchdogTimer timer = new WatchdogTimer(100, observer);
		timer.start();

		assertEquals(0, observer.callCtr);
		Thread.sleep(110L);

		assertEquals(1, observer.callCtr);
	}

	@Test
	public void testCancelBeforeTimeout() throws InterruptedException {
		assertEquals(0, observer.callCtr);

		WatchdogTimer timer = new WatchdogTimer(100, observer);
		timer.start();

		assertEquals(0, observer.callCtr);
		Thread.sleep(60L);
		assertEquals(0, observer.callCtr);
		timer.cancel();
		assertEquals(0, observer.callCtr);

		Thread.sleep(50L);
		assertEquals(0, observer.callCtr);
	}

	@Test
	public void testMultiResetAndTimeout() throws InterruptedException {
		assertEquals(0, observer.callCtr);

		WatchdogTimer timer = new WatchdogTimer(100, observer);
		timer.start();
		assertEquals(0, observer.callCtr);

		for (int i = 0; i < 5; i++) {
			timer.reset();
			assertEquals(0, observer.callCtr);
			Thread.sleep(90L);
			assertEquals(0, observer.callCtr);
		}

		Thread.sleep(50L);
		assertEquals(1, observer.callCtr);
	}

	@Test
	public void testTimeoutAndReset() throws InterruptedException {
		assertEquals(0, observer.callCtr);

		WatchdogTimer timer = new WatchdogTimer(100, observer);
		timer.start();
		assertEquals(0, observer.callCtr);

		Thread.sleep(110L);
		assertEquals(1, observer.callCtr);

		for (int i = 0; i < 3; i++) {
			timer.reset();
			assertEquals(1, observer.callCtr);
			Thread.sleep(90L);
			assertEquals(1, observer.callCtr);
		}

		Thread.sleep(50L);
		assertEquals(2, observer.callCtr);
	}

	@Test
	public void testInterruptingWatchdog() throws InterruptedException {
		assertEquals(0, observer.callCtr);

		WatchdogTimer timer = new WatchdogTimer(100, observer);
		Thread t = new Thread(timer);
		t.start();

		assertEquals(0, observer.callCtr);
		Thread.sleep(10L);

		t.interrupt(); // interrupt and check that the time doesn't change

		Thread.sleep(20L);
		assertEquals(0, observer.callCtr);

		Thread.sleep(80L);

		assertEquals(1, observer.callCtr);
	}

	private static class WatchdogTestObserver implements IWatchdogObserver {
		int callCtr = 0;

		@Override
		public void timeoutOccured(WatchdogTimer watchdogTimer) {
			callCtr++;
		}
	}
}
