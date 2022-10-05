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

import go.graphics.AbstractColor;
import go.graphics.EPrimitiveType;
import go.graphics.GLDrawContext;
import go.graphics.IllegalBufferException;
import go.graphics.ManagedUnifiedDrawHandle;

import java.awt.image.BufferedImage;

import go.graphics.UnifiedDrawHandle;
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

	protected ManagedUnifiedDrawHandle geometryIndex = null;

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
		this(wrap(data), metadata.width, metadata.height, metadata.offsetX, metadata.offsetY, name);
	}

	private static ShortBuffer wrap(short[] data) {
		ShortBuffer bfr = ByteBuffer.allocateDirect(data.length*2).order(ByteOrder.nativeOrder()).asShortBuffer();
		bfr.put(data);
		bfr.rewind();
		return bfr;
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
		checkStaticHandles(gl);

		// dark magic
		float sx = width/(float)twidth;
		float sy = height/(float)theight;
		float tx = x - offsetX*sx;
		float ty = y + height + offsetY*sy;
		geometryIndex.drawSimple(EPrimitiveType.Quad, tx, ty, 0, sx, sy, null, 1);
	}

	@Override
	public ShortBuffer getData() {
		return this.data;
	}

	@Override
	public void drawOnlyImageAt(GLDrawContext gl, float x, float y, float z, Color torsoColor, float fow) {
		checkHandles(gl);
		geometryIndex.drawSimple(EPrimitiveType.Quad, x, y, z, 1, 1, null, fow);
	}

	protected void checkHandles(GLDrawContext gl) {
		if(geometryIndex == null || !geometryIndex.isValid()) {
			geometryIndex = gl.createManagedUnifiedDrawCall(tdata, toffsetX, toffsetY, twidth, theight);
		}
	}

	private void checkStaticHandles(GLDrawContext gl) {
		checkHandles(gl);

		if(progressHandle == null || !progressHandle.isValid()) {
			progressHandle = gl.createUnifiedDrawCall(4, "building-progress-quad", geometryIndex.texture, GLDrawContext.createQuadGeometry(0, 0, 1, 1, 0, 0, 1, 1));
			progressHandle.forceNoCache();
		}

		if(triProgressHandle == null || !triProgressHandle.isValid()) {
			triProgressHandle = gl.createUnifiedDrawCall(3, "building-progress-tri", geometryIndex.texture, new float[] {0, 0, 0, 0, 0.5f, 1, 0.5f, 1, 1, 0, 1, 0});
			triProgressHandle.forceNoCache();
		}
	}

	private static UnifiedDrawHandle progressHandle = null;
	private static UnifiedDrawHandle triProgressHandle = null;


	public void drawOnlyImageWithProgressAt(GLDrawContext gl, float x, float y, float z, float u1, float v1, float u2, float v2, float fow, boolean triangle) {
		checkStaticHandles(gl);

		float nu1 = geometryIndex.texX+u1*(geometryIndex.texWidth-geometryIndex.texX);
		float nu2 = geometryIndex.texX+u2*(geometryIndex.texWidth-geometryIndex.texX);

		float nv1 = geometryIndex.texY+v1*(geometryIndex.texHeight-geometryIndex.texY);
		float nv2 = geometryIndex.texY+v2*(geometryIndex.texHeight-geometryIndex.texY);

		UnifiedDrawHandle dh = triangle?triProgressHandle:progressHandle;
		dh.texture = geometryIndex.texture;
		dh.drawProgress(triangle?EPrimitiveType.Triangle:EPrimitiveType.Quad, x+toffsetX+twidth*u1, y-toffsetY-theight*v1, z, twidth*(u2-u1), -theight*(v2-v1), new Color(nu1, nv1, nu2, nv2), fow);
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