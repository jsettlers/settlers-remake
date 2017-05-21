/*******************************************************************************
 * Copyright (c) 2016
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

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class manages a timer that is called every second to update the UI contents.
 * 
 * @author Michael Zangl
 * @param <T>
 *            The data type that backs this UI element. Should be immutable.
 */
public class UIUpdater<T> {
	private static final int UPDATER_INTERVALL = 1000;

	/**
	 * A listener that listens to UI update events.
	 * 
	 * @author Michael Zangl
	 *
	 * @param <T>
	 *            The data type that is updated. Should be immutable.
	 */
	public interface IUpdateReceiver<T> {
		/**
		 * Called when an update to the UI is required.
		 * 
		 * @param data
		 *            The data to update with.
		 */
		void uiUpdate(T data);
	}

	/**
	 * A data provider that provides the data that might have updated.
	 * 
	 * @author Michael Zangl
	 *
	 * @param <T>
	 *            The data type. Should be immutable.
	 */
	public interface IDataProvider<T> {
		/**
		 * Gets the current data state.
		 * 
		 * @return The state.
		 */
		T getCurrentUIData();
	}

	private static Timer timer;

	private TimerTask started;

	private IDataProvider<T> dataProvider;

	private Collection<? extends IUpdateReceiver<T>> receivers;

	private T lastData;

	/**
	 * Creates a new updater.
	 * 
	 * @param dataProvider
	 *            The data provider to check for.
	 * @param receivers
	 *            The receivers to notify on data changes.
	 */
	public UIUpdater(IDataProvider<T> dataProvider, Collection<? extends IUpdateReceiver<T>> receivers) {
		this.dataProvider = dataProvider;
		this.receivers = receivers;
	}

	/**
	 * Starts the updater thread.
	 * 
	 * @param sendNow
	 *            <code>true</code> if we should fire an update now.
	 */
	public synchronized void start(boolean sendNow) {
		if (started != null) {
			throw new IllegalStateException("UI updater already started.");
		}

		if (sendNow) {
			updateState();
		}

		started = new TimerTask() {
			@Override
			public void run() {
				updateState();
			}
		};
		getUITimer().scheduleAtFixedRate(started, 0, UPDATER_INTERVALL);
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

	/**
	 * Sends a state update.
	 */
	protected synchronized void updateState() {
		T data = dataProvider.getCurrentUIData();
		if (data == null) {
			throw new NullPointerException(dataProvider + " returned null.");
		}
		if (lastData == null || !data.equals(lastData)) {
			for (IUpdateReceiver<T> r : receivers) {
				r.uiUpdate(data);
			}
			lastData = data;
		}
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
	 * @param dataProvider
	 *            The data provider to check for.
	 * @param receivers
	 *            The receivers to notify on data changes.
	 * @param <T>
	 *            The data type that backs this UI element. Should be immutable.
	 * @return The updater.
	 */
	public static <T> UIUpdater<T> getUpdater(IDataProvider<T> dataProvider, Collection<? extends IUpdateReceiver<T>> receivers) {
		return new UIUpdater<>(dataProvider, receivers);
	}

	/**
	 * Forces an immediate update.
	 */
	public void forceUpdate() {
		updateState();
	}
}
