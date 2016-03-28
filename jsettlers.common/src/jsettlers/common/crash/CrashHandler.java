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

/**
 * Classes implementing this interface can handle crash reports.
 * 
 * @author Michael Zangl
 */
public interface CrashHandler {
	/**
	 * This interface provides methods to handle the result of displaying a crash report.
	 * 
	 * @author michael
	 *
	 */
	interface CrashHandlerResultReceiver {
		/**
		 * Suppress all crashes that are the same as the given one.
		 * 
		 * @param e
		 *            The exception
		 * @see CrashReportedException#isSame(CrashReportedException)
		 */
		void suppressSameCrashes(CrashReportedException e);

		/**
		 * Suppress all crashes in the future.
		 */
		void suppressAllCrashes();

		/**
		 * This method needs to be called once the crash was successfully handled.
		 * 
		 * @param e
		 *            The exception, just to do sanity checks.
		 */
		void crashHandled(CrashReportedException e);
	}

	/**
	 * Handles a given crash. Should not block.
	 * <p>
	 * The next crash is send to the handler only after {@link CrashHandlerResultReceiver#crashHandled(CrashReportedException)} was called.
	 * 
	 * @param exception
	 *            The exception to handle.
	 * @param r
	 *            A receiver to call whenever the crash was completely handled.
	 */
	void handleCrash(CrashReportedException exception, CrashHandlerResultReceiver r);
}
