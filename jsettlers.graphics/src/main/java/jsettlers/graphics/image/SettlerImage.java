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

import java.nio.ShortBuffer;

import go.graphics.EGeometryType;
import go.graphics.GL2DrawContext;
import go.graphics.GLDrawContext;
import go.graphics.GeometryHandle;
import go.graphics.IllegalBufferException;
import go.graphics.SharedGeometry;
import go.graphics.TextureHandle;
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
	private boolean gl2;
	private ShortBuffer unifiedData = null;

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

	private boolean gl2Draw(GLDrawContext gl, float x, float y, float z, Color torsoColor, float fow, boolean settler, boolean shadow) {
		if((geometryIndex == null || SharedGeometry.isInvalid(gl, geometryIndex))) {
			gl2 = gl instanceof GL2DrawContext && (this.torso != null || this.shadow != null);

			if(!gl2) return false;
			if(unifiedData == null) generateUData();
			try {
				texture = gl.generateTexture(uwidth, uheight, unifiedData, name+"-merged");
				geometryIndex = SharedGeometry.addGeometry(gl, SharedGeometry.createQuadGeometry(uoffX, -uoffY, uoffX + uwidth, -uoffY - uheight, 0, 0, 1, 1));
			} catch (IllegalBufferException e) {
				e.printStackTrace();
			}
		}
		if(!gl2) return false;

		try {
			((GL2DrawContext)gl).drawUnified2D(geometryIndex.geometry, texture, EGeometryType.Quad, geometryIndex.index, 4, settler, shadow, x, y, z, 1, 1, 1, torsoColor, fow);
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
			TextureHandle settlerTex = getTextureIndex(gl);
			GeometryHandle settlerGeo = getGeometry(gl);
			gl.draw2D(settlerGeo, settlerTex, EGeometryType.Quad, geometryIndex.index, 4, x, y, z, 1, 1, 1, null, fow);

			if(torso != null && torsoColor != null) {
				TextureHandle torsoTex = torso.getTextureIndex(gl);
				GeometryHandle torsoGeo = torso.getGeometry(gl);
				gl.draw2D(torsoGeo, torsoTex, EGeometryType.Quad, torso.geometryIndex.index, 4, x, y, z, 1, 1, 1, torsoColor, fow);
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
				TextureHandle shadowTex = shadow.getTextureIndex(gl);
				GeometryHandle shadowGeo = shadow.getGeometry(gl);
				gl.draw2D(shadowGeo, shadowTex, EGeometryType.Quad, shadow.geometryIndex.index, 4, x, y, z, 1, 1, 1, null, 1);
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

	@Override
	protected GeometryHandle getGeometry(GLDrawContext context) throws IllegalBufferException {
		GeometryHandle index = super.getGeometry(context);
		if (torso != null && torso.getWidth() == getWidth()
				&& torso.getHeight() == getHeight()
				&& torso.getOffsetX() == getOffsetX()
				&& torso.getOffsetY() == getOffsetY()) {
			torso.setGeometry(geometryIndex);
		}
		return index;
	}

	private int uoffX, uoffY, uwidth, uheight;

	private void generateUData() {
		uoffX = offsetX;
		uoffY = offsetY;

		int tx = offsetX+width;
		int ty = offsetY+height;

		if(torso != null) {
			if(torso.offsetX < uoffX) uoffX = torso.offsetX;
			if(torso.offsetY < uoffY) uoffY = torso.offsetY;
			if(torso.offsetX+torso.width > tx) tx = torso.offsetX+torso.width;
			if(torso.offsetY+torso.height > ty) ty = torso.offsetY+torso.height;
		}

		if(shadow != null) {
			if(shadow.offsetX < uoffX) uoffX = shadow.offsetX;
			if(shadow.offsetY < uoffY) uoffY = shadow.offsetY;
			if(shadow.offsetX+shadow.width > tx) tx = shadow.offsetX+shadow.width;
			if(shadow.offsetY+shadow.height > ty) ty = shadow.offsetY+shadow.height;
		}

		uwidth = tx-uoffX;
		uheight = ty-uoffY;

		unifiedData = ShortBuffer.allocate(uwidth * uheight);

		short[] temp = new short[0];

		if(shadow != null) {
			int hoffX = shadow.offsetX-uoffX;
			int hoffY = shadow.offsetY-uoffY;

			if(temp.length < shadow.width) temp = new short[shadow.width];

			for(int y = 0;y != shadow.height;y++) {
				shadow.data.position(y*shadow.width);
				shadow.data.get(temp, 0, shadow.width);

				for(int x = 0;x != shadow.width;x++) {
					if(temp[x] == 0) continue;
					unifiedData.put((y+hoffY)* uwidth +hoffX+x, (short)((temp[x]&0xF)<<8)); // move alpha to green
				}
			}
		}

		if(torso != null) {
			int toffX = torso.offsetX-uoffX;
			int toffY = torso.offsetY-uoffY;

			if(temp.length < torso.width) temp = new short[torso.width];

			for(int y = 0;y != torso.height;y++) {
				torso.data.position(y*torso.width);
				torso.data.get(temp, 0, torso.width);

				for(int x = 0;x != torso.width;x++) {
					if(temp[x] == 0) continue;
					unifiedData.put((y+toffY)* uwidth +toffX+x, (short) ((temp[x]&0xF0)|0xF000)); // strip out everything except blue channel and set full red channel
				}
			}
		}

		int soffX = offsetX-uoffX;
		int soffY = offsetY-uoffY;

		if(temp.length < width) temp = new short[width];

		for(int y = 0;y != height;y++) {
			data.position(y*width);
			data.get(temp, 0, width);

			for(int x = 0;x != width;x++) {
				if(temp[x] != 0) unifiedData.put((y+soffY)* uwidth +soffX+x, temp[x]);
			}
		}
	}
}
