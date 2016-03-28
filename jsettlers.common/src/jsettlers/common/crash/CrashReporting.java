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
package jsettlers.common.crash;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Utility class that manages crashes and crash reporting.
 * 
 * @author Michael Zangl
 */
public final class CrashReporting {
	private static CrashReportQueue queue = new CrashReportQueue();

	private CrashReporting() {
	}

	/**
	 * This creates a new crash report.
	 * 
	 * @param t
	 *            The exception thrown
	 * @return The crash report about that exception.
	 */
	public static CrashReportedException create(Throwable t) {
		CrashReportedException e;
		if (t instanceof CrashReportedException) {
			e = (CrashReportedException) t;
		} else {
			e = new CrashReportedException(t);
		}
		e.startSection(getCallingMethod(2));
		return e;
	}

	/**
	 * Find the method that called us.
	 * 
	 * @param offset
	 *            How many methods to look back in the stack trace. 1 gives the method calling this method, 0 gives you getCallingMethod().
	 * @return The method name.
	 */
	static String getCallingMethod(int offset) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		String className = CrashReporting.class.getName();
		for (int i = 0; i < stackTrace.length - offset; i++) {
			StackTraceElement element = stackTrace[i];
			if (className.equals(element.getClassName()) && "getCallingMethod".equals(element.getMethodName())) {
				StackTraceElement toReturn = stackTrace[i + offset];
				return toReturn.getClassName().replaceFirst(".*\\.", "") + "#" + toReturn.getMethodName();
			}
		}
		return "?";
	}

	/**
	 * Sets the global crash handler that is informed on application crashes.
	 * 
	 * @param handler
	 *            The crash handler.
	 */
	public static void setCrashHandler(CrashHandler handler) {
		queue.setCrashHandler(handler);
	}

	/**
	 * Requests to display a warning for the given exception.
	 * 
	 * @param crashReportedException
	 *            The exception.
	 */
	static void warnFor(CrashReportedException crashReportedException) {
		queue.enqueue(crashReportedException);
	}

	/**
	 * Uses {@link Thread#setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler)} and sets it to use this bug report system.
	 */
	public static void setupUncaughtExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				CrashReportedException reported = new CrashReportedException(e, t);
				reported.startSection("Global exception handler");
				reported.warn();
			}
		});
	}
}
