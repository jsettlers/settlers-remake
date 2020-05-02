/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
package go.graphics.text;

import go.graphics.AbstractColor;
import go.graphics.ETextureType;
import go.graphics.EUnifiedMode;
import go.graphics.GLDrawContext;
import go.graphics.TextureHandle;
import go.graphics.UnifiedDrawHandle;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * This class is a text drawer used to wrap the text renderer.
 *
 * @author michael
 * @author paul
 */
public abstract class AbstractTextDrawer<T extends GLDrawContext> {

	protected static final String CHARACTERS;
	protected static final int CHARACTER_COUNT;
	protected static final int TEXTURE_LINE_LEN;
	protected static final int TEXTURE_LINE_COUNT;

	static {
		StringBuilder charsBuilder = new StringBuilder();
		for(char i=' ';i<128; i++) charsBuilder.append(i);

		charsBuilder.append("ÆØåæéø"); // danish
		charsBuilder.append("ÄÖÜẞäöüß"); // german
		charsBuilder.append("¡¿áéíñóú–"); // spanish
		charsBuilder.append("óąćęŁłńŚśźŻż"); // polish

		// TODO russian characters are breaking some characters like + for some reason
		charsBuilder.append("АБВГДЖЗИКЛМНОПРСТУФХШабвгдежзийклмнопрстуфхцчшщыьэюяё"); // russian


		CHARACTERS = charsBuilder.toString();
		CHARACTER_COUNT = CHARACTERS.length();

		TEXTURE_LINE_LEN = (int)Math.sqrt(CHARACTER_COUNT);
		TEXTURE_LINE_COUNT = (int)Math.ceil(CHARACTER_COUNT/(float)TEXTURE_LINE_LEN);
	}

	protected static final int TEXTURE_GENERATION_SIZE = 30;

	private final float scalingFactor;

	private UnifiedDrawHandle geometry;
	protected TextureHandle font_tex;
	protected int gentex_line_height;
	protected int tex_height;
	protected int tex_width;
	protected int[] char_widths = new int[CHARACTER_COUNT];
	protected final int[] heightPerSize = new int[EFontSize.values().length];

	private final static int char_spacing = 2; // spacing between two characters (otherwise j and f would overlap with the next character)

	protected final T drawContext;

	/**
	 * Creates a new text drawer.
	 *
	 */
	public AbstractTextDrawer(T drawContext, float guiScale) {
		this.drawContext = drawContext;
		scalingFactor = guiScale <= 0.51f ? calculateScalingFactor() : guiScale;

		int descent = init();
		generateTexture();
		generateGeometry(descent);
	}

	private int getMaxLen() {
		int max_len = 0;
		for(int l = 0;l != TEXTURE_LINE_COUNT;l++) {
			int current_len = 0;
			for(int c = 0;c != TEXTURE_LINE_LEN;c++) {
				int i = l*TEXTURE_LINE_LEN+c;
				if(i == CHARACTER_COUNT) break;
				current_len += char_widths[i]+char_spacing;
				max_len = Math.max(max_len, current_len);
			}
		}
		return max_len;
	}

	protected abstract float calculateScalingFactor();

	protected abstract int init();

	protected abstract int[] getRGB();

	protected abstract void setupBitmapDraw();

	protected abstract void drawChar(char[] character, int x, int y);

	protected abstract void endDraw();

