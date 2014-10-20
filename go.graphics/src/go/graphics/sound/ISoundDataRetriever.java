package go.graphics.sound;

import java.io.IOException;

/**
 * Implementors of this interface are used to load the sound data.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ISoundDataRetriever {
	/**
	 * Loads the sound data for the sound given by soundStart
	 * 
	 * @param soundStart
	 *            start byte of the sound
	 * @return sound data as short array.
	 * @throws IOException
	 *             may be thrown if the file can not be accessed.
	 */
	short[] getSoundData(int soundStart) throws IOException;
}
