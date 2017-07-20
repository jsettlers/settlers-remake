/*******************************************************************************
 * Copyright (c) 2016 - 2017
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
package jsettlers.graphics.utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class manages a timer that is called every second to update the UI contents.
 *
 * @author Michael Zangl
 */
public class UIUpdater {
	private static final int UPDATER_INTERVAL = 1000;

	/**
	 * A listener that listens to UI update events.
	 *
	 * @author Michael Zangl
	 */
	public interface IUpdateReceiver {
		/**
		 * Called when an update to the UI is required.
		 */
		void updateUi();
	}

	private static Timer timer;

	private final IUpdateReceiver receiver;
	private TimerTask started;

	/**
	 * Creates a new updater.
	 *
	 * @param receiver
	 * 		The receiver to regularly notify to update the ui
	 */
	private UIUpdater(IUpdateReceiver receiver) {
		this.receiver = receiver;
	}

	/**
	 * Starts the updater thread.
	 *
	 * @param sendNow
	 * 		<code>true</code> if we should fire an update now.
	 */
	public synchronized void start(boolean sendNow) {
		if (started != null) {
			throw new IllegalStateException("UI updater already started.");
		}

		if (sendNow) {
			updateUi();
		}

		started = new TimerTask() {
			@Override
			public void run() {
				updateUi();
			}
		};
		getUITimer().scheduleAtFixedRate(started, UPDATER_INTERVAL, UPDATER_INTERVAL);
	}

	/**
	 * Stops the {@link UIUpdater}.
	 */
	public synchronized void stop() {
		if (started == null) {
			throw new IllegalStateException("UI updater already stopped.");
		}

		started.cancel();
		started = null;
	}

	private static synchronized Timer getUITimer() {
		if (timer == null) {
			timer = new Timer();
		}
		return timer;
	}

	/**
	 * Creates a new updater.
	 *
	 * @param receiver
	 * 		The regularly call to update the ui
	 * @return The updater.
	 */
	public static UIUpdater getUpdater(IUpdateReceiver receiver) {
		return new UIUpdater(receiver);
	}

	/**
	 * Forces an immediate update.
	 */
	public void forceUpdate() {
		updateUi();
	}

	private void updateUi() {
		receiver.updateUi();
	}
}
