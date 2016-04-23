/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.network.infrastructure.log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * {@link Logger} that writes the output to a given stream or a file.
 * 
 * @author Andreas Eberle
 * 
 */
public class StreamLogger extends Logger {
	private static final SimpleDateFormat DATE_FORMAT = Logger.getDateFormat();

	private final String loggerId;
	private final PrintStream outStream;

	public StreamLogger(String loggerId, PrintStream outStream) {
		this.loggerId = loggerId;
		this.outStream = outStream;
	}

	public StreamLogger(String loggerId, File logFile) throws FileNotFoundException {
		this(loggerId, createStream(logFile));
	}

	private static PrintStream createStream(File logFile) throws FileNotFoundException {
		logFile.getParentFile().mkdirs();
		return new PrintStream(new BufferedOutputStream(new FileOutputStream(logFile)), true);
	}

	private synchronized void println(String msg) {
		outStream.println(DATE_FORMAT.format(new Date()) + ": " + msg);
	}

	public void close() {
		outStream.close();
		LoggerManager.removeLogger(loggerId);
	}

	@Override
	public void log(String msg) {
		info(msg);
	}

	@Override
	public void warn(String msg) {
		println("WARN: " + msg);
	}

	@Override
	public synchronized void error(Throwable e) {
		outStream.print(DATE_FORMAT.format(new Date()) + ": ERROR: ");
		e.printStackTrace(outStream);
	}

	@Override
	public void info(String msg) {
		println("INFO: " + msg);
	}
}
