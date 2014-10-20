package jsettlers.graphics.reader;

import java.io.IOException;

/**
 * This class gives us a place to store our image
 * 
 * It is not thread safe.
 * 
 * It internally holds a write pointer, and may be backed by an array or a buffer.
 * 
 * startImage ist always called first, then writeLine for each line.
 * 
 * @author michael
 *
 */
public interface ImageArrayProvider {
	/**
	 * Starts a new image.
	 * @param width May be 0!
	 * @param height May be 0!
	 * @throws IOException
	 */
	public void startImage(int width, int height) throws IOException;
	public void writeLine(short[] data, int length) throws IOException;
}
