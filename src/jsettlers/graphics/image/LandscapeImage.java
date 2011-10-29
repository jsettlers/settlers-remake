package jsettlers.graphics.image;


/**
 * Class to draw triangles of landscape images.
 * <p>
 * There are 2 types of images: <br>
 * Some images are big and continuous, so they are just drawn and wrapped at the
 * end. <br>
 * Other images only consist of 6 triangles indicating a border between two
 * terrian types in all 6 directions.
 * <p>
 * You have to do the drawing yourself, but there are helper functions that help
 * you:
 * <p>
 * bind() activates drawing the texture.
 * 
 * @author michael
 */
public class LandscapeImage extends Image {
	/**
	 * States that you request the image in the top right border of the texture.
	 */
	public static final int TRI_TOPRIGHT = 0;
	public static final int TRI_BOTTOMRIGHT = 1;
	public static final int TRI_BOTTOM = 2;
	public static final int TRI_BOTTOMLEFT = 3;
	public static final int TRI_TOP = 4;
	public static final int TRI_TOPLEFT = 5;

	public LandscapeImage(ImageDataPrivider data) {
		super(data);
	}

	/**
	 * Checks whether the given image is a continous image, that means it can be
	 * repeated when drawing.
	 * 
	 * @return If the image is continuous.
	 */
	public boolean isContinuous() {
		return this.width > 50 && this.height > 50;
	}

}
