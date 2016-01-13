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
import go.graphics.IllegalBufferException;
import jsettlers.common.Color;
import jsettlers.graphics.map.draw.DrawBuffer;

public class ImageIndexImage extends Image {
	private final short width;
	private final short height;
	private final float[] geometry;
	private final ImageIndexTexture texture;
	private final int offsetX;
	private final int offsetY;
	private final float umin;
	private final float vmin;
	private final float umax;
	private final float vmax;

	protected ImageIndexImage(ImageIndexTexture texture, int offsetX,
			int offsetY, short width, short height, float umin, float vmin,
			float umax, float vmax) {
		this.texture = texture;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
		this.umin = umin;
		this.vmin = vmin;
		this.umax = umax;
		this.vmax = vmax;

		geometry =
				createGeometry(offsetX, offsetY, width, height, umin, vmin,
						umax, vmax);
	}

	@Override
	public void drawAt(GLDrawContext gl, float x, float y) {
		drawAt(gl, x, y, null);
	}

	@Override
	public void drawAt(GLDrawContext gl, float x, float y, Color color) {
		gl.glPushMatrix();
		gl.glTranslatef(x, y, 0);
		draw(gl, color);
		gl.glPopMatrix();
	}

	@Override
	public void draw(GLDrawContext gl, Color color) {
		draw(gl, color, 1);
	}

	@Override
	public void draw(GLDrawContext gl, Color color, float multiply) {
		if (color == null) {
			gl.color(multiply, multiply, multiply, 1);
		} else {
			gl.color(color.getRed() * multiply, color.getGreen() * multiply, color.getBlue() * multiply,
					color.getAlpha());
		}

		float[] geometryBuffer = geometry;
		draw(gl, geometryBuffer);
	}

	private void draw(GLDrawContext gl, float[] geometryBuffer) {
		try {
			gl.drawTrianglesWithTexture(texture.getTextureIndex(gl), geometryBuffer);
		} catch (IllegalBufferException e) {
			try {
				texture.recreateTexture();
				gl.drawTrianglesWithTexture(texture.getTextureIndex(gl), geometryBuffer);
			} catch (IllegalBufferException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	private static final float IMAGE_DRAW_OFFSET = .5f;

	private static float[] createGeometry(int offsetX, int offsetY, int width,
			int height, float umin, float vmin, float umax, float vmax) {
		return new float[] {
				// top left
				-offsetX + IMAGE_DRAW_OFFSET,
				-offsetY + height + IMAGE_DRAW_OFFSET,
				0,
				umin,
				vmin,

				// bottom left
				-offsetX + IMAGE_DRAW_OFFSET,
				-offsetY + IMAGE_DRAW_OFFSET,
				0,
				umin,
				vmax,

				// bottom right
				-offsetX + width + IMAGE_DRAW_OFFSET,
				-offsetY + IMAGE_DRAW_OFFSET,
				0,
				umax,
				vmax,

				// top right
				-offsetX + width + IMAGE_DRAW_OFFSET,
				-offsetY + height + IMAGE_DRAW_OFFSET,
				0,
				umax,
				vmin,
				// top left
				-offsetX + IMAGE_DRAW_OFFSET,
				-offsetY + height + IMAGE_DRAW_OFFSET,
				0,
				umin,
				vmin,
				// bottom right
				-offsetX + width + IMAGE_DRAW_OFFSET,
				-offsetY + IMAGE_DRAW_OFFSET,
				0,
				umax,
				vmax,

		};
	}

	static private float[] tmpBuffer = new float[5 * 6];

	@Override
	public void drawImageAtRect(GLDrawContext gl, float minX, float minY,
			float maxX, float maxY) {
		System.arraycopy(geometry, 0, tmpBuffer, 0, 4 * 5);
		tmpBuffer[0] = minX + IMAGE_DRAW_OFFSET;
		tmpBuffer[1] = maxY + IMAGE_DRAW_OFFSET;
		tmpBuffer[5] = minX + IMAGE_DRAW_OFFSET;
		tmpBuffer[6] = minY + IMAGE_DRAW_OFFSET;
		tmpBuffer[10] = maxX + IMAGE_DRAW_OFFSET;
		tmpBuffer[11] = minY + IMAGE_DRAW_OFFSET;
		tmpBuffer[15] = maxX + IMAGE_DRAW_OFFSET;
		tmpBuffer[16] = maxY + IMAGE_DRAW_OFFSET;
		tmpBuffer[20] = minX + IMAGE_DRAW_OFFSET;
		tmpBuffer[21] = maxY + IMAGE_DRAW_OFFSET;
		tmpBuffer[25] = maxX + IMAGE_DRAW_OFFSET;
		tmpBuffer[26] = minY + IMAGE_DRAW_OFFSET;

		draw(gl, tmpBuffer);
	}

	@Override
	public void drawAt(GLDrawContext gl, DrawBuffer buffer, float viewX, float viewY, int iColor) {
		try {
			buffer.addImage(texture.getTextureIndex(gl), viewX - offsetX, viewY - offsetY, viewX - offsetX + width, viewY - offsetY + height, umin,
					vmin,
					umax, vmax, iColor);
		} catch (IllegalBufferException e) {
			handleIllegalBufferException(e);
		}
	}

}
