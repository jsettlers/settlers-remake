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

import go.graphics.EGeometryType;
import go.graphics.GL2DrawContext;
import go.graphics.GL3DrawContext;
import go.graphics.GLDrawContext;
import go.graphics.IllegalBufferException;
import go.graphics.SharedDrawing;
import go.graphics.SharedTexture;
import jsettlers.common.Color;
import jsettlers.graphics.image.reader.ImageMetadata;

/**
 * This is the image of something that is displayed as an object on the map, e.g. an settler.
 * <p>
 * It can have a torso, an overlay that is always drawn together with the image.
 * 
 * @author michael
 */
public class SettlerImage extends SingleImage {

	private SingleImage torso = null;
	private SingleImage shadow = null;
	private boolean gl2 = false;
	private boolean gl3 = false;

	/**
	 * Creates a new settler image.
	 * 
	 * @param metadata
	 *            The mata data to use.
	 * @param data
	 *            The data to use.
	 */
	public SettlerImage(ImageMetadata metadata, short[] data, String name) {
		super(metadata, data, name);
	}

	@Override
	protected void checkHandles(GLDrawContext gl) throws IllegalBufferException {
		if (texture == null || SharedTexture.isInvalid(gl, texture)) {
			gl2 = gl instanceof GL2DrawContext;
			gl3 = gl instanceof GL3DrawContext;
			if(gl2) generateUData();
		}

		super.checkHandles(gl);
		if(!gl2) {
			if (torso != null && torso.getWidth() == getWidth()
					&& torso.getHeight() == getHeight()
					&& torso.getOffsetX() == getOffsetX()
					&& torso.getOffsetY() == getOffsetY()) {
				torso.setGeometry(geometryIndex);
			}
		}
	}

	private boolean gl2Draw(GLDrawContext gl, float x, float y, float z, Color torsoColor, float fow, boolean settler, boolean shadow) {
		try {
			checkHandles(gl);
			if(gl3) {
				SharedDrawing.schedule((GL3DrawContext)gl, texture, geometryIndex, x, y, z, torsoColor, fow, settler, shadow);
			} else if(gl2) {
				((GL2DrawContext) gl).drawUnified2D(geometryIndex.geometry, texture.texture, EGeometryType.Quad, geometryIndex.index, 4, settler, shadow, x, y, z, 1, 1, 1, torsoColor, fow);
			} else {
				return false;
			}
		} catch(IllegalBufferException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public void drawAt(GLDrawContext gl, float x, float y, float z, Color torsoColor, float fow) {
		if(gl2Draw(gl, x, y, z, torsoColor, fow, true, true)) return;
		drawOnlyImageAt(gl, x, y, z, torsoColor, fow);
		drawOnlyShadowAt(gl, x, y, z);
	}

	@Override
	public void drawOnlyImageAt(GLDrawContext gl, float x, float y, float z, Color torsoColor, float fow) {
		if(gl2Draw(gl, x, y, z, torsoColor, fow, true, false)) return;
		try {
			checkHandles(gl);
			gl.draw2D(geometryIndex.geometry, texture.texture, EGeometryType.Quad, geometryIndex.index, 4, x, y, z, 1, 1, 1, null, fow);

			if(torso != null && torsoColor != null) {
				torso.checkHandles(gl);
				gl.draw2D(torso.geometryIndex.geometry, torso.texture.texture, EGeometryType.Quad, torso.geometryIndex.index, 4, x, y, z, 1, 1, 1, torsoColor, fow);
			}
		} catch (IllegalBufferException e) {
			handleIllegalBufferException(e);
		}
	}

	@Override
	public void drawOnlyShadowAt(GLDrawContext gl, float x, float y, float z) {
		if(gl2Draw(gl, x, y, z, null, 0, false, true)) return;
		if(shadow != null) {
			try {
				shadow.checkHandles(gl);
				gl.draw2D(shadow.geometryIndex.geometry, shadow.texture.texture, EGeometryType.Quad, shadow.geometryIndex.index, 4, x, y, z, 1, 1, 1, null, 1);
			} catch (IllegalBufferException e) {
				handleIllegalBufferException(e);
			}
		}
	}

	/**
	 * Sets the image overlay.
	 * 
	 * @param torso
	 *            The torso. May be null.
	 */
	public void setTorso(SingleImage torso) {
		this.torso = torso;
	}

	public void setShadow(SingleImage shadow) {
		this.shadow = shadow;
	}

	/**
	 * Gets the torso for this image.
	 * 
	 * @return The torso.
	 */
	public Image getTorso() {
		return this.torso;
	}

	private void generateUData() {
		toffsetX = offsetX;
		toffsetY = offsetY;

		int tx = offsetX+width;
		int ty = offsetY+height;

		if(torso != null) {
			if(torso.offsetX < toffsetX) toffsetX = torso.offsetX;
			if(torso.offsetY < toffsetY) toffsetY = torso.offsetY;
			if(torso.offsetX+torso.width > tx) tx = torso.offsetX+torso.width;
			if(torso.offsetY+torso.height > ty) ty = torso.offsetY+torso.height;
		}

		if(shadow != null) {
			if(shadow.offsetX < toffsetX) toffsetX = shadow.offsetX;
			if(shadow.offsetY < toffsetY) toffsetY = shadow.offsetY;
			if(shadow.offsetX+shadow.width > tx) tx = shadow.offsetX+shadow.width;
			if(shadow.offsetY+shadow.height > ty) ty = shadow.offsetY+shadow.height;
		}

		twidth = tx-toffsetX;
		theight = ty-toffsetY;

		tdata = ByteBuffer.allocateDirect(twidth * theight * 2).order(ByteOrder.nativeOrder()).asShortBuffer();

		short[] temp = new short[0];

		if(shadow != null) {
			int hoffX = shadow.offsetX-toffsetX;
			int hoffY = shadow.offsetY-toffsetY;

			if(temp.length < shadow.width) temp = new short[shadow.width];

			for(int y = 0;y != shadow.height;y++) {
				shadow.data.position(y*shadow.width);
				shadow.data.get(temp, 0, shadow.width);

				for(int x = 0;x != shadow.width;x++) {
					if(temp[x] == 0) continue;
					tdata.put((y+hoffY)*twidth+hoffX+x, (short)((temp[x]&0xF)<<8)); // move alpha to green
				}
			}
		}

		if(torso != null) {
			int toffX = torso.offsetX-toffsetX;
			int toffY = torso.offsetY-toffsetY;

			if(temp.length < torso.width) temp = new short[torso.width];

			for(int y = 0;y != torso.height;y++) {
				torso.data.position(y*torso.width);
				torso.data.get(temp, 0, torso.width);

				for(int x = 0;x != torso.width;x++) {
					if(temp[x] == 0) continue;
					tdata.put((y+toffY)*twidth+toffX+x, (short) ((temp[x]&0xF0)|0xF000)); // strip out everything except blue channel and set full red channel
				}
			}
		}

		int soffX = offsetX-toffsetX;
		int soffY = offsetY-toffsetY;

		if(temp.length < width) temp = new short[width];

		for(int y = 0;y != height;y++) {
			data.position(y*width);
			data.get(temp, 0, width);

			for(int x = 0;x != width;x++) {
				if(temp[x] != 0) tdata.put((y+soffY)*twidth+soffX+x, temp[x]);
			}
		}
	}
}
