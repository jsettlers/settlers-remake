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
import go.graphics.TextureHandle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.common.Color;
import jsettlers.graphics.map.draw.DrawBuffer;
import jsettlers.graphics.reader.ImageMetadata;

/**
 * This is an image inside a multi image map.
 *
 * @author michael
 */
public class MultiImageImage extends Image {
	private final MultiImageMap map;

	private final float[] settlerGeometry;
	private final float[] torsoGeometry;

	private static class Data extends ImageMetadata {
		private float umin;

		private float umax;

		private float vmin;

		private float vmax;

		@Override
		public void readFrom(DataInputStream in) throws IOException {
			super.readFrom(in);
			umin = in.readFloat();
			umax = in.readFloat();
			vmin = in.readFloat();
			vmax = in.readFloat();
		}

		@Override
		public void writeTo(DataOutputStream out) throws IOException {
			super.writeTo(out);
			out.writeFloat(umin);
			out.writeFloat(umax);
			out.writeFloat(vmin);
			out.writeFloat(vmax);
		}

		private float[] createGeometry() {
			return new float[] {
					// top left
					this.offsetX + IMAGE_DRAW_OFFSET,
					-this.offsetY - this.height + IMAGE_DRAW_OFFSET,
					0,
					this.umin,
					this.vmin,

					// bottom left
					this.offsetX + IMAGE_DRAW_OFFSET,
					-this.offsetY + IMAGE_DRAW_OFFSET,
					0,
					this.umin,
					this.vmax,

					// bottom right
					this.offsetX + this.width + IMAGE_DRAW_OFFSET,
					-this.offsetY + IMAGE_DRAW_OFFSET,
					0,
					this.umax,
					this.vmax,

					// top right
					this.offsetX + this.width + IMAGE_DRAW_OFFSET,
					-this.offsetY - this.height + IMAGE_DRAW_OFFSET,
					0,
					this.umax,
					this.vmin,
			};
		}

		public boolean valid() {
			return width > 0 && height > 0;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Data [umin=");
			builder.append(umin);
			builder.append(", umax=");
			builder.append(umax);
			builder.append(", vmin=");
			builder.append(vmin);
			builder.append(", vmax=");
			builder.append(vmax);
			builder.append(", width=");
			builder.append(width);
			builder.append(", height=");
			builder.append(height);
			builder.append(", offsetX=");
			builder.append(offsetX);
			builder.append(", offsetY=");
			builder.append(offsetY);
			builder.append("]");
			return builder.toString();
		}

	}

	private final Data settler;

	private final Data torso;

	public MultiImageImage(MultiImageMap map, ImageMetadata settlerMeta, int settlerx, int settlery, ImageMetadata torsoMeta, int torsox, int torsoy) {
		this(map, createData(map, settlerMeta, settlerx, settlery), torsoMeta == null ? null : createData(map, torsoMeta, torsox, torsoy));
	}

	public MultiImageImage(MultiImageMap map, Data settler, Data torso) {
		if (!settler.valid() || (torso != null && !torso.valid())) {
			throw new IllegalArgumentException("Invalid settler/torso pair: " + settler + "," + torso);
		}
		this.map = map;
		this.settler = settler;
		this.torso = torso;

		settlerGeometry = settler.createGeometry();
		if (torso != null) {
			torsoGeometry = torso.createGeometry();
		} else {
			torsoGeometry = null;
		}
	}

	private static final float IMAGE_DRAW_OFFSET = 0.5f;

	private static Data createData(MultiImageMap map,
			ImageMetadata settlerMeta, int settlerx, int settlery) {
		Data data = new Data();
		data.width = settlerMeta.width;
		data.height = settlerMeta.height;
		data.offsetX = settlerMeta.offsetX;
		data.offsetY = settlerMeta.offsetY;

		data.umin = (float) settlerx / map.getWidth();
		data.umax = (float) (settlerx + settlerMeta.width) / map.getWidth();

		data.vmin = (float) (settlery + settlerMeta.height) / map.getHeight();
		data.vmax = (float) (settlery) / map.getHeight();
		return data;
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
		try {
			gl.color(multiply, multiply, multiply, 1);
			TextureHandle texture = map.getTexture(gl);
			gl.drawQuadWithTexture(texture, settlerGeometry);
			if (torsoGeometry != null) {
				if (color != null) {
					gl.color(color.getRed() * multiply,
							color.getGreen() * multiply,
							color.getBlue() * multiply, color.getAlpha());
				}
				gl.drawQuadWithTexture(texture, torsoGeometry);
			}
		} catch (IllegalBufferException e) {
			handleIllegalBufferException(e);
		}
	}

