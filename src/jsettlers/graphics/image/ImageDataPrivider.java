package jsettlers.graphics.image;

import java.nio.ShortBuffer;

/**
 * Classes implementing this interface are capable of providing image data.
 * <p>
 * The data should not be changed afterwards.
 * 
 * @author michael
 */
public interface ImageDataPrivider {
	/**
	 * Gets the data for the image.
	 * 
	 * @return The image data.
	 */
	ShortBuffer getData();

	/**
	 * Gets the width for the image
	 * 
	 * @return The width as int.
	 */
	int getWidth();

	/**
	 * Gets the height for the image
	 * 
	 * @return The height as int.
	 */
	int getHeight();

	/**
	 * Gets the x offset of the image.
	 * 
	 * @return THe x offset.
	 */
	int getOffsetX();

	/**
	 * Gets the y offset of the image.
	 * 
	 * @return The y offset.
	 */
	int getOffsetY();

}
