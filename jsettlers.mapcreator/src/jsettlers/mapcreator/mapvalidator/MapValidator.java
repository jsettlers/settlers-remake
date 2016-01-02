package jsettlers.mapcreator.mapvalidator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.mapvalidator.result.ValidationList;

/**
 * Validate the map for errors
 * 
 * @author Andreas Butti
 */
public class MapValidator {

	/**
	 * Map to check
	 */
	protected MapData data;

	/**
	 * Listener for validation results
	 */
	private List<ValidationResultListener> listener = new ArrayList<>();

	/**
	 * Broadcast listener
	 */
	private final ValidationResultListener resultListener = new ValidationResultListener() {

		@Override
		public void validationFinished(final ValidationList list) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					for (ValidationResultListener l : listener) {
						l.validationFinished(list);
					}
				}
			});
		}
	};

	/**
	 * Executor service used to check the errors in another thread
	 */
	private ExecutorService threadpool = Executors.newSingleThreadExecutor(new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setName("MapValidator");
			return t;
		}
	});

	/**
	 * Constructor
	 */
	public MapValidator() {
	}

	/**
	 * @param listener
	 *            Listener to get informed about notifications
	 */
	public void addListener(ValidationResultListener listener) {
		this.listener.add(listener);
	}

	/**
	 * @param data
	 *            Map to check
	 */
	public void setData(MapData data) {
		this.data = data;
	}

	/**
	 * Validate again
	 */
	public void reValidate() {
		threadpool.execute(new ValidatorRunnable(resultListener, data));
	}

	/**
	 * Stop validating, shut down threadpool
	 */
	public void dispose() {
		threadpool.shutdownNow();
		try {
			if (!threadpool.awaitTermination(1, TimeUnit.SECONDS)) {
				throw new IllegalStateException("Could not stop DataTester!");
			}
		} catch (InterruptedException e) {
			System.err.println("InterruptedException on stopping DataTester, shoulden't be a problem");
			e.printStackTrace();
		}
	}

}
