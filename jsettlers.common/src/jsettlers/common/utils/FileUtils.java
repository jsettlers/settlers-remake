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
import java.io.IOException;

public class FileUtils {

	public interface IFileVisitor {
		void visitFile(File file) throws IOException;
	}

	public static void walkFileTree(File file, IFileVisitor fileVisitor) throws IOException {
		if (file == null) {
			return;
		}

		File[] files = file.listFiles();
		if (files != null) {
			for (File child : files) {
				walkFileTree(child, fileVisitor);
			}
		}

		fileVisitor.visitFile(file);
	}

	public static void deleteRecursively(File file) throws IOException {
		walkFileTree(file, new IFileVisitor() {
			@Override
			public void visitFile(File file) throws IOException {
				file.delete();
			}
		});
	}
}
