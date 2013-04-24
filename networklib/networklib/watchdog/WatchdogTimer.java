package networklib.watchdog;

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
