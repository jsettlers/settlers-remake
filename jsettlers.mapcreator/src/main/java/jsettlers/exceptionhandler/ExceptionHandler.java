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
package jsettlers.exceptionhandler;

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
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> ExceptionHandler.displayError(e, "Unhandled error in Thread " + t.getName()));
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
			SwingUtilities.invokeLater(() -> displayError(e, description, t));
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