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
 * Some drawing constants for the background / grid sizes.
 * 
 * @author michael
 *
 */
public final class DrawConstants {
	/**
	 * X distance of two tiles on the screen.
	 */
	public static final int DISTANCE_X = 16;
	/**
	 * Y distance of two tiles on the screen.
	 */
	public static final int DISTANCE_Y = 9;
	/**
	 * X height of a single texture piece.
	 */
	public static final int TEXTUREUNIT_X = 16;
	/**
	 * Y height of a single texture piece.
	 */
	public static final int TEXTUREUNIT_Y = 16;
	/**
	 * Offset each texture should have, in pixels. This is half a pixel to align them with the screen.
	 */
	public static final float TEXTURE_BORDER_OFFSET = 0.5f;

	private DrawConstants() {
	}
}
