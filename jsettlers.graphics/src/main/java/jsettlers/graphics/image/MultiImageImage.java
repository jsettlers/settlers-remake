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
import java.nio.FloatBuffer;

import go.graphics.GLDrawContext;
import go.graphics.GeometryHandle;
import go.graphics.IllegalBufferException;
import go.graphics.SharedGeometry;
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

	private SharedGeometry.SharedGeometryHandle settlerGeometry;
	private SharedGeometry.SharedGeometryHandle torsoGeometry;
	private float[] settlerFloats;
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
		return SharedGeometry.createQuadGeometry(settlerMeta.offsetX + IMAGE_DRAW_OFFSET, -settlerMeta.offsetY + IMAGE_DRAW_OFFSET,
				settlerMeta.offsetX + settlerMeta.width + IMAGE_DRAW_OFFSET, -settlerMeta.offsetY - settlerMeta.height + IMAGE_DRAW_OFFSET,
				data.umin, data.vmax, data.umax, data.vmin);
	}

	@Override
	public void drawOnlyImageAt(GLDrawContext gl, float x, float y, float fow) {
		gl.glPushMatrix();
		gl.glTranslatef(x, y, 0);
		try {
			if(settlerGeometry == null)	settlerGeometry = SharedGeometry.addGeometry(gl, settlerFloats);
			gl.color(fow, fow, fow, 1);
			TextureHandle texture = map.getTexture(gl);
			gl.drawQuadWithTexture(texture, settlerGeometry.geometry, settlerGeometry.index);
		} catch (IllegalBufferException e) {
			e.printStackTrace();
		}
		gl.glPopMatrix();
	}

	@Override
	public void drawOnlyTorsoAt(GLDrawContext gl, float x, float y, Color torsoColor, float fow) {
		if(torsoFloats == null) return;

		gl.glPushMatrix();
		gl.glTranslatef(x, y, 0);
		try {
			if(torsoGeometry == null) torsoGeometry = SharedGeometry.addGeometry(gl, torsoFloats);
			if (torsoColor != null) {
				gl.color(torsoColor.getRed()*fow,
						torsoColor.getGreen()*fow,
						torsoColor.getBlue()*fow, torsoColor.getAlpha());
			}
			TextureHandle texture = map.getTexture(gl);
			gl.drawQuadWithTexture(texture, torsoGeometry.geometry, torsoGeometry.index);
		} catch (IllegalBufferException e) {
			e.printStackTrace();
		}
		gl.glPopMatrix();
	}

	private static final ByteBuffer tempBuffer = ByteBuffer.allocateDirect(5*4*4).order(ByteOrder.nativeOrder());
	private static GeometryHandle tempGeometry  = null;

	@Override
	public void drawImageAtRect(GLDrawContext gl, float left, float bottom,
			float right, float top) {
		try {
			gl.color(1, 1, 1, 1);
			if(tempGeometry == null) {
				tempGeometry = gl.generateGeometry(20*4);
				tempBuffer.asFloatBuffer().get(settlerFloats, 0, 20);
			}

			FloatBuffer fltcopy = tempBuffer.asFloatBuffer();

			fltcopy.put(0, left + IMAGE_DRAW_OFFSET);
			fltcopy.put(1, top + IMAGE_DRAW_OFFSET);
			fltcopy.put(5, left + IMAGE_DRAW_OFFSET);
			fltcopy.put(6, bottom + IMAGE_DRAW_OFFSET);
			fltcopy.put(10, right + IMAGE_DRAW_OFFSET);
			fltcopy.put(11, bottom + IMAGE_DRAW_OFFSET);
			fltcopy.put(15, right + IMAGE_DRAW_OFFSET);
			fltcopy.put(16, top + IMAGE_DRAW_OFFSET);

			gl.updateGeometryAt(tempGeometry, 0, tempBuffer);

			gl.drawQuadWithTexture(map.getTexture(gl), tempGeometry, 0);
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
