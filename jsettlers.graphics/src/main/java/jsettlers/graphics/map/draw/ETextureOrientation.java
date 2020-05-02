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
public class ETextureOrientation {

	/**
	 * The triangle consists of these points: The upper point,
	 * <p>
	 * the left point
	 * <p>
	 * the right point
	 */
	private static final float[] CONTINUOUS_UP = new float[] {
			DrawConstants.TEXTUREUNIT_X / 2,
			0,
			0,
			DrawConstants.TEXTUREUNIT_Y,
			DrawConstants.TEXTUREUNIT_X,
			DrawConstants.TEXTUREUNIT_Y
	};

	private static final float[] TOPLEFT = new float[] {
			DrawConstants.TEXTUREUNIT_X / 2
					+ DrawConstants.TEXTURE_BORDER_OFFSET,
			0,
			0 + DrawConstants.TEXTURE_BORDER_OFFSET,
			DrawConstants.TEXTUREUNIT_Y,
			DrawConstants.TEXTUREUNIT_X,
			DrawConstants.TEXTUREUNIT_Y
	};

	private static final float[] TOPRIGHT = new float[] {
			DrawConstants.TEXTUREUNIT_X * 3 / 2
					- DrawConstants.TEXTURE_BORDER_OFFSET,
			0,
			DrawConstants.TEXTUREUNIT_X - DrawConstants.TEXTURE_BORDER_OFFSET,
			DrawConstants.TEXTUREUNIT_Y,
			DrawConstants.TEXTUREUNIT_X * 2
					- DrawConstants.TEXTURE_BORDER_OFFSET,
			DrawConstants.TEXTUREUNIT_Y
	};

	private static final float[] BOTTOM = new float[] {
			DrawConstants.TEXTUREUNIT_X,
			DrawConstants.TEXTUREUNIT_Y,
			DrawConstants.TEXTUREUNIT_X / 2,
			DrawConstants.TEXTUREUNIT_Y * 2,
			DrawConstants.TEXTUREUNIT_X * 3 / 2,
			DrawConstants.TEXTUREUNIT_Y * 2
	};
	/**
	 * left
	 * <p>
	 * bottom
	 * <p>
	 * right
	 */
	private static final float[] CONTINUOUS_DOWN = new float[] {
			DrawConstants.TEXTUREUNIT_X / 2,
			0,
			DrawConstants.TEXTUREUNIT_X,
			DrawConstants.TEXTUREUNIT_Y,
			DrawConstants.TEXTUREUNIT_X * 3 / 2,
			0
	};

	private static final float[] TOP = new float[] {
			DrawConstants.TEXTUREUNIT_X / 2,
			0,
			DrawConstants.TEXTUREUNIT_X,
			DrawConstants.TEXTUREUNIT_Y,
			DrawConstants.TEXTUREUNIT_X * 3 / 2,
			0
	};

	private static final float[] BOTTOMLEFT = new float[] {
			0 + DrawConstants.TEXTURE_BORDER_OFFSET,
			DrawConstants.TEXTUREUNIT_Y,
			DrawConstants.TEXTUREUNIT_X / 2 + DrawConstants.TEXTURE_BORDER_OFFSET,
			DrawConstants.TEXTUREUNIT_Y * 2,
			DrawConstants.TEXTUREUNIT_X + DrawConstants.TEXTURE_BORDER_OFFSET,
			DrawConstants.TEXTUREUNIT_Y
	};

	private static final float[] BOTTOMRIGHT = new float[] {
			DrawConstants.TEXTUREUNIT_X - DrawConstants.TEXTURE_BORDER_OFFSET,
			DrawConstants.TEXTUREUNIT_Y,
			DrawConstants.TEXTUREUNIT_X * 3 / 2 - DrawConstants.TEXTURE_BORDER_OFFSET,
			DrawConstants.TEXTUREUNIT_Y * 2,
			DrawConstants.TEXTUREUNIT_X * 2 - DrawConstants.TEXTURE_BORDER_OFFSET,
			DrawConstants.TEXTUREUNIT_Y
	};

	public static final float[][] CONTINUOS = {CONTINUOUS_UP, CONTINUOUS_DOWN};
	public static final float[][] ORIENTATION = {BOTTOM, TOP};
	public static final float[][] RIGHT = {TOPRIGHT, BOTTOMRIGHT};
	public static final float[][] LEFT = {TOPLEFT, BOTTOMLEFT};
}
