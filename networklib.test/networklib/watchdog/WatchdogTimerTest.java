package networklib.watchdog;

import static org.junit.Assert.assertEquals;

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
		Thread.sleep(110);

		assertEquals(1, observer.callCtr);
	}

	@Test
	public void testCancelBeforeTimeout() throws InterruptedException {
		assertEquals(0, observer.callCtr);

		WatchdogTimer timer = new WatchdogTimer(100, observer);
		timer.start();

		assertEquals(0, observer.callCtr);
		Thread.sleep(60);
		assertEquals(0, observer.callCtr);
		timer.cancel();
		assertEquals(0, observer.callCtr);

		Thread.sleep(50);
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
			Thread.sleep(90);
			assertEquals(0, observer.callCtr);
		}

		Thread.sleep(50);
		assertEquals(1, observer.callCtr);
	}

	@Test
	public void testTimeoutAndReset() throws InterruptedException {
		assertEquals(0, observer.callCtr);

		WatchdogTimer timer = new WatchdogTimer(100, observer);
		timer.start();
		assertEquals(0, observer.callCtr);

		Thread.sleep(110);
		assertEquals(1, observer.callCtr);

		for (int i = 0; i < 3; i++) {
			timer.reset();
			assertEquals(1, observer.callCtr);
			Thread.sleep(90);
			assertEquals(1, observer.callCtr);
		}

		Thread.sleep(50);
		assertEquals(2, observer.callCtr);
	}

	@Test
	public void testInterruptingWatchdog() throws InterruptedException {
		assertEquals(0, observer.callCtr);

		WatchdogTimer timer = new WatchdogTimer(100, observer);
		Thread t = new Thread(timer);
		t.start();

		assertEquals(0, observer.callCtr);
		Thread.sleep(10);

		t.interrupt(); // interrupt and check that the time doesn't change

		Thread.sleep(20);
		assertEquals(0, observer.callCtr);

		Thread.sleep(80);

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
