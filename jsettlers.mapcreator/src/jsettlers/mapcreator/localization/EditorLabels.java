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
package jsettlers.mapcreator.localization;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.PropertyResourceBundle;

import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.localization.Labels.LocaleSuffix;

public class EditorLabels {
	private static boolean load;
	private static PropertyResourceBundle resource;

	public synchronized static String getLabel(String name) {
		if (!load) {
			LocaleSuffix[] locales = Labels.getLocaleSuffixes();

			for (LocaleSuffix locale : locales) {
				String filename = locale.getFileName("labels", ".properties");
				try {
					InputStream stream = EditorLabels.class.getResourceAsStream(filename);
					if (stream == null) {
						continue;
					}
					resource = new PropertyResourceBundle(new InputStreamReader(
							stream, "UTF-8"));
					break;
				} catch (IOException e) {
				}
			}
			load = true;
		}
		if (resource == null || !resource.containsKey(name)) {
			return name;
		} else {
			return resource.getString(name);
		}
	}
}
