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
package jsettlers.common.utils;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extension of the standard {@link Properties} class.
 * <p />
 * Features: allows to check if an option has been set to true and load the startup arguments as properties file.
 * 
 * @author Andreas Eberle
 *
 */
public class OptionableProperties extends Properties {
	private static final long serialVersionUID = 6425219415673331880L;

	public OptionableProperties() {
	}

	public OptionableProperties(Properties defaults) {
		super(defaults);
	}

	public boolean isOptionSet(String key) {
		String value = super.getProperty(key);
		return value != null && value.toLowerCase().equals("true");
	}

	public void loadArguments(String[] args) {
		Pattern parameterPattern = Pattern.compile("--(.*?)=(.*?)");
		Pattern optionPattern = Pattern.compile("--(.*?)");

		for (String arg : args) {
			Matcher parameterMatcher = parameterPattern.matcher(arg);
			if (parameterMatcher.matches()) {
				String parameter = parameterMatcher.group(1);
				String value = parameterMatcher.group(2);
				this.setProperty(parameter, value);
			} else {
				Matcher optionMatcher = optionPattern.matcher(arg);
				if (optionMatcher.matches()) {
					String option = optionMatcher.group(1);
					this.setProperty(option, "true");
				}
			}
		}
	}
}
