package networklib.infrastructure.log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

/**
 * {@link Logger} that writes the output to a given stream or a file.
 * 
 * @author Andreas Eberle
 * 
 */
public class StreamLogger extends Logger {

	private final String loggerId;
	private final PrintStream outStream;

	public StreamLogger(String loggerId, PrintStream outStream) {
		this.loggerId = loggerId;
		this.outStream = outStream;
	}

	public StreamLogger(String loggerId, File logFile) throws FileNotFoundException {
		this(loggerId, new PrintStream(new BufferedOutputStream(new FileOutputStream(logFile)), true));
	}

	private synchronized void println(String msg) {
		outStream.println(Logger.DATE_FORMAT.format(new Date()) + ": " + msg);
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
