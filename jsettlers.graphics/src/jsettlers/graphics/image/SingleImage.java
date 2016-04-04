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

import go.graphics.GLDrawContext;
import go.graphics.GeometryHandle;
import go.graphics.IllegalBufferException;
import go.graphics.TextureHandle;

import java.nio.ShortBuffer;

import jsettlers.common.Color;
import jsettlers.graphics.map.draw.DrawBuffer;
import jsettlers.graphics.reader.ImageMetadata;

/**
 * This is the base for all images that are directly loaded from the image file.
 * <p>
 * This class interprets the image data in 5-5-5-1-Format. To change the interpretation, it is possible to subclass this class.
 * 
 * @author Michael Zangl
 */
public class SingleImage extends Image implements ImageDataPrivider {

	protected ShortBuffer data;
	protected final int width;
	protected final int height;
	protected int textureWidth = 0;
	protected int textureHeight = 0;
	protected final int offsetX;
	protected final int offsetY;

	private TextureHandle texture = null;
	private GeometryHandle geometryhandle = null;

	/**
	 * Creates a new image by the given buffer.
	 * 
	 * @param data
	 *            The data buffer for the image with an unspecified color format.
	 * @param width
	 *            The width.
	 * @param height
	 *            The height.
	 * @param offsetX
	 *            The x offset of the image.
	 * @param offsetY
	 *            The y offset of the image.
	 */
	protected SingleImage(ShortBuffer data, int width, int height, int offsetX,
			int offsetY) {
		this.data = data;
		this.width = width;
		this.height = height;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	/**
	 * Creates a new image by linking this images data to the data of the provider.
	 * 
	 * @param metadata
	 *            The mata data to use.
	 * @param data
	 *            The data to use.
	 */
	protected SingleImage(ImageMetadata metadata, short[] data) {
		this.data = ShortBuffer.wrap(data);
		this.width = metadata.width;
		this.height = metadata.height;
		this.offsetX = metadata.offsetX;
		this.offsetY = metadata.offsetY;
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

	/**
	 * Converts the current data to match the power of two size.
	 */
	protected void adaptDataToTextureSize() {
		if (width == 0 || height == 0) {
			return;
		}

		this.data.rewind();
		short[] newData = new short[textureHeight * textureWidth];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				newData[y * textureWidth + x] = data.get(y * width + x);
			}
			for (int x = width; x < textureWidth; x++) {
				newData[y * textureWidth + x] = newData[y * textureWidth + width - 1];
			}
		}
		for (int y = height; y < textureHeight; y++) {
			for (int x = 0; x < textureWidth; x++) {
				newData[y * textureWidth + x] = newData[(height - 1) * textureWidth + x];
			}
		}
		data = ShortBuffer.wrap(newData);
	}

	/**
	 * Generates the texture, if needed, and returns the index of that texutre.
	 * 
	 * @param gl
	 *            The gl context to use to generate the image.
	 * @return The gl handle or <code>null</code> if the texture is not allocated.
	 */
	public TextureHandle getTextureIndex(GLDrawContext gl) {
		if (texture == null || !texture.isValid()) {
			if (textureWidth == 0) {
				textureWidth = gl.makeWidthValid(width);
				textureHeight = gl.makeHeightValid(height);
				if (textureWidth != width || textureHeight != height) {
					adaptDataToTextureSize();
				}
				data.position(0);
			}
			texture = gl.generateTexture(textureWidth, textureHeight, this.data);
		}
		return this.texture;
	}

	private static float[] TMP_BUFFER = new float[] {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};

