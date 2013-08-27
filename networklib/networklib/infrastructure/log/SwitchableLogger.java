package networklib.infrastructure.log;

/**
 * A {@link Logger} that delegates the logging to a given {@link Logger}, that can be changed dynamically.
 * 
 * @author Andreas Eberle
 * 
 */
public class SwitchableLogger extends Logger {
	private Logger logger;

	public SwitchableLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void error(Throwable e) {
		logger.error(e);
	}

	@Override
	public void log(String msg) {
		logger.log(msg);
	}

	public void setLogger(Logger newLogger) {
		this.logger = newLogger;
	}

	@Override
	public void warn(String msg) {
		this.logger.warn(msg);
	}

	@Override
	public void info(String msg) {
		this.logger.info(msg);
	}
}
