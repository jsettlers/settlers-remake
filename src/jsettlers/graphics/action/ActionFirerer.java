package jsettlers.graphics.action;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class lets you schedule the firing of actions in a separate thread.
 * 
 * @author michael
 */
public class ActionFirerer implements ActionFireable {
	/**
	 * How long the action thread may lack behind.
	 */
	private static final long ACTION_FIRERER_TIMEOUT = 1000;

	private final Thread thread;

	private BlockingQueue<Action> toFire = new LinkedBlockingQueue<Action>();
	private Queue<Long> fireStartTime = new ConcurrentLinkedQueue<Long>();

	private final ActionFireable fireTo;

	private ActionThreadBlockingListener listener;

	private boolean isBlockingSent;

	private final Timer watchdogTimer = new Timer();

	private TimerTask executingTimerTask;

	public ActionFirerer(ActionFireable fireTo) {
		this.fireTo = fireTo;
		this.thread = new ActionFirererThread();
		this.thread.setDaemon(true);
		this.thread.start();
	}

	private class ActionFirererThread extends Thread {
		public ActionFirererThread() {
			super("action firerer");
		}

		@Override
		public void run() {
			while (true) {
				Action action;
				try {
					action = toFire.take();
					long startTime = fireStartTime.poll();
					startWatchdog(startTime);
					fireTo.fireAction(action);
					stopWatchdog();

				} catch (Throwable e) {
					System.err.println("Exception while habdling action:");
					e.printStackTrace();
					if (listener != null) {
						listener.actionThreadCoughtException(e);
					}
				}
				if (toFire.isEmpty()) {
					disableWatchdog();
				}
			}
		}

	}

	public void setBlockingListener(ActionThreadBlockingListener listener) {
		this.listener = listener;
	}

	public void stopWatchdog() {
		if (executingTimerTask != null) {
			executingTimerTask.cancel();
		}
	}

	protected void startWatchdog(long startTime) {
		long destTime = startTime + ACTION_FIRERER_TIMEOUT;

		long timeUntilFreezeState = System.currentTimeMillis() - destTime;
		if (timeUntilFreezeState <= 0) {
			sendIsBlocking(true);
		} else {
			executingTimerTask = new TimerTask() {
				@Override
				public void run() {
					sendIsBlocking(true);
				}
			};
			watchdogTimer.schedule(executingTimerTask, timeUntilFreezeState);
		}
	}

	protected void disableWatchdog() {
		sendIsBlocking(false);
	}

	private void sendIsBlocking(boolean blocking) {
		if (isBlockingSent != blocking && listener != null) {
			listener.actionThreadSlow(blocking);
		}
		isBlockingSent = blocking;
	}

	@Override
	public void fireAction(Action action) {
		toFire.offer(action);
		fireStartTime.add(System.currentTimeMillis());
	}
}
