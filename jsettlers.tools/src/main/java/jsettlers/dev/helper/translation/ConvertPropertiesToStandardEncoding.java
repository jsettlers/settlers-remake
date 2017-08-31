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
package jsettlers.dev.helper.translation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;

/**
 * Convert the translation properties to standard Encoding ISO-8859-1 and \\uXXXX for unicode chars
 * 
 * @author Andreas Butti
 *
 */
public class ConvertPropertiesToStandardEncoding {

	/**
	 * Read the property file to string
	 * 
	 * @param file
	 *            File
	 * @return String
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static String readProperty(File file) throws IOException {
		byte[] encoded = Files.readAllBytes(file.toPath());
		return new String(encoded, "utf-8");
	}

	/**
	 * Encode the property as ISO-8859-1 and escape all unicode chars
	 * 
	 * @param contents
	 *            Contents to write out
	 * @param f
	 *            Target file
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void encodeProperty(String contents, File f) throws IOException {
		try (FileOutputStream out = new FileOutputStream(f)) {
			for (int i = 0; i < contents.length(); i++) {
				int c = contents.charAt(i);

				if (c <= 127) {
					out.write(c);
				} else {
					out.write('\\');
					out.write('u');

					String str = String.format(Locale.ENGLISH, "%04x", c).toUpperCase(Locale.ENGLISH);
					out.write(str.getBytes());
				}
			}

		}

	}

	/**
	 * Convert encoding
	 * 
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws IOException {
		for (File f : ConvertPropertiesToUtf8.listProperties()) {
			System.out.println("->" + f);

			String contents = readProperty(f);
			encodeProperty(contents, f);

		}
	}

}
