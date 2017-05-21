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
	private static final HashMap<String, StreamLogger> LOGGERS = new HashMap<>();

	static {
		Logger logger;
		try {
			logger = new StreamLogger("rootLogger", new File(LOG_FOLDER, "server-" + Logger.getDateFormat().format(new Date()) + ".log"));
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
