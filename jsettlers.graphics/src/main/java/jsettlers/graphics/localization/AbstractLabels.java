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
	private static Locale preferredLocale = Locale.getDefault();

	/**
	 * This defines a locale suffix (like _en_US).
	 * 
	 * @author Michael Zangl
	 */
	public static class LocaleSuffix {
		private static final String SEPARATOR = "_";
		private Locale locale;
		private boolean useCountry;

		/**
		 * Creates a new locale suffix object.
		 * 
		 * @param locale
		 *            The locale to use.
		 * @param useCountry
		 *            The we should interpret the country field of the locale.
		 */
		public LocaleSuffix(Locale locale, boolean useCountry) {
			this.locale = locale;
			this.useCountry = useCountry;
		}

		/**
		 * Gets the locale used.
		 * 
		 * @return The {@link Locale}
		 */
		public Locale getLocale() {
			return locale;
		}

		/**
		 * Creates a file name with this suffix.
		 * 
		 * @param prefix
		 *            The file prefix.
		 * @param suffix
		 *            The file ending to append after the name.
		 * @return The new name.
		 */
		public String getFileName(String prefix, String suffix) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(prefix);
			stringBuilder.append(SEPARATOR);
			stringBuilder.append(locale.getLanguage());
			if (useCountry) {
				stringBuilder.append(SEPARATOR);
				stringBuilder.append(locale.getCountry());
			}
			stringBuilder.append(suffix);
			return stringBuilder.toString();
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("LocaleSuffix [locale=");
			builder.append(locale);
			builder.append(", useCountry=");
			builder.append(useCountry);
			builder.append("]");
			return builder.toString();
		}

	}

	private Properties loadedLabels;
	private boolean labelsLoaded;
	/**
	 * The most dominant locale that was used.
	 */
	private Locale usedLocale;

	private synchronized Properties getLabels() {
		if (!labelsLoaded) {
			loadLabels();
			labelsLoaded = true;
		}
		return loadedLabels;
	}

	private void loadLabels() {
		LocaleSuffix[] locales = getLocaleSuffixes();

		for (LocaleSuffix locale : locales) {
			try {
				Properties currentLocaleLabels = new Properties(loadedLabels);
				InputStream inputStream = getLocaleStream(locale);
				if (inputStream == null) {
					throw new IOException();
				}
				currentLocaleLabels.load(new InputStreamReader(inputStream, "UTF-8"));
				loadedLabels = currentLocaleLabels;
				// Store the most dominant locale found.
				usedLocale = locale.getLocale();
			} catch (IOException e) {
				System.err.println("Warning: Could not load " + locale + ". Falling back to next file.");
			}
		}
	}

	/**
	 * Generates an input stream for the given locale.
	 * 
	 * @param locale
	 *            The locale to generate the input stream for.
	 * @return The stream.
	 * @throws IOException
	 *             If the stream could not be generated.
	 */
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
	 * Gets a string.
	 * 
	 * @param key
	 *            The name of the string
	 * @return The localized string
	 */
	public String getSingleString(String key) {
		Properties labels = getLabels();
		if (labels != null) {
			String value = labels.getProperty(key);
			if (value != null) {
				return value;
			}
		}
		return key;
	}

	/**
	 * Gets the locale that was used.
	 * 
	 * @return The locale.
	 */
	public Locale getUsedLocale() {
		return usedLocale;
	}

	/**
	 * Sets the preferred locale to use. This is only regarded when creating new locale objects.
	 * 
	 * @param preferredLocale
	 *            The new proffered locale.
	 */
	public static void setPreferredLocale(Locale preferredLocale) {
		AbstractLabels.preferredLocale = preferredLocale;
	}
}
