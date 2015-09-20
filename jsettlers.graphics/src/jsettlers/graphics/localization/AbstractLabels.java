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
package jsettlers.graphics.localization;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Properties;

/**
 * This is a class that helps with the locale management. Don't use it directly, use {@link Labels} instead.
 * 
 * @author Michael Zangl
 *
 */
public abstract class AbstractLabels {
	public static Locale preferredLocale = Locale.getDefault();

	public static class LocaleSuffix {
		private Locale locale;
		private boolean useCountry;

		public LocaleSuffix(Locale locale, boolean useCountry) {
			this.locale = locale;
			this.useCountry = useCountry;
		}

		public Locale getLocale() {
			return locale;
		}

		public String getFileName(String prefix, String suffix) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(prefix);
			stringBuilder.append("_");
			stringBuilder.append(locale.getLanguage());
			if (useCountry) {
				stringBuilder.append("_");
				stringBuilder.append(locale.getCountry());
			}
			stringBuilder.append(suffix);
			return stringBuilder.toString();
		}

		@Override
		public String toString() {
			return getFileName("LocaleSuffix [", "]");
		}

	}

	private Properties labels;
	private boolean labelsLoaded;
	/**
	 * The most dominant locale that was used.
	 */
	protected Locale usedLocale;

	private synchronized Properties getLabels() {
		if (!labelsLoaded) {
			loadLabels();
			labelsLoaded = true;
		}
		return labels;
	}

	private void loadLabels() {
		LocaleSuffix[] locales = getLocaleSuffixes();

		for (LocaleSuffix locale : locales) {
			try {
				Properties currentLocaleLabels = new Properties(labels);
				InputStream inputStream = getLocaleStream(locale);
				currentLocaleLabels.load(new InputStreamReader(inputStream, "UTF-8"));
				labels = currentLocaleLabels;
				// Store the most dominant locale found.
				usedLocale = locale.getLocale();
			} catch (IOException e) {
				System.err.println("Warning: Could not load " + locale + ". Falling back to next file.");
			}
		}
	}

	protected abstract InputStream getLocaleStream(LocaleSuffix locale) throws IOException;

	/**
	 * Gets a list of locale suffixes.
	 * 
	 * @return The list, ordered from least to most preferred.
	 */
	public LocaleSuffix[] getLocaleSuffixes() {
		LocaleSuffix[] locales = new LocaleSuffix[] {
				new LocaleSuffix(new Locale("en"), false),
				new LocaleSuffix(preferredLocale, false),
				new LocaleSuffix(preferredLocale, true),
		};
		return locales;
	}

	/**
	 * Gets a string
	 * 
	 * @param key
	 *            The name of the string
	 * @return The localized string
	 */
	public String getSingleString(String key) {
		Properties labels = getLabels();
		if (labels == null) {
			return key;
		} else {
			String value = labels.getProperty(key);
			if (value == null) {
				return key;
			} else {
				return value;
			}
		}
	}
}
