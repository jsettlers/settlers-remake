package jsettlers.mapcreator.mapvalidator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import jsettlers.mapcreator.data.MapData;

/**
 * Validate the map for errors
 * 
 * @author Andreas Butti
 */
public class MapValidator {

	/**
	 * Listener for validation result
	 */
	private final ValidationResultListener resultListener;

	/**
	 * Map to check
	 */
	protected MapData data;

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
	 * 
	 * @param resultListener
	 *            Listener for validation result
	 */
	public MapValidator(ValidationResultListener resultListener) {
		this.resultListener = resultListener;
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
