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
package go.graphics;

import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * This is the main OpenGL context
 * 
 * @author michael
 */
public interface GLDrawContext {

	/**
	 * Fills a quad.
	 *
	 * @param geometry
	 */
	void fillQuad(GeometryHandle geometry) throws IllegalBufferException;

	/**
	 * Draws a line between an array of points
	 *
	 * @param geometryHandle
	 * @param count
	 * @param loop
	 */
	void drawLine(GeometryHandle geometryHandle, int count, boolean loop) throws IllegalBufferException;

	void glPushMatrix();

	void glTranslatef(float x, float y, float z);

	void glScalef(float x, float y, float z);

	void glPopMatrix();

	/**
	 * Set the color of the context.
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	void color(float red, float green, float blue, float alpha);

	/**
	 * Returns a texture id which is positive or 0. It returns a negative number on error.
	 * 
	 * @param width
	 * @param height
	 *            The height of the image.
	 * @param data
	 *            The data as array. It needs to have a length of width * height and each element is a color with: 5 bits red, 5 bits gree,n, 5 bits
	 *            blue and 1 bit alpha.
	 * @return The id of the generated texture.
	 */
	TextureHandle generateTexture(int width, int height, ShortBuffer data);

	void drawQuadWithTexture(TextureHandle textureid, GeometryHandle geometryindex, int quadOffset) throws IllegalBufferException;

	void drawTrianglesWithTexture(TextureHandle textureid, GeometryHandle geometryindex, int triangleCount) throws IllegalBufferException;

	void drawTrianglesWithTextureColored(TextureHandle textureid, GeometryHandle vertexHandle, GeometryHandle paintHandle, int offset, int lines, int width, int stride) throws IllegalBufferException;

	int makeSideLengthValid(int width);

	void glMultMatrixf(float[] matrix);

	/**
	 * Updates a part of a texture image.
	 * 
	 * @param textureIndex
	 *            The texture to use.
	 * @param left
	 * @param bottom
	 * @param width
	 * @param height
	 * @param data
	 * @throws IllegalBufferException
	 */
	void updateTexture(TextureHandle textureIndex, int left, int bottom, int width, int height, ShortBuffer data) throws IllegalBufferException;

	TextDrawer getTextDrawer(EFontSize size);

	GeometryHandle storeGeometry(float[] geometry);

	void updateGeometryAt(GeometryHandle handle, int pos, ByteBuffer data) throws IllegalBufferException;

	GeometryHandle generateGeometry(int bytes);
}
