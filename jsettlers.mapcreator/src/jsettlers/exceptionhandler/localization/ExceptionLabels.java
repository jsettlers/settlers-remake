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
package jsettlers.exceptionhandler.localization;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import jsettlers.graphics.localization.AbstractLabels;

/**
 * Translation for Exception dialog
 * 
 * @author Andreas Butti
 *
 */
public final class ExceptionLabels extends AbstractLabels {

	/**
	 * Singleton
	 */
	private static final ExceptionLabels instance = new ExceptionLabels();

	/**
	 * Utility class
	 */
	private ExceptionLabels() {
	}

	/**
	 * Gets a string
	 * 
	 * @param key
	 *            The name of the string
	 * @return The localized string
	 */
	public static String getLabel(String key) {
		return instance.getSingleString(key);
	}

	@Override
	protected InputStream getLocaleStream(LocaleSuffix locale) throws IOException {
		String filename = locale.getFileName("labels", ".properties");

		InputStream stream = ExceptionLabels.class.getResourceAsStream(filename);
		if (stream == null) {
			throw new FileNotFoundException(filename);
		}
		return stream;
	}
}
