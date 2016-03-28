/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.graphics.map.draw;

/**
 * All texture types a texture can have. This is used for the background.
 * 
 * @author Michael Zangl
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
			DrawConstants.TEXTUREUNIT_X / 2
					+ DrawConstants.TEXTURE_BORDER_OFFSET,
			0,
			0 + DrawConstants.TEXTURE_BORDER_OFFSET,
			DrawConstants.TEXTUREUNIT_Y,
			DrawConstants.TEXTUREUNIT_X,
			DrawConstants.TEXTUREUNIT_Y
	}),

	TOPRIGHT(false, new float[] {
			DrawConstants.TEXTUREUNIT_X * 3 / 2
					- DrawConstants.TEXTURE_BORDER_OFFSET,
			0,
			DrawConstants.TEXTUREUNIT_X - DrawConstants.TEXTURE_BORDER_OFFSET,
			DrawConstants.TEXTUREUNIT_Y,
			DrawConstants.TEXTUREUNIT_X * 2
					- DrawConstants.TEXTURE_BORDER_OFFSET,
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
	}),
	BOTTOMLEFT(false, new float[] {
			0 + DrawConstants.TEXTURE_BORDER_OFFSET,
			DrawConstants.TEXTUREUNIT_Y,
			DrawConstants.TEXTUREUNIT_X / 2 + DrawConstants.TEXTURE_BORDER_OFFSET,
			DrawConstants.TEXTUREUNIT_Y * 2,
			DrawConstants.TEXTUREUNIT_X + DrawConstants.TEXTURE_BORDER_OFFSET,
			DrawConstants.TEXTUREUNIT_Y
	}),
	BOTTOMRIGHT(false, new float[] {
			DrawConstants.TEXTUREUNIT_X - DrawConstants.TEXTURE_BORDER_OFFSET,
			DrawConstants.TEXTUREUNIT_Y,
			DrawConstants.TEXTUREUNIT_X * 3 / 2 - DrawConstants.TEXTURE_BORDER_OFFSET,
			DrawConstants.TEXTUREUNIT_Y * 2,
			DrawConstants.TEXTUREUNIT_X * 2 - DrawConstants.TEXTURE_BORDER_OFFSET,
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
