package jsettlers.graphics.swing.utils;

import java.awt.image.BufferedImage;
import java.nio.ShortBuffer;

import jsettlers.common.Color;
import jsettlers.graphics.image.SingleImage;

public class ImageUtils {
	/**
	 * Converts a single image to a buffered image.
	 * 
	 * @param image
	 *            The image to convert, needs to be loaded.
	 * @return
	 */
	public static BufferedImage convertToBufferedImage(SingleImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		if (width <= 0 || height <= 0) {
			return null;
		}

		BufferedImage rendered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		ShortBuffer data = image.getData().duplicate();
		data.rewind();

		int[] rgbArray = new int[data.remaining()];
		for (int i = 0; i < rgbArray.length; i++) {
			short myColor = data.get();
			rgbArray[i] = Color.convertTo32Bit(myColor);
		}

		rendered.setRGB(0, 0, width, height, rgbArray, 0, width);
		return rendered;

	}
}
