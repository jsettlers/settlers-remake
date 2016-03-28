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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This is a special {@link RuntimeException} that can be thrown to trigger a crash report and add data to that crash report.
 * <p>
 * After creating an exception you should do one of those things:
 * <ul>
 * <li>Throw it to let normal excpetion processing continue
 * <li>Call warn() to display a warning to the user.
 * </ul>
 * 
 * @author Michael Zangl
 */
public class CrashReportedException extends RuntimeException {
	private static final int MAX_COLLECTION_ENTRIES = 30;
	/**
	 * 
	 */
	private static final long serialVersionUID = 737333873766201033L;
	private final Map<Thread, StackTraceElement[]> allStackTraces;
	private final LinkedList<Section> sections = new LinkedList<>();
	private final Thread caughtOnThread;
	private final Throwable exception;
	private String methodWarningFrom;

	CrashReportedException(Throwable exception) {
		this(exception, Thread.currentThread());
	}

	CrashReportedException(Throwable exception, Thread caughtOnThread) {
		super(exception);
		this.exception = exception;

		allStackTraces = Thread.getAllStackTraces();
		this.caughtOnThread = caughtOnThread;
	}

	/**
	 * Displays a warning for this exception. The program can then continue normally. Does not block.
	 */
	public void warn() {
		methodWarningFrom = CrashReporting.getCallingMethod(2);
		CrashReporting.warnFor(this);
	}

	/**
	 * Starts a new debug data section. This normally does not need to be called manually.
	 * 
	 * @param sectionName
	 *            The section name.
	 */
	public void startSection(String sectionName) {
		sections.add(new Section(sectionName));
	}

	/**
	 * Prints the report to the console.
	 * 
	 * @param out
	 *            The console to print to.
	 * @param settings
	 *            Some settings that influence the report generation.
	 */
	public void printReportTo(PrintStream out, CrashReportSettings settings) {
		out.println("=== REPORTED CRASH DATA ===");
		if (settings.isAddDebugData()) {
			for (Section s : sections) {
				s.printSection(out);
				out.println();
			}

			if (methodWarningFrom != null) {
				out.println("Warning issued by: " + methodWarningFrom);
				out.println();
			}
		} else {
			out.println("The user chose not to add debug data.");
			out.println();
		}

		out.println("=== STACK TRACE ===");
		out.println(niceThreadName(caughtOnThread));
		getCause().printStackTrace(out);
		out.println();

		if (settings.isAddAllStackTraces()) {
			out.println("=== RUNNING THREADS ===");
			for (Entry<Thread, StackTraceElement[]> thread : allStackTraces.entrySet()) {
				out.println(niceThreadName(thread.getKey()));
				if (thread.getKey() == caughtOnThread) {
					out.println("Stacktrace see above.");
				} else {
					for (StackTraceElement e : thread.getValue()) {
						out.println(e);
					}
				}
				out.println();
			}
		}
	}

	private String niceThreadName(Thread thread) {
		String name = "Thread: " + thread.getName() + " (" + thread.getId() + ")";
		ThreadGroup threadGroup = thread.getThreadGroup();
		if (threadGroup != null) {
			name += " of " + threadGroup.getName();
		}
		return name;
	}

	/**
	 * Checks if this exception is considered the same as an other exception. This is the case if both have the same cause and message.
	 * 
	 * @param e
	 *            THe exception to check against.
	 * @return <code>true</code> if they are considered the same.
	 */
	public boolean isSame(CrashReportedException e) {
		if (!getMessage().equals(e.getMessage())) {
			return false;
		}

		Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
		return hasSameStackTrace(dejaVu, this.exception, e.exception);
	}

	private boolean hasSameStackTrace(Set<Throwable> dejaVu, Throwable e1, Throwable e2) {
		if (dejaVu.contains(e1)) {
			// cycle. If it was the same until here, we hope both have that cycle.
			return true;
		}
		dejaVu.add(e1);

		StackTraceElement[] t1 = e1.getStackTrace();
		StackTraceElement[] t2 = e2.getStackTrace();

		if (!Arrays.equals(t1, t2)) {
			return false;
		}

		Throwable c1 = e1.getCause();
		Throwable c2 = e2.getCause();
		if ((c1 == null) != (c2 == null)) {
			return false;
		} else if (c1 != null) {
			return hasSameStackTrace(dejaVu, c1, c2);
		} else {
			return true;
		}
	}

	/**
	 * Adds some debug values to this exception.
	 * 
	 * @param key
	 *            The key to add this for. Does not need to be unique but it would be nice.
	 * @param value
	 *            The value.
	 * @return This exception for easy chaining.
	 */
	public CrashReportedException put(String key, Object value) {
		String string;
		try {
			if (value == null) {
				string = "null";
			} else if (value instanceof Collection) {
				string = makeCollectionNice((Collection<?>) value);
			} else if (value.getClass().isArray()) {
				string = makeCollectionNice(Arrays.asList(value));
			} else {
				string = value.toString();
			}
		} catch (Throwable t) {
			string = "<Error calling toString()>";
		}
		sections.getLast().put(key, string);
		return this;
	}

	private String makeCollectionNice(Collection<?> value) {
		int lines = 0;
		String str = "";
		for (Object e : value) {
			str += "\n    - ";
			if (lines <= MAX_COLLECTION_ENTRIES) {
				str += e;
			} else {
				str += "...";
				break;
			}
		}
		return str;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CrashReportedException [on thread ");
		builder.append(caughtOnThread);
		builder.append("]");
		return builder.toString();
	}

	private static class SectionEntry {
		private final String key;
		private final String value;

		SectionEntry(String key, String value) {
			this.key = key;
			this.value = value;

		}

		public void print(PrintStream out) {
			out.print(" - ");
			out.print(key);
			out.print(": ");
			out.println(value);
		}
	}

	private static class Section {

		private String sectionName;
		private ArrayList<SectionEntry> entries = new ArrayList<>();

		Section(String sectionName) {
			this.sectionName = sectionName;
		}

		public void put(String key, String value) {
			entries.add(new SectionEntry(key, value));
		}

		public void printSection(PrintStream out) {
			out.println(sectionName + ":");
			if (entries.isEmpty()) {
				out.println("No data collected.");
			} else {
				for (SectionEntry e : entries) {
					e.print(out);
				}
			}
		}

	}

}
