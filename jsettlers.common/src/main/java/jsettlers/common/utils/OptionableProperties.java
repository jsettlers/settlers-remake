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

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extension of the standard {@link Properties} class.
 * <p />
 * Features: allows to check if an option has been set to true.
 * <p />
 * Options are scanned in this order:
 * <ul>
 * <li>command line options passed to #loadArguments</li>
 * <li>Environment variables in the form of SETTLERS_{NAME}, used as {name}</li>
 * <li>The defaults given to the constructor.</li>
 * </ul>
 *
 * @author Andreas Eberle
 *
 */
public class OptionableProperties extends Properties {
	private static final long serialVersionUID = 6425219415673331880L;
	public static final String ENV_PREFIX = "SETTLERS_";
	public static final String PROPERTIES_PREFIX = "settlers.";

	public OptionableProperties() {
		loadFromEnv();
	}

	public OptionableProperties(Properties defaults) {
		super(defaults);
		loadFromEnv();
	}

	private void loadFromEnv() {
		for (Map.Entry<String, String> e : System.getenv().entrySet()) {
			loadEntry(e, ENV_PREFIX);
		}

		for (Map.Entry<Object, Object> e : System.getProperties().entrySet()) {
			loadEntry(e, PROPERTIES_PREFIX);
		}
	}

	private void loadEntry(Map.Entry<?, ?> e, String envPrefix) {
		if (e.getKey().toString().startsWith(envPrefix)) {
			String key = e.getKey().toString().substring(envPrefix.length()).toLowerCase();
			setProperty(key, e.getValue().toString());
			System.out.println("Argument: " + key + " -> " + e.getValue().toString());
		}
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

	public File getAppHome() {
		String home = getProperty("home");
		if (home != null) {
			return new File(home);
		} else {
			home = System.getProperty("user.home");
			String os = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH);
			String dir = home + File.separator + ".jsettlers";

			if (os.startsWith("windows")) {
				dir = System.getenv("APPDATA") + File.separator + ".jsettlers";
			}
			File dirFile = new File(dir);
			dirFile.mkdirs();
			return dirFile;
		}
	}

	public File getConfigFile() {
		if (containsKey("config")) {
			String configFileName = getProperty("config");
			return new File(configFileName);
		} else {
			return new File(getAppHome(), "config.prp");
		}
	}
}
