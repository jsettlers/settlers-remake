package jsettlers.lookandfeel.ui.img;

import jsettlers.lookandfeel.DrawHelper;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Image loader for UI Images
 * 
 * @author Andreas Butti
 */
public final class UiImageLoader {

	/**
	 * Cached images
	 */
	private static HashMap<String, BufferedImage> cache = new HashMap<>();

	/**
	 * Utility class
	 */
	private UiImageLoader() {
	}

	/**
	 * Get or load an image
	 * 
	 * @param name
	 *            Name of the image to load
	 * @return Image, do not change!
	 */
	public static BufferedImage get(String name) {
		BufferedImage img = cache.get(name);
		if (img != null) {
			return img;
		}

		img = DrawHelper.toBufferedImage(new ImageIcon(UiImageLoader.class.getResource(name)).getImage());

		cache.put(name, img);
		return img;
	}
}
