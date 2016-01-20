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
