package jsettlers.graphics.image;

import go.graphics.GLDrawContext;

import java.awt.Color;
import java.nio.ShortBuffer;

/**
 * This is a null image.
 * <p>
 * It may be returned by image methods if the requested image is not available.
 * 
 * @author michael
 */
public final class NullImage extends Image {
	private static final int HALFSIZE = 3;
	private static NullImage instance;

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
				-HALFSIZE, -HALFSIZE, 0,
				+HALFSIZE, -HALFSIZE,0,
				+HALFSIZE, +HALFSIZE,0,
				-HALFSIZE, +HALFSIZE,0,
		}, true);
	}
}
