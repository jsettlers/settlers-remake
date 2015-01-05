package go.graphics.sound;

public interface SoundPlayer {
	/**
	 * Starts to play a sound.
	 * <p>
	 * Blocks until the sound is played.
	 * 
	 * @param soundStart
	 * @param lvolume
	 * @param rvolume
	 */
	public void playSound(int soundStart, float lvolume, float rvolume);

	public void setSoundDataRetriever(ISoundDataRetriever soundDataRetriever);

}
