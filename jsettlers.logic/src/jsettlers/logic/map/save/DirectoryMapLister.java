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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jsettlers.common.CommonConstants;
import jsettlers.common.utils.FileUtils;
import jsettlers.common.utils.FileUtils.IFileVisitor;
import jsettlers.logic.map.MapLoader;

/**
 * Lists all maps in a directory.
 * 
 * @author michael
 *
 */
public class DirectoryMapLister implements IMapLister {

	private final File directory;

	public static class ListedMapFile implements IListedMap {
		private final File file;

		public ListedMapFile(File file) {
			this.file = file;
		}

		@Override
		public String getFileName() {
			return file.getName().replaceFirst(".*/", "");
		}

		@Override
		public InputStream getInputStream() throws FileNotFoundException {
			return new FileInputStream(file);
		}

		@Override
		public void delete() {
			file.delete();
		}

		@Override
		public boolean isCompressed() {
			return file.getName().endsWith(MapLoader.MAP_EXTENSION_COMPRESSED);
		}
	}

	public DirectoryMapLister(File directory, boolean createIfMissing) {
		this.directory = directory;
		if (createIfMissing && !directory.exists()) {
			directory.mkdirs();
		}
	}

	@Override
	public void listMaps(final IMapListerCallable callback) {
		if (directory == null || !directory.isDirectory() || directory.listFiles() == null) {
			return;
		}

		try {
			// traverse all files and sub-folders
			FileUtils.walkFileTree(directory, new IFileVisitor() {
				@Override
				public void visitFile(File file) throws IOException {
					String fileName = file.getName();
					if (MapLoader.isExtensionKnown(fileName)) {
						callback.foundMap(new ListedMapFile(file));
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public OutputStream getOutputStream(MapFileHeader header) throws IOException {
		String name = header.getName().toLowerCase().replaceAll("\\W+", "");
		if (name.isEmpty()) {
			name = "map";
		}

		Date date = header.getCreationDate();
		if (date != null) {
			SimpleDateFormat format = new SimpleDateFormat("-yyyy-MM-dd_HH-mm-ss");
			name += format.format(date);
		}

		String mapFileExtension = MapList.getMapExtension();

		String actualName = name;
		File file = new File(directory, actualName + mapFileExtension);
		int i = 1;
		while (file.exists()) {
			actualName = name + "-" + i;
			file = new File(directory, actualName + mapFileExtension);
			i++;
		}

		try {
			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));

			if (CommonConstants.USE_SAVEGAME_COMPRESSION) {
				System.out.println("Using savegame compression!");
				ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
				ZipEntry zipEntry = new ZipEntry(actualName + MapLoader.MAP_EXTENSION);
				zipOutputStream.putNextEntry(zipEntry);

				return zipOutputStream;
			} else {
				System.out.println("No savegame compression!");
				return outputStream;
			}
		} catch (FileNotFoundException e) {
			throw new IOException(e);
		}
	}
}
