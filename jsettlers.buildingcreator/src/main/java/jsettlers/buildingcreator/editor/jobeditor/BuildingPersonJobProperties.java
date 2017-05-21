/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.buildingcreator.editor.jobeditor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * This is an action a person can do. Every action may have some properties. , which are all enums.
 * 
 * @author michael
 */
public class BuildingPersonJobProperties {
	private final Properties properties;

	private Properties defaultProperties;

	/**
	 * action type id => key1, key2, ... The key "type" is not explicitly mentined.
	 */
	private static Properties allowedKeyFile;

	/**
	 * key => value1, value2, value3
	 */
	private static Properties allowedValueFile;

	public BuildingPersonJobProperties() {
		properties = new Properties(getDefaultProperties());
	}

	private Properties getDefaultProperties() {
		if (defaultProperties == null) {
			defaultProperties = new Properties();
			Enumeration<Object> keys = getAllowedValueFile().keys();
			while (keys.hasMoreElements()) {
				Object current = keys.nextElement();
				if (current instanceof String) {
					String value = getAllowedValues((String) current).get(0);
					defaultProperties.setProperty((String) current, value);
				}
			}
		}
		return defaultProperties;
	}

	/**
	 * gets the allowed keys for this action, this includes the "type" key.
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<String> getAllowedKeys() {
		if (allowedKeyFile == null) {
			allowedKeyFile = new Properties();
			InputStream in = getClass().getResourceAsStream("actionkeys.props");
			try {
				allowedKeyFile.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				close(in);
			}
		}

		String myKeys =
				allowedKeyFile.getProperty(properties.getProperty("type"));
		if (myKeys == null) {
			myKeys = "";
		}

		List<String> keys = Arrays.asList(myKeys.split(","));
		List<String> result = new LinkedList<>();
		result.add("type");
		for (String key : keys) {
			if (!result.contains(key) && getAllowedValueFile().containsKey(key)) {
				result.add(key);
			}
		}
		return result;
	}

	public List<String> getAllowedValues(String key) {
		String values = getAllowedValueFile().getProperty(key);
		return Arrays.asList(values.split(",\\s*"));
	}

	private Properties getAllowedValueFile() {
		if (allowedValueFile == null) {
			allowedValueFile = new Properties();
			InputStream in =
					getClass().getResourceAsStream("keyvalues.props");
			try {
				allowedValueFile.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				close(in);
			}
		}
		return allowedValueFile;
	}

	private void close(InputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public boolean setProperty(String key, String value) {
		List<String> allowed = getAllowedValues(key);
		if (allowed.contains(value)) {
			properties.setProperty(key, value);
			return true;
		} else {
			return false;
		}
	}

}
