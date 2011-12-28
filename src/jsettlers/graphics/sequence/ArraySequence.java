package jsettlers.graphics.sequence;

import java.util.Arrays;
import java.util.Iterator;

import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.image.NullImage;

/**
 * This class defines an image sequence.
 * 
 * @author michael
 * @param <T>
 */
public final class ArraySequence<T extends Image> implements Iterable<T>, Sequence<T> {
	private static Sequence<SingleImage> nullSequence;
	private final T[] images;

	/**
	 * Creates a new sequence.
	 * 
	 * @param images
	 *            The images for the sequence.
	 */
	public ArraySequence(T[] images) {
		this.images = images;
	}

	/* (non-Javadoc)
     * @see jsettlers.graphics.sequence.Sequence#length()
     */
	@Override
    public int length() {
		return this.images.length;
	}

	/* (non-Javadoc)
     * @see jsettlers.graphics.sequence.Sequence#getImage(int)
     */
	@Override
    public T getImage(int index) {
			return this.images[index];
		
	}

	/* (non-Javadoc)
     * @see jsettlers.graphics.sequence.Sequence#getImageSafe(int)
     */
	@Override
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
	public static Sequence<SingleImage> getNullSequence() {
		if (nullSequence == null) {
			nullSequence = new ArraySequence<SingleImage>(new SingleImage[0]);
		}
		return nullSequence;
	}
}
