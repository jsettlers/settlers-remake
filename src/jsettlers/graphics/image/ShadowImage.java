package jsettlers.graphics.image;

import jsettlers.graphics.reader.ImageMetadata;

/**
 * This is a shadow image.
 * 
 * @author michael
 */
public class ShadowImage extends SingleImage {

	/**
	 * Generates a shadow image.
	 * 
	 * @param data
	 *            The data to use.
	 */
	public ShadowImage(ImageMetadata metadata, short[] data) {
		super(metadata, data);
	}
}
