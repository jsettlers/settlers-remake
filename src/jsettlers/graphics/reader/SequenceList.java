package jsettlers.graphics.reader;

import jsettlers.graphics.image.Image;
import jsettlers.graphics.sequence.Sequence;

/**
 * This is a list of image sequences.
 * 
 * @author michael
 * @param <T>
 *            The type of the images.
 */
public interface SequenceList<T extends Image> {
	/**
	 * Gets an image in the sequence.
	 * 
	 * @param index
	 *            The index of the image.
	 * @return The image in the sequence
	 * @throws IndexOutOfBoundsException
	 *             if the index is >= size of smaller than 0.
	 */
	Sequence<T> get(int index);

	/**
	 * Gets the length of the sequence.
	 * 
	 * @return The number of images in this sequence.
	 */
	int size();
}
