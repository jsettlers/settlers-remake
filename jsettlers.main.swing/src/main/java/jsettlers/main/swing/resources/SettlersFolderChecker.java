/*******************************************************************************
 * Copyright (c) 2015 - 2016
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
package jsettlers.main.swing.resources;

import java.io.File;

import jsettlers.graphics.reader.DatFileType;

/**
 * Checks if a settler folder is valid
 * 
 * @author Andreas Butti
 * @author Andreas Eberle
 */
public final class SettlersFolderChecker {

	/**
	 * No Instances of class allowed
	 */
	private SettlersFolderChecker() {
	}

	/**
	 * Checks a potential Settlers III installation folder and extracts the gfx and snd folders.
	 * 
	 * @param settlersBaseFolder
	 *            Folder of Settlers III installation
	 * @return Instance of {@link SettlersFolderInfo}.
	 */
	public static SettlersFolderInfo checkSettlersFolder(String settlersBaseFolder) {
		if (settlersBaseFolder == null) {
			return new SettlersFolderInfo();
		}

		if (settlersBaseFolder.isEmpty()) {
			return new SettlersFolderInfo();
		}

		File settlersBaseFolderFile = new File(settlersBaseFolder);
		if (!settlersBaseFolderFile.exists() || !settlersBaseFolderFile.isDirectory()) {
			return new SettlersFolderInfo();
		}

		File[] files = settlersBaseFolderFile.listFiles();
		if (files == null) {
			return new SettlersFolderInfo();
		}

		File sndFolder = null;
		File gfxFolder = null;
		File mapsFolder = null;

		for (File f : files) {
			if (f.getName().equalsIgnoreCase("snd")) {
				File testSndFile = new File(f, "Siedler3_00.dat");
				if (testSndFile.exists()) {
					sndFolder = f;
				}
			} else if (f.getName().equalsIgnoreCase("gfx")) {
				for (DatFileType t : DatFileType.values()) {
					if (new File(f, "siedler3_00" + t.getFileSuffix()).exists()) {
						gfxFolder = f;
					}
				}
			} else if(f.getName().equalsIgnoreCase("maps")) {
				mapsFolder = f;
			}
		}

		return new SettlersFolderInfo(sndFolder, gfxFolder, mapsFolder);
	}

	public static class SettlersFolderInfo {
		public final File sndFolder;
		public final File gfxFolder;
		public final File mapsFolder;

		SettlersFolderInfo() {
			this(null, null, null);
		}

		SettlersFolderInfo(File sndFolder, File gfxFolder, File mapsFolder) {
			this.sndFolder = sndFolder;
			this.gfxFolder = gfxFolder;
			this.mapsFolder = mapsFolder;
		}

		public boolean isValidSettlersFolder() {
			return sndFolder != null && gfxFolder != null;
		}
	}
}
