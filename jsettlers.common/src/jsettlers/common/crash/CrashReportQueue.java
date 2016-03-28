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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import jsettlers.common.crash.CrashHandler.CrashHandlerResultReceiver;

/**
 * This is a queue of pending crash reports that should be displayed.
 *
 * @author Michael Zangl
 *
 */
public class CrashReportQueue implements CrashHandlerResultReceiver {
	/**
	 * Maximum number of pending crash reports. If there are more, they are discared.
	 */
	private static final int MAX_PENDING = 100;

	/**
	 * Maximum number of crash reports that are considered to be the same type.
	 */
	private static final int MAX_PENDING_SAME_TYPE = 11;

	private final LinkedList<CrashReportedException> pending = new LinkedList<>();

	/**
	 * A list of crash report types that should be suppressed.
	 */
	private final ArrayList<CrashReportedException> suppressed = new ArrayList<>();

	private boolean suppressAllReports;

	private boolean crashHandlerActive;

	/**
	 * This prevents a recursion in {@link #recheckShowDialog()}.
	 */
	private boolean crashHandlerActiveAsync;

	/**
	 * We use the console crash handler as default handler. This should be replaced with a more advanced handler during program startup.
	 */
	private CrashHandler crashHandler = new ConsoleCrashHandler();

	/**
	 * Enqueues a crash report that should be displayed.
	 * 
	 * @param exception
	 *            The exception to display.
	 */
	public synchronized void enqueue(CrashReportedException exception) {
		if (isSuppressed(exception)) {
			trace("Dropped pending crash report because it is Suppressed.");
		} else if (pending.size() >= MAX_PENDING) {
			trace("Dropped pending crash report because we exceeded total limit.");
		} else if (countSameInPending(exception) >= MAX_PENDING_SAME_TYPE) {
			trace("Dropped pending crash report because we exceeded same type limit.");
		} else {
			pending.add(exception);
			recheckShowDialog();
		}
	}

	private void trace(String string) {
		System.err.println(string);
	}

	/**
	 * Displays the crash report dialog if required.
	 */
	private void recheckShowDialog() {
		if (!pending.isEmpty() && !crashHandlerActive) {
			CrashReportedException toShow = pending.poll();
			crashHandlerActive = true;
			crashHandler.handleCrash(toShow, this);
			if (crashHandlerActive) {
				crashHandlerActiveAsync = true;
			} else {
				recheckShowDialog();
			}
		}
	}

	@Override
	public synchronized void suppressSameCrashes(CrashReportedException toSuppress) {
		suppressed.add(toSuppress);
		int pendingRemoved = 0;
		for (Iterator<CrashReportedException> iterator = pending.iterator(); iterator.hasNext();) {
			CrashReportedException e = iterator.next();
			if (toSuppress.isSame(e)) {
				pendingRemoved++;
				iterator.remove();
			}
		}
		if (pendingRemoved > 0) {
			trace("Suppressed " + pendingRemoved + " pending error reports.");
		}
	}

	@Override
	public synchronized void suppressAllCrashes() {
		this.suppressAllReports = true;
		pending.clear();
	}

	@Override
	public synchronized void crashHandled(CrashReportedException e) {
		crashHandlerActive = false;
		boolean wasAsync = crashHandlerActiveAsync;
		crashHandlerActiveAsync = false;

		if (wasAsync) {
			recheckShowDialog();
		}
	}

	private boolean isSuppressed(CrashReportedException exception) {
		boolean isSuppressed = suppressAllReports;
		for (int i = 0; i < suppressed.size() && !isSuppressed; i++) {
			CrashReportedException s = suppressed.get(i);
			isSuppressed |= s.isSame(exception);
		}
		return isSuppressed;
	}

	/**
	 * Counts how many crash reports are considered the same in the pending list.
	 * 
	 * @param sameTo
	 *            The crash report to compare to.
	 * @return That number
	 */
	private int countSameInPending(CrashReportedException sameTo) {
		int count = 0;
		for (CrashReportedException p : pending) {
			if (sameTo.isSame(p)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Sets the crash handler that should be informed by this queue.
	 * 
	 * @param crashHandler
	 *            The crash handler.
	 */
	public synchronized void setCrashHandler(CrashHandler crashHandler) {
		this.crashHandler = crashHandler;
	}
}
