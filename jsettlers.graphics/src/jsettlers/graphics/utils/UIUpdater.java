package jsettlers.graphics.utils;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class manages a timer that is called every second to update the UI contents.
 * 
 * @author Michael Zangl
 */
public class UIUpdater<T> {
	public interface IUpdateReceiver<T> {
		/**
		 * Called when an update to the UI is required.
		 * 
		 * @param data
		 *            The data to update with.
		 */
		public void uiUpdate(T data);
	}

	public interface IDataProvider<T> {
		public T getCurrentUIData();
	}

	private static Timer timer;

	private TimerTask started;

	private IDataProvider<T> dataProvider;

	private Collection<? extends IUpdateReceiver<T>> receivers;

	private T lastData;

	public UIUpdater(IDataProvider<T> dataProvider, Collection<? extends IUpdateReceiver<T>> receivers) {
		this.dataProvider = dataProvider;
		this.receivers = receivers;
	}

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
		getUITimer().scheduleAtFixedRate(started, 0, 1000);
	}

	public synchronized void stop() {
		if (started == null) {
			throw new IllegalStateException("UI updater already stopped.");
		}

		started.cancel();
		started = null;
	}

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

	public static <T> UIUpdater<T> getUpdater(IDataProvider<T> dataProvider, Collection<? extends IUpdateReceiver<T>> receivers) {
		UIUpdater<T> uiUpdater = new UIUpdater<T>(dataProvider, receivers);
		return uiUpdater;
	}

	/**
	 * Forces an immediate update.
	 */
	public void forceUpdate() {
		updateState();
	}
}