	@Override
	public void drawImageAtRect(GLDrawContext gl, float left, float bottom,
			float right, float top) {
		try {
			TextureHandle textureHandle = getTextureIndex(gl);

			TMP_BUFFER[0] = left;
			TMP_BUFFER[1] = top;

			TMP_BUFFER[5] = left;
			TMP_BUFFER[6] = bottom;
			TMP_BUFFER[9] = (float) height / textureHeight;

			TMP_BUFFER[10] = right;
			TMP_BUFFER[11] = bottom;
			TMP_BUFFER[13] = (float) width / textureWidth;
			TMP_BUFFER[14] = (float) height / textureHeight;

			TMP_BUFFER[15] = right;
			TMP_BUFFER[16] = top;
			TMP_BUFFER[18] = (float) width / textureWidth;

			gl.drawQuadWithTexture(textureHandle, TMP_BUFFER);
		} catch (IllegalBufferException e) {
			handleIllegalBufferException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsettlers.graphics.image.Image#drawAt(go.graphics.GLDrawContext, float, float)
	 */
	@Override
	public void drawAt(GLDrawContext gl, float x, float y) {
		drawAt(gl, x, y, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsettlers.graphics.image.Image#drawAt(go.graphics.GLDrawContext, float, float, go.graphics.Color)
	 */
	@Override
	public void drawAt(GLDrawContext gl, float x, float y, Color color) {
		gl.glPushMatrix();
		gl.glTranslatef(x, y, 0);
		draw(gl, color);
		gl.glPopMatrix();
	}

	@Override
	public ShortBuffer getData() {
		return this.data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsettlers.graphics.image.Image#draw(go.graphics.GLDrawContext, go.graphics.Color)
	 */
	@Override
	public void draw(GLDrawContext gl, Color color) {
		try {
			if (color == null) {
				gl.color(1, 1, 1, 1);
			} else {
				gl.color(color.getRed(), color.getGreen(), color.getBlue(),
						color.getAlpha());
			}

			TextureHandle textureIndex = getTextureIndex(gl);
			GeometryHandle geometry = getGeometry(gl);
			gl.drawTrianglesWithTexture(textureIndex, geometry, 2);
		} catch (IllegalBufferException e) {
			handleIllegalBufferException(e);
		}
	}

	@Override
	public void draw(GLDrawContext gl, Color color, float multiply) {
		try {
			if (color == null) {
				gl.color(multiply, multiply, multiply, 1);
			} else {
				gl.color(color.getRed() * multiply, color.getGreen() * multiply,
						color.getBlue() * multiply, color.getAlpha());
			}

			TextureHandle textureIndex = getTextureIndex(gl);
			GeometryHandle geometryIndex2 = getGeometry(gl);
			gl.drawTrianglesWithTexture(textureIndex, geometryIndex2, 2);
		} catch (IllegalBufferException e) {
			handleIllegalBufferException(e);
		}
	}

	private float[] getGeometry() {
		int left = getOffsetX();
		int top = -getOffsetY();
		return new float[] {
				// bottom right
				left + this.width,
				top,
				0,
				(float) width / textureWidth,
				0,
				// top left
				left,
				top,
				0,
				0,
				0,
				// top right
				left + this.width,
				top - this.height,
				0,
				(float) width / textureWidth,
				(float) height / textureHeight,

				// top right
				left + this.width,
				top - this.height,
				0,
				(float) width / textureWidth,
				(float) height / textureHeight,
				// bottom left
				left,
				top,
				0,
				0,
				0,
				// top left
				left,
				top - this.height,
				0,
				0,
				(float) height / textureHeight,
		};
	}

	protected void setGeometry(GeometryHandle geometry) {
		geometryhandle = geometry;
	}

	protected GeometryHandle getGeometry(GLDrawContext context) {
		if (geometryhandle == null || !geometryhandle.isValid()) {
			geometryhandle = context.storeGeometry(getGeometry());
		}
		return geometryhandle;
	}

	public float getTextureScaleX() {
		return (float) width / textureWidth;
	}

	public float getTextureScaleY() {
		return (float) height / textureHeight;
	}

	@Override
	public void drawAt(GLDrawContext gl, DrawBuffer buffer, float viewX,
			float viewY, int iColor) {
		try {
			TextureHandle textureIndex = getTextureIndex(gl);
			buffer.addImage(textureIndex, viewX + getOffsetX(), viewY
					- getOffsetY(), viewX + getOffsetX() + width, viewY
					- getOffsetY() - height,
					0, 0, getTextureScaleX(),
					getTextureScaleY(), iColor);
		} catch (IllegalBufferException e) {
			handleIllegalBufferException(e);
		}
	}

	protected float convertU(float relativeU) {
		return relativeU * getTextureScaleX();
	}

	protected float convertV(float relativeV) {
		return relativeV * getTextureScaleY();
	}

	/**
	 * Draws a triangle part of this image on the image buffer.
	 * 
	 * @param gl
	 *            The context to use
	 * @param buffer
	 *            The buffer to draw on.
	 * @param viewX
	 *            Image center x coordinate
	 * @param viewY
	 *            Image center y coordinate
	 * @param u1
	 * @param v1
	 * @param u2
	 * @param v2
	 * @param u3
	 * @param v3
	 * @param activeColor
	 */
	public void drawTriangle(GLDrawContext gl, DrawBuffer buffer, float viewX,
			float viewY, float u1, float v1, float u2, float v2, float u3, float v3, int activeColor) {
		try {
			DrawBuffer.Buffer buffer2 = buffer.getBuffer(getTextureIndex(gl));
			float left = getOffsetX() + viewX;
			float top = -getOffsetY() + viewY;
			// In the draw process sub-integer coordinates can be rounded in unexpected ways that is particularly noticeable when redrawing the
			// growing
			// image of a building in the construction phase. By aligning to the nearest integer images can be placed in a more predictable and
			// controlled
			// manner.
			u1 = (float) Math.round(u1 * width) / width;
			u2 = (float) Math.round(u2 * width) / width;
			u3 = (float) Math.round(u3 * width) / width;
			v1 = (float) Math.round(v1 * height) / height;
			v2 = (float) Math.round(v2 * height) / height;
			v3 = (float) Math.round(v3 * height) / height;

			buffer2.addTriangle(
					(left + u1 * width),
					(top - v1 * height),
					(left + u2 * width),
					(top - v2 * height),
					(left + u3 * width),
					(top - v3 * height),
					convertU(u1),
					convertV(v1),
					convertU(u2),
					convertV(v2),
					convertU(u3),
					convertV(v3),
					activeColor);
		} catch (IllegalBufferException e) {
			handleIllegalBufferException(e);
		}
	}
}