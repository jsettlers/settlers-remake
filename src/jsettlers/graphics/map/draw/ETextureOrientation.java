package jsettlers.graphics.map.draw;

/**
 * All texture types a texture can have.
 * 
 * @author michael
 */
public enum ETextureOrientation {

	/**
	 * The triangle consists of these points: The upper point,
	 * <p>
	 * the left point
	 * <p>
	 * the right point
	 */
	CONTINUOUS_UP(true, new float[] {
	        DrawConstants.DISTANCE_X / 2,
	        0,
	        0,
	        DrawConstants.DISTANCE_Y,
	        DrawConstants.DISTANCE_X,
	        DrawConstants.DISTANCE_Y
	}),

	TOPLEFT(false, new float[] {
	        DrawConstants.TEXTUREUNIT_X / 2,
	        0,
	        0,
	        DrawConstants.TEXTUREUNIT_Y,
	        DrawConstants.TEXTUREUNIT_X,
	        DrawConstants.TEXTUREUNIT_Y
	}),

	TOPRIGHT(false, new float[] {
	        DrawConstants.TEXTUREUNIT_X * 3 / 2,
	        0,
	        DrawConstants.TEXTUREUNIT_X,
	        DrawConstants.TEXTUREUNIT_Y,
	        DrawConstants.TEXTUREUNIT_X * 2,
	        DrawConstants.TEXTUREUNIT_Y
	}),

	BOTTOM(false, new float[] {
	        DrawConstants.TEXTUREUNIT_X,
	        DrawConstants.TEXTUREUNIT_Y,
	        DrawConstants.TEXTUREUNIT_X / 2,
	        DrawConstants.TEXTUREUNIT_Y * 2,
	        DrawConstants.TEXTUREUNIT_X * 3 / 2,
	        DrawConstants.TEXTUREUNIT_Y * 2
	}),
	/**
	 * left
	 * <p>
	 * bottom
	 * <p>
	 * right
	 */
	CONTINUOUS_DOWN(true, new float[] {
	        DrawConstants.DISTANCE_X / 2,
	        0,
	        DrawConstants.DISTANCE_X,
	        DrawConstants.DISTANCE_Y,
	        DrawConstants.DISTANCE_X * 3 / 2,
	        0
	}),

	TOP(false, new float[] {
	        DrawConstants.TEXTUREUNIT_X / 2,
	        0,
	        DrawConstants.TEXTUREUNIT_X,
	        DrawConstants.TEXTUREUNIT_Y,
	        DrawConstants.TEXTUREUNIT_X * 3 / 2,
	        0
	}), BOTTOMLEFT(false, new float[] {
	        0,
	        DrawConstants.TEXTUREUNIT_Y,
	        DrawConstants.TEXTUREUNIT_X / 2,
	        DrawConstants.TEXTUREUNIT_Y * 2,
	        DrawConstants.TEXTUREUNIT_X,
	        DrawConstants.TEXTUREUNIT_Y
	}), BOTTOMRIGHT(false, new float[] {
	        DrawConstants.TEXTUREUNIT_X,
	        DrawConstants.TEXTUREUNIT_Y,
	        DrawConstants.TEXTUREUNIT_X * 3 / 2,
	        DrawConstants.TEXTUREUNIT_Y * 2,
	        DrawConstants.TEXTUREUNIT_X * 2,
	        DrawConstants.TEXTUREUNIT_Y
	});

	private static final int CONTINOUS_SIZE = 128;

	private final boolean continous;
	private final float[] relativecoords;

	private ETextureOrientation(boolean continous, float[] relativecoords) {
		this.continous = continous;
		this.relativecoords = relativecoords;
	}

	public void addCoordsTo(float[] array, int offset, int stride, int x,
	        int y, int dx, int dy, int texturesize) {
		int adddx = 0;
		int adddy = 0;
		if (continous) {
			adddx = x * DrawConstants.DISTANCE_X - y * DrawConstants.DISTANCE_X / 2;
			adddy = y * DrawConstants.DISTANCE_Y;
			adddx = realModulo(adddx, CONTINOUS_SIZE);
			adddy = realModulo(adddy, CONTINOUS_SIZE);
		}
		adddx += dx;
		adddy += dy;

		array[offset] = (relativecoords[0] + adddx) / (float) texturesize;
		array[offset + 1] = (relativecoords[1] + adddy) / (float) texturesize;
		array[offset + stride] =
		        (relativecoords[2] + adddx) / (float) texturesize;
		array[offset + stride + 1] =
		        (relativecoords[3] + adddy) / (float) texturesize;
		array[offset + 2 * stride] =
		        (relativecoords[4] + adddx) / (float) texturesize;
		array[offset + 2 * stride + 1] =
		        (relativecoords[5] + adddy) / (float) texturesize;
	}

	private int realModulo(int number, int modulo) {
		if (number >= 0) {
			return number % modulo;
		} else {
			return number % modulo + modulo;
		}
	}
}
