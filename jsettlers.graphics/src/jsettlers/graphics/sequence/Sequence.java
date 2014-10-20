package jsettlers.graphics.sequence;

import jsettlers.graphics.image.Image;

public interface Sequence<T extends Image> {

	/**
	 * Gets the length of the sequence.
	 * 
	 * @return The number of images in the seuqence.
	 */
	public abstract int length();

	/**
	 * Gets a image at a given position.
	 * 
	 * @param index
	 *            The image index in the sequence.
	 * @return The image.
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of the ounds of the base array.
	 */
	public abstract T getImage(int index);

	/**
	 * Gets a image at a given position.
	 * 
	 * @param index
	 *            The image index in the sequence.
	 * @return The image, or a null image if the index is out of bounds
	 */
	public abstract Image getImageSafe(int index);

}