	static private float[] tmpBuffer = new float[5 * 4];

	@Override
	public void drawImageAtRect(GLDrawContext gl, float left, float bottom,
			float right, float top) {
		try {
			gl.color(1, 1, 1, 1);

			System.arraycopy(settlerGeometry, 0, tmpBuffer, 0, 4 * 5);
			tmpBuffer[0] = left + IMAGE_DRAW_OFFSET;
			tmpBuffer[1] = top + IMAGE_DRAW_OFFSET;
			tmpBuffer[5] = left + IMAGE_DRAW_OFFSET;
			tmpBuffer[6] = bottom + IMAGE_DRAW_OFFSET;
			tmpBuffer[10] = right + IMAGE_DRAW_OFFSET;
			tmpBuffer[11] = bottom + IMAGE_DRAW_OFFSET;
			tmpBuffer[15] = right + IMAGE_DRAW_OFFSET;
			tmpBuffer[16] = top + IMAGE_DRAW_OFFSET;

			gl.drawQuadWithTexture(map.getTexture(gl), tmpBuffer);
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

	@Override
	public void drawAt(GLDrawContext gl, DrawBuffer buffer, float viewX,
			float viewY, int iColor) {
		drawAt(gl, buffer, viewX, viewY, iColor, iColor);
	}

	private void drawAt(GLDrawContext gl, DrawBuffer buffer, float viewX,
			float viewY, int sColor, int tColor) {
		try {
			TextureHandle texture = map.getTexture(gl);
			buffer.addImage(texture, viewX + settler.offsetX
					+ IMAGE_DRAW_OFFSET, viewY - settler.offsetY - settler.height
					+ IMAGE_DRAW_OFFSET, viewX + settler.offsetX + settler.width
					+ IMAGE_DRAW_OFFSET, viewY - settler.offsetY
					+ IMAGE_DRAW_OFFSET, settler.umin, settler.vmin, settler.umax,
					settler.vmax, sColor);
			if (torso != null) {
				buffer.addImage(texture, viewX + torso.offsetX
						+ IMAGE_DRAW_OFFSET, viewY - torso.offsetY - torso.height
						+ IMAGE_DRAW_OFFSET, viewX + torso.offsetX + torso.width
						+ IMAGE_DRAW_OFFSET, viewY - torso.offsetY
						+ IMAGE_DRAW_OFFSET, torso.umin, torso.vmin, torso.umax,
						torso.vmax, tColor);
			}
		} catch (IllegalBufferException e) {
			handleIllegalBufferException(e);
		}
	}

	@Override
	public void drawAt(GLDrawContext gl, DrawBuffer buffer, float viewX,
			float viewY, Color color, float multiply) {
		drawAt(gl, buffer, viewX, viewY,
				Color.getABGR(multiply, multiply, multiply, 1),
				dimColor(color, multiply));
	}

	/**
	 * Writes the meta data to the given output stream
	 * 
	 * @param out
	 *            The stream to write to.
	 * @throws IOException
	 */
	public void writeTo(DataOutputStream out) throws IOException {
		settler.writeTo(out);
		out.writeBoolean(torso != null);
		if (torso != null) {
			torso.writeTo(out);
		}
	}

	public static MultiImageImage readFrom(MultiImageMap map, DataInputStream in) throws IOException {
		Data settler = new Data();
		settler.readFrom(in);
		boolean hasTorso = in.readBoolean();
		Data torso = null;
		if (hasTorso) {
			torso = new Data();
			torso.readFrom(in);
		}
		return new MultiImageImage(map, settler, torso);
	}
}
