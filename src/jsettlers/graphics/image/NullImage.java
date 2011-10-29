package jsettlers.graphics.image;

import go.graphics.Color;
import go.graphics.GLDrawContext;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * This is a null image.
 * <p>
 * It may be returned by image methods if the requested image is not available.
 * 
 * @author michael
 */
public class NullImage extends Image {
	private static final int HALFSIZE = 3;
	private static NullImage instance;
	private static LandscapeImage landscapeinstance = null;

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

	private static ImageDataPrivider nullproivder = new ImageDataPrivider() {
		@Override
		public int getWidth() {
			return 1;
		}

		@Override
		public int getOffsetY() {
			return 0;
		}

		@Override
		public int getOffsetX() {
			return 0;
		}

		@Override
		public int getHeight() {
			return 1;
		}

		@Override
		public ShortBuffer getData() {
			ByteBuffer data = ByteBuffer.allocateDirect(2);
			data.order(ByteOrder.nativeOrder());
			data.putShort((short) 0x0);
			data.rewind();
			return data.asShortBuffer();
		}
	};

	private static GuiImage guiinstance;

	/**
	 * Gets an empty landscape image.
	 * @return The imge instance.
	 */
	public static LandscapeImage getForLandscape() {
		if (landscapeinstance == null) {
			landscapeinstance = new LandscapeImage(nullproivder);
		}
		return landscapeinstance;
	}

	/**
	 * Gets an empty gui image.
	 * @return The imge instance.
	 */
	public static GuiImage getForGui() {
		if (guiinstance == null) {
			guiinstance = new GuiImage(nullproivder);
		}
		return guiinstance;
	}
}
