/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.graphics.action;

import jsettlers.common.action.IAction;

import java.util.Timer;
import java.util.TimerTask;
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

	/**
	 * The actions that are queued to fire.
	 */
	private final LinkedBlockingQueue<FireringAction> toFire = new LinkedBlockingQueue<>();

	private final Object toFireMutex = new Object();

	/**
	 * The object we should fire the actions to.
	 */
	private final ActionFireable fireTo;

	/**
	 * A listener that listens if this action firerer is slow
	 */
	private ActionThreadBlockingListener blockingListener;

	/**
	 * A flag indicating if we notified the {@link #blockingListener} that we are blocking.
	 */
	private boolean isBlockingSent;

	/**
	 * Mutex for {@link #blockingListener} and {@link #isBlockingSent}
	 */
	private final Object blockingListenerMutex = new Object();

	/**
	 * The timer that watches for logic freezes.
	 */
	private final Timer watchdogTimer = new Timer("action firerer timer");

	/**
	 * The timer task that is currently active for the {@link #watchdogTimer}.
	 */
	private TimerTask watchdogTimerTask;

	/**
	 * If we were stopped yet.
	 */
	private boolean stopped = false;

	/**
	 * Creates a new action firerer and starts it.
	 *
	 * @param fireTo
	 *            The object we should fire to.
	 */
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
			FireringAction action;

			while (!stopped) {
				try {
					synchronized (toFireMutex) {
						while (toFire.isEmpty() && !stopped) {
							toFireMutex.wait();
						}
						if (stopped) {
							break;
						}
						action = toFire.poll();
					}
					startWatchdog(action.startTime);
					fireTo.fireAction(action.action);
					stopWatchdog();

				} catch (Throwable e) {
					System.err.println("Exception while handling action:");
					e.printStackTrace();
					if (blockingListener != null) {
						blockingListener.actionThreadCaughtException(e);
					}
				}
				if (toFire.isEmpty()) {
					disableWatchdog();
				}
			}
		}
	}

	/**
	 * An action in the queue.
	 *
	 * @author michael
	 */
	private static class FireringAction {
		private final long startTime;
		private final IAction action;

		FireringAction(IAction action, long startTime) {
			this.action = action;
			this.startTime = startTime;
		}
	}

	/**
	 * Sets the listener to be notified on blocking state changes.
	 *
	 * @param listener
	 */
	public void setBlockingListener(ActionThreadBlockingListener listener) {
		synchronized (blockingListenerMutex) {
			this.blockingListener = listener;
		}
	}

	public void stopWatchdog() {
		if (watchdogTimerTask != null) {
			watchdogTimerTask.cancel();
		}
	}

	protected void startWatchdog(long startTime) {
		long destTime = startTime + ACTION_FIRERER_TIMEOUT;

		long timeUntilFreezeState = System.currentTimeMillis() - destTime;
		if (timeUntilFreezeState <= 0) {
			sendIsBlocking(true);
		} else {
			watchdogTimerTask = new TimerTask() {
				@Override
				public void run() {
					sendIsBlocking(true);
				}
			};
			watchdogTimer.schedule(watchdogTimerTask, timeUntilFreezeState);
		}
	}

	protected void disableWatchdog() {
		sendIsBlocking(false);
	}

	private void sendIsBlocking(boolean blocking) {
		synchronized (blockingListenerMutex) {
			if (isBlockingSent != blocking && blockingListener != null) {
				blockingListener.actionThreadSlow(blocking);
			}
			isBlockingSent = blocking;
		}
	}

	/**
	 * Schedules an action to be fired.
	 */
	@Override
	public void fireAction(IAction action) {
		synchronized (toFireMutex) {
			toFire.offer(new FireringAction(action, System.currentTimeMillis()));
			toFireMutex.notifyAll();
		}
	}

	/**
	 * Stops this action firerer. The queue is not emptied by this operation.
	 */
	public void stop() {
		synchronized (toFireMutex) {
			stopped = true;
			toFireMutex.notifyAll();
		}
		watchdogTimer.cancel();
	}
}
