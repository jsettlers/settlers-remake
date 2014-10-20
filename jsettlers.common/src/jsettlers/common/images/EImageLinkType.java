package jsettlers.common.images;

/**
 * This is the type of an image link.
 * 
 * @author michael
 */
public enum EImageLinkType {
	/**
	 * A normal settler sequence
	 */
	SETTLER,
	/**
	 * A gui image. The sequence index is ignored and should be 0.
	 * <p>
	 * Shadow and torso is automatically added.
	 */
	GUI,
	/**
	 * A landscape image. The sequence index is ignored and should be 0.
	 */
	LANDSCAPE
}
