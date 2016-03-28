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
package jsettlers.graphics.font;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.common.images.DirectImageLink;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.map.draw.DrawBuffer;
import jsettlers.graphics.map.draw.ImageProvider;

/**
 * This is a special font drawer class. It draws fonts using our built in font.
 * <p>
 * Currently, this class is not used for in-game messages but the {@link TextDrawer} is used instead. This class is a fall back if that fails.
 * 
 * @author Michael Zangl
 */
public class FontDrawer implements TextDrawer {
	private final EFontSize size;
	private final DrawBuffer drawBuffer;

	private static final DirectImageLink TEXTURE = new DirectImageLink("font.0");
	private static final String CHARACTERS =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!.?,„“()+-%_ÄÖÜ ";
	private static final int CHARS_PER_ROW = 16;
	private static final int CHARS_PER_COLUMN = 16;
	private final GLDrawContext gl;

	/**
	 * Creates a new {@link FontDrawer}
	 * 
	 * @param gl
	 *            The gl context to work with.
	 * @param drawBuffer
	 *            The draw buffer to work on.
	 * @param size
	 *            The font size to use.
	 */
	public FontDrawer(GLDrawContext gl, DrawBuffer drawBuffer, EFontSize size) {
		this.gl = gl;
		this.drawBuffer = drawBuffer;
		this.size = size;
	}

	@Override
	public void renderCentered(float cx, float cy, String text) {
		drawString(cx - (float) getWidth(text) / 2, cy
				- (float) getHeight(text) / 2, text);
	}

	private int getCharIndex(char c) {
		return CHARACTERS.indexOf(Character.toUpperCase(c));
	}

	@Override
	public void drawString(float x, float y, String string) {
		float cursorX = 0;
		float top = y + size.getSize();

		for (int i = 0; i < string.length(); i++) {
			int idx = getCharIndex(string.charAt(i));

			float w = getWidth(idx);
			float right = cursorX + w;
			float umin = (float) (idx & (CHARS_PER_ROW - 1)) / CHARS_PER_ROW;
			float umax = umin + (w / size.getSize() / CHARS_PER_ROW);
			float vmin = (float) (idx / CHARS_PER_ROW) / CHARS_PER_COLUMN;
			float vmax = vmin + (1.0f / CHARS_PER_COLUMN);

			// TODO: Chars, color
			Image image = ImageProvider.getInstance().getImage(TEXTURE);

			image.drawAt(gl, drawBuffer, cursorX, top, 0xffffffff);
			image.drawAt(gl, drawBuffer, 0, 0, 0xffffffff);
			cursorX += image.getWidth();
		}

		drawBuffer.flush();
		System.out.println("Drawed " + string + " chars");
	}

	private float getWidth(int charIndex) {
		if (charIndex >= 0) {
			return size.getSize();
		} else {
			return 0;
		}
	}

	@Override
	public double getWidth(String string) {
		float w = 0;
		for (int i = 0; i < string.length(); i++) {
			int idx = getCharIndex(string.charAt(i));
			w += getWidth(idx);
		}
		return w;
	}

	@Override
	public double getHeight(String string) {
		return size.getSize();
	}

	@Override
	public void setColor(float red, float green, float blue, float alpha) {
		// TODO Support color changes.
	}
}
