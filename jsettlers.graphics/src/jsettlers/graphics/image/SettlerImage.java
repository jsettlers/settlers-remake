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
import jsettlers.common.Color;
import jsettlers.graphics.map.draw.DrawBuffer;
import jsettlers.graphics.reader.ImageMetadata;

/**
 * This is the image of something that is displayed as an object on the map, e.g. an settler.
 * <p>
 * It can have a torso, an overlay that is always drawn together with the image.
 * 
 * @author michael
 */
public class SettlerImage extends SingleImage {

	private SingleImage torso = null;

	/**
	 * {@inheritDoc:Image#Image(ImageDataPrivider)}
	 * 
	 * @param data
	 *            The data to use.
	 */
	public SettlerImage(ImageMetadata metadata, short[] data) {
		super(metadata, data);
	}

	@Override
	public void draw(GLDrawContext gl, Color color) {
		if (this.torso != null) {
			super.draw(gl, null);
			this.torso.draw(gl, color);
		} else {
			super.draw(gl, color);
		}
	}

	@Override
	public void draw(GLDrawContext gl, Color color, float multiply) {
		super.draw(gl, null, multiply);
		if (this.torso != null) {
			this.torso.draw(gl, color, multiply);
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

	/**
	 * Gets the torso for this image.
	 * 
	 * @return The torso.
	 */
	public Image getTorso() {
		return this.torso;
	}

	@Override
	protected int getGeometryIndex(GLDrawContext context) {
		int index = super.getGeometryIndex(context);
		if (torso != null && torso.getWidth() == getWidth()
				&& torso.getHeight() == getHeight()
				&& torso.getOffsetX() == getOffsetX()
				&& torso.getOffsetY() == getOffsetY()) {
			torso.setGeometryIndex(index);
		}
		return index;
	}

	@Override
	public void drawAt(GLDrawContext gl, DrawBuffer buffer, float viewX,
			float viewY, int iColor) {
		super.drawAt(gl, buffer, viewX, viewY, iColor);
		if (this.torso != null) {
			torso.drawAt(gl, buffer, viewX, viewY, iColor);
		}
	}

	@Override
	public void drawAt(GLDrawContext gl, DrawBuffer buffer, float viewX,
			float viewY, Color color, float multiply) {
		super.drawAt(gl, buffer, viewX, viewY, Color.WHITE, multiply);
		if (this.torso != null) {
			torso.drawAt(gl, buffer, viewX, viewY, dimColor(color, multiply));
		}
	}
}
