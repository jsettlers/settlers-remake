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
package jsettlers.graphics.swing.resources;

import java.io.File;

import jsettlers.graphics.reader.DatFileType;

/**
 * Checks if a settler folder is valid
 * 
 * @author Andreas Butti
 */
public class SettlerFolderCheck {

	/**
	 * Sound folder, if found
	 */
	private File sndFolder;

	/**
	 * Graphics folder if found
	 */
	private File gfxFolder;

	/**
	 * Constructor
	 */
	public SettlerFolderCheck() {
	}

	/**
	 * Checks a folder
	 * 
	 * @param settlersFolder
	 *            Folder
	 * @return true if a valid settler folder
	 */
	public boolean check(String settlersFolder) {
		if (settlersFolder == null) {
			return false;
		}

		if (settlersFolder.isEmpty()) {
			return false;
		}

		File file = new File(settlersFolder);
		if (!file.exists()) {
			return false;
		}

		File[] files = file.listFiles();
		if (files == null) {
			return false;
		}

		boolean sndFolderFound = false;
		boolean gfxFolderFound = false;

		for (File f : files) {
			if (f.getName().toLowerCase().equals("snd")) {
				File testSndFile = new File(f, "Siedler3_00.dat");
				if (testSndFile.exists()) {
					this.sndFolder = f;
					sndFolderFound = true;
				}
			} else if (f.getName().toLowerCase().equals("gfx")) {
				for (DatFileType t : DatFileType.values()) {
					if (new File(f, "siedler3_00" + t.getFileSuffix()).exists()) {
						gfxFolderFound = true;
						this.gfxFolder = f;
					}
				}
			}
		}

		return sndFolderFound && gfxFolderFound;
	}

	/**
	 * Return last result, if check() was true
	 * 
	 * @return Sound folder
	 */
	public File getSndFolder() {
		return sndFolder;
	}

	/**
	 * Return last result, if check() was true
	 * 
	 * @return Graphics folder
	 */
	public File getGfxFolder() {
		return gfxFolder;
	}
}
