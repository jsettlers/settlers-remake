package jsettlers.common.music;

/**
 *
 * Background thread for ingame music
 *
 * @author MarviMarv
 */
public class MusicThread implements Runnable {
	private final Thread musicThread;
	private final MusicPlayer musicPlayer;
	private final int startVolume;

	private final String musicFilePath;
	private final String musicFileType;
	private final String[] musicTracks;

	private boolean canceled;

	public MusicThread(final MusicPlayer musicPlayer, final int volume, final String musicFilePath, final String musicFileType, final String[] musicTracks) {
		canceled = false;

		this.musicPlayer = musicPlayer;
		this.startVolume = volume;
		this.musicFilePath = musicFilePath;
		this.musicFileType = musicFileType;
		this.musicTracks = musicTracks;

		musicThread = new Thread(this);
		musicThread.setName("MusicThread");
		musicThread.setDaemon(true);
	}

	@Override
	public void run() {
		int trackIndex = 0;

		while (!canceled) {

			musicPlayer.play(musicFilePath + musicTracks[trackIndex] + "." + musicFileType, startVolume);
			trackIndex++;

			if (trackIndex == musicTracks.length) {
				trackIndex = 0;
			}
		}
	}

	public void start() {
		musicThread.start();
	}

	public void cancel() {
		canceled = true;
		musicPlayer.stop();
		musicThread.interrupt();
	}
}