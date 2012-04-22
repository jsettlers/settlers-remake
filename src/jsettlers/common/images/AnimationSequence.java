package jsettlers.common.images;

/**
 * This is a definition for a sequence of images.
 * 
 * @author michael
 */
public final class AnimationSequence {
	private final String name;
	private final int first;
	private final int length;

	public AnimationSequence(String name, int first, int length) {
		super();
		this.name = name;
		this.first = first;
		this.length = length;
	}

	/**
	 * @return the name to use for image links.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the first index in the sequence
	 */
	public int getFirst() {
		return first;
	}

	/**
	 * @return the length of the sequence
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Gets the n-th image in this sequence, starting by 0. If the parameter is
	 * bigger than the length of the sequence or less than 0, the result is
	 * undefined.
	 * 
	 * @param index
	 * @return
	 */
	public ImageLink getImage(int index) {
		return ImageLink.fromName(name, first + index);
	}
}