	private void generateTexture() {
		int max_len = getMaxLen();

		tex_width = max_len;
		tex_height = gentex_line_height*16;

		setupBitmapDraw();

		for(int l = 0;l != TEXTURE_LINE_COUNT;l++) {
			int line_offset = 0;
			for (int c = 0; c != TEXTURE_LINE_LEN; c++) {
				int i = l*TEXTURE_LINE_LEN+c;
				if(i == CHARACTER_COUNT) break;
				drawChar(new char[]{CHARACTERS.charAt(i)}, line_offset, l * gentex_line_height);
				line_offset += char_widths[i]+char_spacing;
			}
		}

		ShortBuffer bfr = ByteBuffer.allocateDirect(tex_width*tex_height*2).order(ByteOrder.nativeOrder()).asShortBuffer();

		int[] pixels = getRGB();
		endDraw();

		final short alpha_channel = 0b1111;
		final short alpha_white = ~alpha_channel;
		for (int y = 0; y != tex_height; y++) {
			for(int x = 0;x != tex_width;x++) {
				int pixel = pixels[(tex_height-y-1)*tex_width+x];

				short a = ((pixel >> 24) != 0 ? alpha_channel : 0);
				bfr.put((short) (a | alpha_white));
			}
		}

		bfr.rewind();
		font_tex = drawContext.generateTexture(max_len, tex_height, bfr, "text-drawer");
		if(font_tex != null) font_tex.setType(ETextureType.LINEAR_FILTER);
	}

	private void generateGeometry(int descent) {
		float[] geodata = new float[CHARACTER_COUNT*4*4];
		for(int l = 0;l != TEXTURE_LINE_COUNT;l++) {
			int line_offset = 0;
			for (int c = 0; c != TEXTURE_LINE_LEN; c++) {
				if(l*TEXTURE_LINE_LEN+c == CHARACTER_COUNT) break;

				float dx = line_offset;
				float dy = tex_height-(l*gentex_line_height+descent);

				float dw = char_widths[l*TEXTURE_LINE_LEN+c];
				float dh = gentex_line_height;

				float[] data = GLDrawContext.createQuadGeometry(0, 0,dw/(float)gentex_line_height, 1, dx/tex_width, dy/tex_height, (dx+dw)/tex_width, (dy+dh)/tex_height);
				System.arraycopy(data, 0, geodata, (l*TEXTURE_LINE_LEN+c)*4*4, 4*4);

				line_offset += char_widths[l*TEXTURE_LINE_LEN+c]+char_spacing;
			}
		}
		geometry = drawContext.createUnifiedDrawCall(CHARACTER_COUNT*4, "text-drawer", font_tex, geodata);
		geometry.forceNoCache();
	}

	public TextDrawer derive(EFontSize size) {
		return new SizedTextDrawer(size);
	}


	private class SizedTextDrawer implements TextDrawer {

		private final float widthFactor;
		private final float lineHeight;

		private SizedTextDrawer(EFontSize size) {
			lineHeight = heightPerSize[size.ordinal()]*scalingFactor;
			widthFactor = lineHeight/gentex_line_height;
		}

		private void drawChar(float x, float y, AbstractColor color, char c) {
			geometry.offset = indexOf(c)*4;
			geometry.drawComplexQuad(EUnifiedMode.TEXTURE, x, y, 0, lineHeight, lineHeight, color, 1);
			geometry.flush();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see go.graphics.swing.text.TextDrawer#drawString(int, int, java.lang.String)
		 */
		@Override
		public void drawString(float x, float y, AbstractColor color, String string) {
			float x_offset = 0;
			float y_offset = 0;

			for(int i = 0;i != string.length();i++) {
				if(string.charAt(i) == '\n') {
					y_offset += lineHeight;
					x_offset = 0;
				} else {
					drawChar(x+x_offset, y+y_offset, color, string.charAt(i));
					x_offset += char_widths[indexOf(string.charAt(i))]*widthFactor;
				}
			}
		}

		private int indexOf(char c) {
			int indexOf = CHARACTERS.indexOf(c);
			if(indexOf == -1) return indexOf('?');
			return indexOf;
		}

		@Override
		public float getWidth(String string) {
			float tmp_width = 0;
			for(int i = 0;i != string.length();i++) {
				if(string.charAt(i) != '\n') {
					tmp_width += char_widths[indexOf(string.charAt(i))]*widthFactor;
				}
			}
			return tmp_width;
		}

		@Override
		public float getHeight(String string) {
			float tmp_height = lineHeight;
			for(int i = 0;i != string.length();i++) {
				if(string.charAt(i) == '\n') {
					tmp_height += lineHeight;
				}
			}
			return tmp_height;
		}
	}
}
