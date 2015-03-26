package jsettlers.network.infrastructure.log;

import java.text.SimpleDateFormat;

/**
 * Abstract super class for all loggers.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class Logger {
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");

	/**
	 * Logs the given exception.
	 * 
	 * @param e
	 *            Exception to be logged.
	 */
	public abstract void error(Throwable e);

	public abstract void log(String msg);

	public abstract void warn(String msg);

	public abstract void info(String msg);
}
