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
package jsettlers.logic.map.loading.list;

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
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jsettlers.common.CommonConstants;
import jsettlers.common.utils.FileUtils;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.newmap.MapFileHeader;

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

		@Override
		public File getFile() {
			return file;
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

		// traverse all files and sub-folders
		FileUtils.walkFileTree(directory, file -> {
			String fileName = file.getName();
			if (!file.isDirectory() && MapLoader.isExtensionKnown(fileName)) {
				callback.foundMap(new ListedMapFile(file));
			}
		});
	}

	@Override
	public OutputStream getOutputStream(MapFileHeader header) throws IOException {
		String name = header.getName().toLowerCase(Locale.ENGLISH).replaceAll("^\\W+|\\W+$", "").replaceAll("\\W+", "_");
		if (name.isEmpty()) {
			name = "map";
		}

		String sizePrefix;
		if (header.getWidth() == header.getHeight()) {
			sizePrefix = "" + header.getWidth();
		} else {
			sizePrefix = header.getWidth() + "x" + header.getHeight();
		}
		name = sizePrefix + "-" + header.getMaxPlayers() + "-" + name;

		Date date = header.getCreationDate();
		if (date != null) {
			SimpleDateFormat format = new SimpleDateFormat("-yyyy-MM-dd_HH-mm-ss", Locale.ENGLISH);
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

		if (!directory.exists()) {
			directory.mkdirs();
		}

		if (!directory.isDirectory()) {
			throw new IOException("maps directory does not exist.");
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
