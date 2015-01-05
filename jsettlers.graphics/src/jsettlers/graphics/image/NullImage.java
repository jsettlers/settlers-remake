package jsettlers.graphics.image;

import go.graphics.GLDrawContext;

import java.nio.ShortBuffer;

import jsettlers.common.Color;
import jsettlers.graphics.reader.ImageMetadata;

/**
 * This is a null image.
 * <p>
 * It may be returned by image methods if the requested image is not available.
 * 
 * @author michael
 */
public class NullImage extends SingleImage {
	private static final short[] NULL_DATA = new short[] {
			0
	};
	private static final int HALFSIZE = 3;
	private static NullImage instance;
	private static LandscapeImage landscapeinstance = null;
	private static final ImageMetadata nullImageMeta = new ImageMetadata();

	static {
		nullImageMeta.width = 1;
		nullImageMeta.height = 1;
		nullImageMeta.offsetX = 0;
		nullImageMeta.offsetY = 0;
	}

	/**
	 * Gets an instance of the null image.
	 * 
	 * @return An instance.
	 */
	public static NullImage getInstance() {
		if (instance == null) {
			instance = new NullImage();
		}
		return instance;
	}

	private NullImage() {
		super(ShortBuffer.allocate(1), 1, 1, 0, 0);
	}

	@Override
	public void draw(GLDrawContext gl, Color color) {
		gl.color(1, 1, 1, 0.5f);
		gl.fillQuad(-HALFSIZE, -HALFSIZE, HALFSIZE, HALFSIZE);

		gl.color(1, 0, 0, 1);
		gl.drawLine(new float[] {
				-HALFSIZE,
				-HALFSIZE,
				0,
				+HALFSIZE,
				-HALFSIZE,
				0,
				+HALFSIZE,
				+HALFSIZE,
				0,
				-HALFSIZE,
				+HALFSIZE,
				0,
		}, true);
	}

	private static GuiImage guiinstance;

	/**
	 * Gets an empty landscape image.
	 * 
	 * @return The imge instance.
	 */
	public static LandscapeImage getForLandscape() {
		if (landscapeinstance == null) {
			landscapeinstance = new LandscapeImage(nullImageMeta, NULL_DATA);
		}
		return landscapeinstance;
	}

	/**
	 * Gets an empty gui image.
	 * 
	 * @return The imge instance.
	 */
	public static GuiImage getForGui() {
		if (guiinstance == null) {
			guiinstance = new GuiImage(nullImageMeta, NULL_DATA);
		}
		return guiinstance;
	}
}
