package networklib.infrastructure.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public final class LoggerManager {
	private static final File LOG_FOLDER = new File("logs/");

	public static final Logger ROOT_LOGGER;
	private static final HashMap<String, StreamLogger> LOGGERS = new HashMap<String, StreamLogger>();

	static {
		Logger logger;
		try {
			logger = new StreamLogger("rootLogger", new File(LOG_FOLDER, "server-" + Logger.DATE_FORMAT.format(new Date()) + ".log"));
		} catch (FileNotFoundException e) {
			logger = new ConsoleLogger("rootLogger");
			logger.error(e);
		}
		ROOT_LOGGER = logger;
	}

	// No objects of this class can be created.
	private LoggerManager() {
	}

	public static Logger getMatchLogger(String matchId, String matchName) {
		StreamLogger logger = LOGGERS.get(matchId);

		if (logger == null) {
			File matchLogFile = new File(LOG_FOLDER, matchId + "-" + matchName + "/match.log");
			matchLogFile.getParentFile().mkdir();
			try {
				logger = new StreamLogger(matchId, matchLogFile);
			} catch (FileNotFoundException e) {
				logger = new ConsoleLogger(matchId);
			}
			LOGGERS.put(matchId, logger);
		}

		return logger;
	}

	static void removeLogger(String loggerId) {
		LOGGERS.remove(loggerId);
	}
}
