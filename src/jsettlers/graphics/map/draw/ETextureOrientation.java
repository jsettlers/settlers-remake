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

	static final int CONTINOUS_SIZE = 128;

	private final boolean continous;
	private final float[] relativecoords;

	private ETextureOrientation(boolean continous, float[] relativecoords) {
		this.continous = continous;
		this.relativecoords = relativecoords;
	}

	public boolean isContinous() {
	    return continous;
    }

	public float[] getRelativecoords() {
	    return relativecoords;
    }
}
