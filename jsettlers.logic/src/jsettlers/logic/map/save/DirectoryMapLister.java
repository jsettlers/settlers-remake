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
package jsettlers.logic.map.save;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import jsettlers.common.utils.FileUtils;
import jsettlers.common.utils.FileUtils.IFileVisitor;

/**
 * Lists all maps in a directory.
 * 
 * @author michael
 *
 */
public class DirectoryMapLister implements IMapLister {

	private final File directory;
	private final boolean writeable;

	public static class ListedMapFile implements IListedMap {
		private final File file;
		private final boolean writeable;

		public ListedMapFile(File file, boolean writeable) {
			this.file = file;
			this.writeable = writeable;
			// TODO Auto-generated constructor stub
		}

		@Override
		public String getFileName() {
			return file.getName();
		}

		@Override
		public InputStream getInputStream() throws FileNotFoundException {
			return new FileInputStream(file);
		}

		@Override
		public void delete() {
			if (!writeable) {
				throw new UnsupportedOperationException();
			}
			file.delete();
		}
	}

	public DirectoryMapLister(File directory, boolean writeable) {
		this.directory = directory;
		this.writeable = writeable;
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	@Override
	public void getMaps(final IMapListerCallable callable) {

		File[] files = directory.listFiles();
		if (files == null) {
			throw new IllegalArgumentException("map directory "
					+ directory.getAbsolutePath() + " is not a directory.");
		}

		try {
			// traverse all folders
			FileUtils.walkFileTree(directory, new IFileVisitor() {
				@Override
				public void visitFile(File file) throws IOException {
					if (file.getName().endsWith(MapList.MAP_EXTENSION)) {
						callable.foundMap(new ListedMapFile(file, writeable));
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public OutputStream getOutputStream(MapFileHeader header)
			throws IOException {
		if (!writeable) {
			throw new UnsupportedOperationException();
		}

		String name = header.getName().toLowerCase().replaceAll("\\W+", "");
		if (name.isEmpty()) {
			name = "map";
		}

		Date date = header.getCreationDate();
		if (date != null) {
			SimpleDateFormat format = new SimpleDateFormat(
					"-yyyy-MM-dd_HH-mm-ss");
			name += format.format(date);
		}

		File file = new File(directory, name + MapList.MAP_EXTENSION);
		int i = 1;
		while (file.exists()) {
			file = new File(directory, name + "-" + i + MapList.MAP_EXTENSION);
			i++;
		}
		try {
			return new BufferedOutputStream(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			throw new IOException(e);
		}
	}

}
