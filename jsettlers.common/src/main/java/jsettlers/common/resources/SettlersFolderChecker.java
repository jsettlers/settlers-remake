/*
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
 */
package jsettlers.common.resources;

import java.io.File;

import jsettlers.common.music.MusicManager;
import jsettlers.common.utils.FileUtils;
import jsettlers.common.utils.mutables.Mutable;

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
	 * @param settlersFolder
	 *            Folder of Settlers III installation
	 * @return Instance of {@link SettlersFolderInfo}.
	 */
	public static SettlersFolderInfo checkSettlersFolder(String settlersFolder) {
		if (settlersFolder == null || settlersFolder.isEmpty()) {
			return new SettlersFolderInfo();
		}

		File settlersBaseFolderFile = new File(settlersFolder);
		return checkSettlersFolder(settlersBaseFolderFile);
	}

	/**
	 * Checks a potential Settlers III installation folder and extracts the gfx and snd folders.
	 *
	 * @param settlersFolder
	 *            Folder of Settlers III installation
	 * @return Instance of {@link SettlersFolderInfo}.
	 */
	public static SettlersFolderInfo checkSettlersFolder(File settlersFolder) {
		if (!settlersFolder.exists() || !settlersFolder.isDirectory()) {
			return new SettlersFolderInfo();
		}

		Mutable<File> sndFolder = new Mutable<>();
		Mutable<File> gfxFolder = new Mutable<>();
		Mutable<File> mapsFolder = new Mutable<>();
		Mutable<File> musicFolder = new Mutable<>();

		FileUtils.iterateChildren(settlersFolder, currentFolder -> {
			if (!currentFolder.isDirectory()) {
				return;
			}

			if (FileUtils.nameEqualsIgnoringCase("SND", currentFolder)) {
				FileUtils.iterateChildren(currentFolder, currentFile -> {
					if (currentFile.isFile() && FileUtils.nameEqualsIgnoringCase("siedler3_00.dat", currentFile)) {
						sndFolder.object = currentFolder;
					}
				});
			} else if (FileUtils.nameEqualsIgnoringCase("GFX", currentFolder)) {
				FileUtils.iterateChildren(currentFolder, currentFile -> {
					String fileName = currentFile.getName();
					String firstPartOfName = fileName.substring(0, fileName.indexOf('.'));
					if (currentFile.isFile() && "siedler3_00".equalsIgnoreCase(firstPartOfName)) {
						gfxFolder.object = currentFolder;
					}
				});
			} else if (FileUtils.nameEqualsIgnoringCase("MAP", currentFolder)) {
				mapsFolder.object = currentFolder;
			} else if (FileUtils.nameEqualsIgnoringCase(MusicManager.ULTIMATE_EDITION_MUSIC_FOLDER_NAME, currentFolder) ||
					FileUtils.nameEqualsIgnoringCase(MusicManager.HISTORY_EDITION_MUSIC_FOLDER_NAME, currentFolder)) {
				musicFolder.object = currentFolder;
			}
		});

		return new SettlersFolderInfo(sndFolder.object, gfxFolder.object, mapsFolder.object, musicFolder.object);
	}

	public static class SettlersFolderInfo {
		public final File sndFolder;
		public final File gfxFolder;
		public final File mapsFolder;
		public final File musicFolder;

		SettlersFolderInfo() {
			this(null, null, null, null);
		}

		SettlersFolderInfo(File sndFolder, File gfxFolder, File mapsFolder, File musicFolder) {
			this.sndFolder = sndFolder;
			this.gfxFolder = gfxFolder;
			this.mapsFolder = mapsFolder;
			this.musicFolder = musicFolder;
		}

		public boolean isValidSettlersFolder() {
			return sndFolder != null && gfxFolder != null;
		}
	}
}
