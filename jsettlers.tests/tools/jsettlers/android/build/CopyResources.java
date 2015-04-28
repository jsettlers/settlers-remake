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
package jsettlers.android.build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * copys resources to the right android directory.
 * 
 * @author michael
 */
public final class CopyResources {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Too few arguments");
			System.exit(1);
		}
		File resourcesrc = new File(args[0]);
		File resourcedest = new File(args[1]);

		if (!resourcesrc.isDirectory() || !resourcedest.isDirectory()) {
			System.err.println("Source or dest is not a directory");
			System.exit(1);
		}

		copy(resourcesrc, resourcedest);
	}

	private static void copy(File src, File dest) {
		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdirs();
			}
			for (File file : src.listFiles()) {
				String name = file.getName();
				File srcFile = new File(src, name);
				copy(srcFile, dest);
			}
		} else if (src.exists()) {
			try {
				FileInputStream in = new FileInputStream(src);
				FileOutputStream out = new FileOutputStream(dest);
				byte[] buffer = new byte[1024];
				while (in.available() > 0) {
					int read = in.read(buffer);
					out.write(buffer, 0, read);
				}
				in.close();
				out.close();
			} catch (IOException e) {
			}
		}
	}
}
