package jsettlers.common.music;

import jsettlers.common.player.ECivilisation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * music management of different s3 versions, provides CIVILISATION specific music management
 *
 * TODO: PC only so far, add ingame gui config, validate music files and integrate exception handling
 *
 * @author MarviMarv
 */
public final class MusicManager {

	public final static String ULTIMATE_EDITION_MUSIC_FOLDER_NAME = "MUSIC";
	public final static String HISTORY_EDITION_MUSIC_FOLDER_NAME = "Theme";

	private final static String ULTIMATE_EDITION_FILE_TYPE = "ogg";
	private final static String HISTORY_EDITION_FILE_TYPE = "mp3";

	private final static String MUSIC_FILE_PREFIX = "Track";

	// private final static int[] CIVILISATION = {0, 1, 2, 3, 4}; //roman, egyptian, asian, amazon, unknown
	private final static String[][] ULTIMATE_EDITION_MUSIC_SET = { { "02", "03", "12" }, { "06", "07", "14" }, { "04", "05", "13" }, { "08", "09", "10" }, { "11" } };
	private final static String[][] HISTORY_EDITION_MUSIC_SET = { { "02", "03", "04" }, { "05", "06", "07" }, { "08", "09", "10" }, { "13", "14", "15" }, { "11", "12" } };

	private final static int MUSIC_VOLUME = 50;

	private static File lookupPath;
	private static String fileType;
	private static String[][] musicSet;

	private static MusicThread musicThread;
	private static MusicPlayer musicPlayer;

	static {
		MusicManager.lookupPath = null;
		MusicManager.fileType = null;
		MusicManager.musicSet = null;

		MusicManager.musicThread = null;
		musicPlayer = new MusicPlayer();
	}

	public static void setLookupPath(final File lookupPath) {
		boolean valid = false;

		if (lookupPath.getName().equals(MusicManager.HISTORY_EDITION_MUSIC_FOLDER_NAME)) {
			MusicManager.fileType = MusicManager.HISTORY_EDITION_FILE_TYPE;
			MusicManager.musicSet = MusicManager.HISTORY_EDITION_MUSIC_SET;
			valid = true;
		} else if (lookupPath.getName().equals(MusicManager.ULTIMATE_EDITION_MUSIC_FOLDER_NAME)) {
			MusicManager.fileType = MusicManager.ULTIMATE_EDITION_FILE_TYPE;
			MusicManager.musicSet = MusicManager.ULTIMATE_EDITION_MUSIC_SET;
			valid = true;
		} else {
			// throw new Exception("invalid music folder");
		}

		if (valid) {
			MusicManager.lookupPath = lookupPath;
		}
	}

	public static void startMusicThread(final ECivilisation civilisation, final boolean playAll) {
		if (MusicManager.lookupPath != null) {
			MusicManager.musicThread = new MusicThread(
					musicPlayer,
					MusicManager.MUSIC_VOLUME,
					MusicManager.lookupPath.getAbsolutePath() + "\\" + MusicManager.MUSIC_FILE_PREFIX,
					MusicManager.fileType,
					MusicManager.assembleMusicSet(civilisation, playAll));

			MusicManager.musicThread.start();
		}
	}

	public static void stopMusicThread() {
		if (MusicManager.lookupPath != null) {
			MusicManager.musicThread.cancel();
		}
	}

	public static void setMusicVolume(final int volume) {
		if (MusicManager.lookupPath != null) {
			musicPlayer.setVolume(volume);
		}
	}

	private static String[] assembleMusicSet(final ECivilisation civilisation, final boolean playAll) {
		List<String> list = new ArrayList<String>();

		if (playAll) {
			for (int i = 0; i < MusicManager.musicSet.length; i++) {
				list.addAll(Arrays.asList(MusicManager.musicSet[i]));
			}
		} else {
			list.addAll(Arrays.asList(MusicManager.musicSet[civilisation.getFileIndex() - 1]));
		}

		// shuffle list so we don't get bored :)
		Collections.shuffle(list);

		return list.toArray(new String[0]);
	}
}
