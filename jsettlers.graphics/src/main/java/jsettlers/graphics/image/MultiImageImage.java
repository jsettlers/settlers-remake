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
import jsettlers.common.Color;
import jsettlers.graphics.image.reader.ImageMetadata;

/**
 * This is an image inside a multi image map.
 *
 * @author michael
 */
public class MultiImageImage extends Image {
	private final MultiImageMap map;

	private GeometryHandle settlerGeometry;
	private GeometryHandle torsoGeometry;
	private float[] settlerFloats = null;
	private float[] torsoFloats = null;

	/**
	 * This is the data that is required to store the position of a {@link MultiImageImage}.
	 * 
	 * @author Michael Zangl.
	 *
	 */
	private final class Data {
		private int width;

		private int height;

		private int offsetX;

		private int offsetY;

		private float umin;

		private float umax;

		private float vmin;

		private float vmax;
	}

	private final Data settler;

	private final Data torso;

	public MultiImageImage(MultiImageMap map, ImageMetadata settlerMeta,
			int settlerx, int settlery, ImageMetadata torsoMeta, int torsox,
			int torsoy) {
		this.map = map;

		settler = new Data();
		settlerFloats = createGeometry(map, settlerMeta, settlerx, settlery, settler);
		if (torsoMeta != null) {
			torso = new Data();
			torsoFloats = createGeometry(map, torsoMeta, torsox, torsoy, torso);
		} else {
			torso = null;
			torsoGeometry = null;
		}
	}

	private static final float IMAGE_DRAW_OFFSET = 0.5f;

	private static float[] createGeometry(MultiImageMap map,
			ImageMetadata settlerMeta, int settlerx, int settlery, Data data) {
		data.width = settlerMeta.width;
		data.height = settlerMeta.height;
		data.offsetX = settlerMeta.offsetX;
		data.offsetY = settlerMeta.offsetY;

		data.umin = (float) settlerx / map.getWidth();
		data.umax = (float) (settlerx + settlerMeta.width) / map.getWidth();

		data.vmin = (float) (settlery + settlerMeta.height) / map.getHeight();
		data.vmax = (float) settlery / map.getHeight();
		return new float[] {
				// top left
				settlerMeta.offsetX + IMAGE_DRAW_OFFSET,
				-settlerMeta.offsetY - settlerMeta.height + IMAGE_DRAW_OFFSET,
				0,
				data.umin,
				data.vmin,

				// bottom left
				settlerMeta.offsetX + IMAGE_DRAW_OFFSET,
				-settlerMeta.offsetY + IMAGE_DRAW_OFFSET,
				0,
				data.umin,
				data.vmax,

				// bottom right
				settlerMeta.offsetX + settlerMeta.width + IMAGE_DRAW_OFFSET,
				-settlerMeta.offsetY + IMAGE_DRAW_OFFSET,
				0,
				data.umax,
				data.vmax,

				// top right
				settlerMeta.offsetX + settlerMeta.width + IMAGE_DRAW_OFFSET,
				-settlerMeta.offsetY - settlerMeta.height + IMAGE_DRAW_OFFSET,
				0,
				data.umax,
				data.vmin,
		};
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
	public void drawAt(GLDrawContext gl, float viewX, float viewY, int color) {
		gl.glPushMatrix();
		gl.glTranslatef(viewX, viewY, 0);
		draw(gl, Color.getABGR(color));
		gl.glPopMatrix();
	}

	@Override
	public void draw(GLDrawContext gl, Color color) {
		draw(gl, color, 1);
	}

	@Override
	public void draw(GLDrawContext gl, Color color, float multiply) {
		if(settlerGeometry == null) {
			settlerGeometry = gl.storeGeometry(settlerFloats);
			torsoGeometry = gl.storeGeometry(torsoFloats);
		}
		try {
			gl.color(multiply, multiply, multiply, 1);
			TextureHandle texture = map.getTexture(gl);
			gl.drawQuadWithTexture(texture, settlerGeometry, 0);
			if (torsoGeometry != null) {
				if (color != null) {
					gl.color(color.getRed() * multiply,
							color.getGreen() * multiply,
							color.getBlue() * multiply, color.getAlpha());
				}
				gl.drawQuadWithTexture(texture, torsoGeometry, 0);
			}
		} catch (IllegalBufferException e) {
			handleIllegalBufferException(e);
		}
	}

	private static final float[] TEMP_BUFFER = new float[5 * 4];

	@Override
	public void drawImageAtRect(GLDrawContext gl, float left, float bottom,
			float right, float top) {
		try {
			gl.color(1, 1, 1, 1);

			System.arraycopy(settlerFloats, 0, TEMP_BUFFER, 0, 4 * 5);
			TEMP_BUFFER[0] = left + IMAGE_DRAW_OFFSET;
			TEMP_BUFFER[1] = top + IMAGE_DRAW_OFFSET;
			TEMP_BUFFER[5] = left + IMAGE_DRAW_OFFSET;
			TEMP_BUFFER[6] = bottom + IMAGE_DRAW_OFFSET;
			TEMP_BUFFER[10] = right + IMAGE_DRAW_OFFSET;
			TEMP_BUFFER[11] = bottom + IMAGE_DRAW_OFFSET;
			TEMP_BUFFER[15] = right + IMAGE_DRAW_OFFSET;
			TEMP_BUFFER[16] = top + IMAGE_DRAW_OFFSET;
			GeometryHandle temp_handle = gl.storeGeometry(TEMP_BUFFER);

			gl.drawQuadWithTexture(map.getTexture(gl), temp_handle, 0);
			temp_handle.delete();
		} catch (IllegalBufferException e) {
			handleIllegalBufferException(e);
		}
	}

	@Override
	public int getWidth() {
		return settler.width;
	}

	@Override
	public int getHeight() {
		return settler.height;
	}
}
