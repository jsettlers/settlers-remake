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
package jsettlers.common.utils;

import java.io.File;

import java8.util.function.Consumer;
import java8.util.function.Predicate;
import jsettlers.common.utils.mutables.Mutable;

public class FileUtils {

	public static void walkFileTree(File file, Consumer<File> consumer) {
		iterateChildren(file, child -> walkFileTree(child, consumer));
		consumer.accept(file);
	}

	public static void iterateChildren(File file, Consumer<File> consumer) {
		iterateChildrenEarlyStopping(file, child -> {
			consumer.accept(child);
			return false;
		});
	}

	/**
	 *
	 * @param file
	 *            File to travers the children. If this is null or a file instead of a directory, the predicate will never be called.
	 * @param stopEarlyPredicate
	 *            This predicate is called for all children and hence can be used to traverse the children. If the predicate returns true, the iteration is stopped early.
	 */
	public static void iterateChildrenEarlyStopping(File file, Predicate<File> stopEarlyPredicate) {
		if (file == null || !file.isDirectory()) {
			return;
		}

		File[] files = file.listFiles();
		if (files != null) {
			for (File child : files) {
				if (stopEarlyPredicate.test(child)) {
					return;
				}
			}
		}
	}

	public static void deleteRecursively(File file) {
		walkFileTree(file, File::delete);
	}

	public static File getFileByNameIgnoringCase(File folder, String name) {
		Mutable<File> result = new Mutable<>();

		iterateChildrenEarlyStopping(folder, currentFile -> {
			if (currentFile.isFile() && nameEqualsIgnoringCase(name, currentFile)) {
				result.object = currentFile;
				return true; // stop iteration because we found the file
			}
			return false;
		});

		return result.object;
	}

	public static boolean nameEqualsIgnoringCase(String name, File file) {
		return name.equalsIgnoreCase(file.getName());
	}
}
