package go.graphics.sound;

public interface SoundPlayer {
	/**
	 * Starts to play a sound.
	 * <p>
	 * Blocks until the sound is played.
	 * @param sound
	 * @param lvolume
	 * @param rvolume
	 */
	public void playSound(int sound, float lvolume, float rvolume);

	/**
	 * Loads a signed 16 bit little endian stram from the sound file.
	 * <p>
	 * This method will block until the sound is loaded.
	 * @param sndfile The file to load
	 * @param leftstart The start byte offset of left 
	 * @param rightstart 
	 * @param length The length in samples
	 * @return An integer used to refer to playSound()
	 */
	//public int load(File sndfile, int leftstart, int rightstart, int length);

	public int load(short[] loadSound);
}
