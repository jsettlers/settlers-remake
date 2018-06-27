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

	/**
	 * Creates a new settler image.
	 * 
	 * @param metadata
	 *            The mata data to use.
	 * @param data
	 *            The data to use.
	 */
	public SettlerImage(ImageMetadata metadata, short[] data) {
		super(metadata, data);
	}

	@Override
	public void drawOnlyTorsoAt(GLDrawContext gl, float x, float y, Color torsoColor, float fow) {
		if(torso != null) torso.drawOnlyTorsoAt(gl, x, y, torsoColor, fow);
	}

	@Override
	public void drawOnlyShadowAt(GLDrawContext gl, float x, float y, float fow) {
		if(shadow != null) shadow.drawOnlyImageAt(gl, x, y, fow);
	}

	@Override
	public void drawAt(GLDrawContext gl, float x, float y, Color torsoColor, float fow) {
		drawOnlyImageAt(gl, x , y, fow);
		drawOnlyTorsoAt(gl, x, y, torsoColor != null ? torsoColor : Color.WHITE, fow);
		drawOnlyShadowAt(gl, x, y, fow);
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
}
