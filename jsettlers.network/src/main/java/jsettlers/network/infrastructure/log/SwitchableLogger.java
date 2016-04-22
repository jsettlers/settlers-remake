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
