package jsettlers.common.utils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to cluster util functions concerning the main classes.
 * 
 * @author Andreas Eberle
 * 
 */
public final class MainUtils {
	private MainUtils() {
	}

	public static HashMap<String, String> createArgumentsMap(String[] args) {
		HashMap<String, String> argsMap = new HashMap<String, String>();

		Pattern parameterPattern = Pattern.compile("--(.*?)=(.*?)");
		Pattern optionPattern = Pattern.compile("--(.*?)");

		for (String arg : args) {
			Matcher parameterMatcher = parameterPattern.matcher(arg);
			if (parameterMatcher.matches()) {
				String parameter = parameterMatcher.group(1);
				String value = parameterMatcher.group(2);
				argsMap.put(parameter, value);
			} else {
				Matcher optionMatcher = optionPattern.matcher(arg);
				if (optionMatcher.matches()) {
					String option = optionMatcher.group(1);
					argsMap.put(option, null);
				}
			}
		}
		return argsMap;
	}
}
