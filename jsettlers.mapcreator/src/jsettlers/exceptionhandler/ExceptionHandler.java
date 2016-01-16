package jsettlers.exceptionhandler;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;

/**
 * Handle unhandled exception and display a user popup
 * 
 * @author Andreas Butti
 *
 */
public class ExceptionHandler {

	/**
	 * Count errors, reset on error dialog close, to prevent loops, e.g. on out of memory
	 */
	private static final AtomicInteger errorCount = new AtomicInteger(0);

	/**
	 * Constructor
	 */
	public ExceptionHandler() {
	}

	/**
	 * Set the error counter back to 0
	 */
	public static void resetErrorCounter() {
		errorCount.set(0);
	}

	/**
	 * Set Up an exception handler for uncatcht exception, call this method once from main
	 */
	public static void setupDefaultExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(final Thread t, final Throwable e) {
				ExceptionHandler.displayError(e, "Unhandled error in Thread " + t.getName());
			}
		});
	}

	/**
	 * Display an error message if an unexpected exception occurs
	 * 
	 * @param e
	 *            Exception
	 * @param description
	 *            Description to display
	 */
	public static void displayError(final Throwable e, final String description) {
		displayError(e, description, Thread.currentThread());
	}

	/**
	 * Display an error message if an unexpected exception occurs
	 * 
	 * @param e
	 *            Exception
	 * @param description
	 *            Description to display
	 * @param t
	 *            Thread
	 */
	public static void displayError(final Throwable e, final String description, final Thread t) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					displayError(e, description, t);
				}
			});
			return;
		}

		int count = errorCount.incrementAndGet();
		if (count > 5) {
			// Probably an error loop, quit application
			// TODO: Set a flag and ask user to send logfile on next startup
			System.exit(-1);
		}

		System.err.println(description);
		e.printStackTrace();

		ExceptionDialog dlg = new ExceptionDialog(e, description, t);
		dlg.setVisible(true);
	}

}
