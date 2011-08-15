package jsettlers.graphics.sequence;

import java.util.Arrays;
import java.util.Iterator;

import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.NullImage;

/**
 * This class defines an image sequence.
 * 
 * @author michael
 * @param <T>
 */
public final class Sequence<T extends Image> implements Iterable<T> {
	private static Sequence<Image> nullSequence;
	private final T[] images;

	/**
	 * Creates a new sequence.
	 * 
	 * @param images
	 *            The images for the sequence.
	 */
	public Sequence(T[] images) {
		this.images = images;
	}

	/**
	 * Gets the length of the sequence.
	 * 
	 * @return The number of images in the seuqence.
	 */
	public int length() {
		return this.images.length;
	}

	/**
	 * Gets a image at a given position.
	 * 
	 * @param index
	 *            The image index in the sequence.
	 * @return The image.
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of the ounds of the base array.
	 */
	public T getImage(int index) {
			return this.images[index];
		
	}

	/**
	 * Gets a image at a given position.
	 * 
	 * @param index
	 *            The image index in the sequence.
	 * @return The image, or a null image if the index is out of bounds
	 */
	public Image getImageSafe(int index) {
		if (index >= 0 && index < this.images.length) {
			return this.images[index];
		} else {
			return NullImage.getInstance();
		}
	}

	@Override
	public Iterator<T> iterator() {
		return Arrays.asList(this.images).iterator();
	}

	/**
	 * Gets an empty sequence.
	 * 
	 * @return The emepty sequence.
	 */
	public static Sequence<Image> getNullSequence() {
		if (nullSequence == null) {
			nullSequence = new Sequence<Image>(new Image[0]);
		}
		return nullSequence;
	}
}
