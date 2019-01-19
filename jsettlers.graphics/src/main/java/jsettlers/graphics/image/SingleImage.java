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
package jsettlers.graphics.image;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import go.graphics.EGeometryFormatType;
import go.graphics.EGeometryType;
import go.graphics.GLDrawContext;
import go.graphics.GeometryHandle;
import go.graphics.IllegalBufferException;
import go.graphics.SharedGeometry;
import go.graphics.TextureHandle;

import java.awt.image.BufferedImage;

import jsettlers.common.Color;
import jsettlers.graphics.image.reader.ImageMetadata;

/**
 * This is the base for all images that are directly loaded from the image file.
 * <p>
 * This class interprets the image data in 5-5-5-1-Format. To change the interpretation, it is possible to subclass this class.
 *
 * @author Michael Zangl
 */
public class SingleImage extends Image implements ImageDataPrivider {

	protected ShortBuffer data, tdata;
	protected final int width;
	protected final int height;
	protected int twidth, theight, toffsetX, toffsetY;
	protected final int offsetX;
	protected final int offsetY;
	protected String name;

	protected TextureHandle texture = null;
	protected SharedGeometry.SharedGeometryHandle geometryIndex = null;

	/**
	 * Creates a new image by the given buffer.
	 *
	 * @param data
	 * 		The data buffer for the image with an unspecified color format.
	 * @param width
	 * 		The width.
	 * @param height
	 * 		The height.
	 * @param offsetX
	 * 		The x offset of the image.
	 * @param offsetY
	 * 		The y offset of the image.
	 */
	protected SingleImage(ShortBuffer data, int width, int height, int offsetX,
			int offsetY, String name) {
		this.data = tdata = data;
		this.width = twidth = width;
		this.height = theight = height;
		this.offsetX = toffsetX = offsetX;
		this.offsetY = toffsetY = offsetY;
		this.name = name;
	}

	/**
	 * Creates a new image by linking this images data to the data of the provider.
	 *
	 * @param metadata
	 * 		The mata data to use.
	 * @param data
	 * 		The data to use.
	 */
	public SingleImage(ImageMetadata metadata, short[] data, String name) {
		this(ShortBuffer.wrap(data), metadata.width, metadata.height, metadata.offsetX, metadata.offsetY, name);
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public int getOffsetX() {
		return this.offsetX;
	}

	@Override
	public int getOffsetY() {
		return this.offsetY;
	}

	@Override
	public void drawImageAtRect(GLDrawContext gl, float x, float y, float width, float height) {
		try {
			checkStaticHandles(gl);
			gl.draw2D(rectHandle.geometry, texture, EGeometryType.Quad, rectHandle.index, 4, x, y, 0, twidth/this.width*width, theight/this.height*height, 0, null, 1);
		} catch (IllegalBufferException e) {
			handleIllegalBufferException(e);
		}
	}

	@Override
	public ShortBuffer getData() {
		return this.data;
	}

	@Override
	public void drawOnlyImageAt(GLDrawContext gl, float x, float y, float z, Color torsoColor, float fow) {
		try {
			checkHandles(gl);
			gl.draw2D(geometryIndex.geometry, texture, EGeometryType.Quad, geometryIndex.index, 4, x, y, z, 1, 1, 1, null, 1);
		} catch (IllegalBufferException e) {
			handleIllegalBufferException(e);
		}
	}

	protected void checkHandles(GLDrawContext gl) throws IllegalBufferException {
		if (texture == null || !texture.isValid()) {
			texture = gl.generateTexture(twidth, theight, tdata, name);
		}

		if(geometryIndex == null || SharedGeometry.isInvalid(gl, geometryIndex)) {
			geometryIndex = SharedGeometry.addGeometry(gl, getGeometry());
		}
	}

	private void checkStaticHandles(GLDrawContext gl) throws IllegalBufferException {
		checkHandles(gl);
		if(buildHandle == null || !buildHandle.isValid()) {
			buildHandle = gl.generateGeometry(3, EGeometryFormatType.Texture2D, true, "building-progress");
		}
		if(rectHandle == null || SharedGeometry.isInvalid(gl, rectHandle)) {
			rectHandle = SharedGeometry.addGeometry(gl, SharedGeometry.createQuadGeometry(0, 1, 1, 0, 0, 0, 1, 1));
		}
	}

	protected float[] getGeometry() {
		return SharedGeometry.createQuadGeometry(toffsetX, -toffsetY, toffsetX + twidth, -toffsetY - theight, 0, 0, 1, 1);
	}

	protected void setGeometry(SharedGeometry.SharedGeometryHandle geometry) {
		geometryIndex = geometry;
	}

	private static GeometryHandle buildHandle = null;
	private static SharedGeometry.SharedGeometryHandle rectHandle = null;
	private static final ByteBuffer buildBfr = ByteBuffer.allocateDirect(4*4*3).order(ByteOrder.nativeOrder());

	/**
	 * Draws a triangle part of this image on the image buffer.
	 *
	 * @param gl
	 * 		The context to use
	 * @param viewX
	 * 		Image center x coordinate
	 * @param viewY
	 * 		Image center y coordinate
	 * @param u1
	 * @param v1
	 * @param u2
	 * @param v2
	 * @param u3
	 * @param v3
	 * @param color
	 */
	public void drawTriangle(GLDrawContext gl, float viewX,
			float viewY, float u1, float v1, float u2, float v2, float u3, float v3, float color) {
		try {
			checkStaticHandles(gl);
			float left = toffsetX + viewX;
			float top = -toffsetY + viewY;
			// In the draw process sub-integer coordinates can be rounded in unexpected ways that is particularly noticeable when redrawing the
			// growing
			// image of a building in the construction phase. By aligning to the nearest integer images can be placed in a more predictable and
			// controlled
			// manner.
			u1 = (float) Math.round(u1 * twidth) / twidth;
			u2 = (float) Math.round(u2 * twidth) / twidth;
			u3 = (float) Math.round(u3 * twidth) / twidth;
			v1 = (float) Math.round(v1 * theight) / theight;
			v2 = (float) Math.round(v2 * theight) / theight;
			v3 = (float) Math.round(v3 * theight) / theight;

			buildBfr.asFloatBuffer().put(new float[] {
					u1 * twidth,
					-v1 * theight,
					u1,
					v1,

					u2 * twidth,
					-v2 * theight,
					u2,
					v2,

					u3 * twidth,
					-v3 * theight,
					u3,
					v3,

			});
			gl.updateGeometryAt(buildHandle, 0, buildBfr);
			gl.draw2D(buildHandle, texture, EGeometryType.Triangle, 0, 3, left, top, 0, 1, 1, 1, null, color);
		} catch (IllegalBufferException e) {
			handleIllegalBufferException(e);
		}
	}

	public BufferedImage convertToBufferedImage() {
		if (width <= 0 || height <= 0) {
			return null;
		}

		BufferedImage rendered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		ShortBuffer data = getData().duplicate();
		data.rewind();

		int[] rgbArray = new int[data.remaining()];
		for (int i = 0; i < rgbArray.length; i++) {
			short myColor = data.get();
			rgbArray[i] = Color.convertTo32Bit(myColor);
		}

		rendered.setRGB(0, 0, width, height, rgbArray, 0, width);
		return rendered;
	}

	public Long hash() {
		long hashCode = 1L;
		long multiplier = 1L;
		while (data.hasRemaining()) {
			multiplier *= 31L;
			hashCode += (data.get() + 27L) * multiplier;
		}
		data.rewind();
		return hashCode;
	}
}