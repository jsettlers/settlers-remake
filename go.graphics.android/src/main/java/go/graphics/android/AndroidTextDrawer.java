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
package go.graphics.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES20;

import go.graphics.AbstractColor;
import go.graphics.EUnifiedMode;
import go.graphics.GLDrawContext;
import go.graphics.TextureHandle;
import go.graphics.UnifiedDrawHandle;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * This class is a text drawer used to wrap the text renderer.
 *
 * @author michael
 * @author paul
 */
public final class AndroidTextDrawer {

	private static final int TEXTURE_GENERATION_SIZE = 30;

	private final float scaling_factor;

	private UnifiedDrawHandle geometry;
	private TextureHandle font_tex;
	private final int gentex_line_height;
	private int tex_height;
	private int tex_width;
	private final float[] char_widths;

	private final static int char_spacing = 2; // spacing between two characters (otherwise j and f would overlap with the next character)

	private final GLESDrawContext drawContext;

	private final Paint paint;

	/**
	 * Creates a new text drawer.
	 *
	 */
	public AndroidTextDrawer(GLESDrawContext drawContext) {
		this.drawContext = drawContext;
		scaling_factor = drawContext.getAndroidContext().getResources().getDisplayMetrics().density;

		paint = new Paint();
		paint.setTextSize(TEXTURE_GENERATION_SIZE);

		char_widths = new float[256];
		StringBuilder triggerString = new StringBuilder();
		for(char i = 0;i != 256; i++) {
			triggerString.append(i);
		}
		paint.getTextWidths(triggerString.toString(), char_widths);

		Paint.FontMetricsInt fm = paint.getFontMetricsInt();
		gentex_line_height = fm.leading-fm.ascent+fm.descent;

		generateTexture();
		generateGeometry(fm.descent);
	}

	private int getMaxLen() {
		int max_len = 0;
		for(int l = 0;l != 16;l++) {
			int current_len = 0;
			for(int c = 0;c != 16;c++) {
				current_len += char_widths[l*16+c]+char_spacing;
				max_len = Math.max(max_len, current_len);
			}
		}
		return max_len;
	}

	private void generateTexture() {
		int max_len = getMaxLen();

		tex_width = max_len;
		tex_height = gentex_line_height*16;

		Bitmap pre_render = Bitmap.createBitmap(tex_width, tex_height, Bitmap.Config.ALPHA_8);
		Canvas canvas = new Canvas(pre_render);
		paint.setColor(0);
		canvas.drawPaint(paint);
		paint.setColor(0xFFFFFFFF);

		for(int l = 0;l != 16;l++) {
			int line_offset = 0;
			for (int c = 0; c != 16; c++) {
				canvas.drawText(new char[]{(char) (l * 16 + c)}, 0, 1, line_offset, l * gentex_line_height, paint);
				line_offset += char_widths[l*16+c]+char_spacing;
			}
		}

		ShortBuffer bfr = ByteBuffer.allocateDirect(tex_width*tex_height*2).order(ByteOrder.nativeOrder()).asShortBuffer();

		int[] pixels = new int[tex_width*tex_height];
		pre_render.getPixels(pixels, 0, tex_width, 0, 0, tex_width, tex_height);

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
		font_tex = drawContext.generateTexture(max_len, tex_height, bfr, "");

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
				GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
				GLES20.GL_LINEAR);
	}

	private void generateGeometry(int descent) {
		float[] geodata = new float[256*4*4];
		for(int l = 0;l != 16;l++) {
			int line_offset = 0;
			for (int c = 0; c != 16; c++) {

				float dx = line_offset;
				float dy = tex_height-(l*gentex_line_height+descent);

				float dw = char_widths[l*16+c];
				float dh = gentex_line_height;

				float[] data = GLDrawContext.createQuadGeometry(0, 0,dw/(float)gentex_line_height, 1, dx/tex_width, dy/tex_height, (dx+dw)/tex_width, (dy+dh)/tex_height);
				System.arraycopy(data, 0, geodata, (l*16+c)*4*4, 4*4);

				line_offset += char_widths[l*16+c]+char_spacing;
			}
		}
		geometry = drawContext.createUnifiedDrawCall(256*4, "android-font", font_tex, geodata);
	}

	public TextDrawer derive(EFontSize size) {
		return new SizedAndroidTextDrawer(size);
	}


	private class SizedAndroidTextDrawer implements TextDrawer {

		private final float widthFactor;
		private final float line_height;
		private final Paint sizedFont;

		private SizedAndroidTextDrawer(EFontSize size) {
			sizedFont = new Paint(paint);
			sizedFont.setTextSize(size.getSize());

			Paint.FontMetricsInt fm = sizedFont.getFontMetricsInt();

			line_height = (fm.leading-fm.ascent+fm.descent)* scaling_factor;
			widthFactor = line_height/gentex_line_height;
		}

		private void drawChar(float x, float y, AbstractColor color, char c) {
			geometry.offset = c*4;
			geometry.drawComplexQuad(EUnifiedMode.TEXTURE, x, y, 0, line_height, line_height, color, 1);
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
					y_offset += line_height;
				} else {
					drawChar(x+x_offset, y+y_offset, color, string.charAt(i));
					x_offset += char_widths[string.charAt(i)]*widthFactor;
				}
			}
		}

		@Override
		public float getWidth(String string) {
			float tmp_width = 0;
			for(int i = 0;i != string.length();i++) {
				if(string.charAt(i) != '\n') {
					tmp_width += char_widths[string.charAt(i)]*widthFactor;
				}
			}
			return tmp_width;
		}

		@Override
		public float getHeight(String string) {
			float tmp_height = line_height;
			for(int i = 0;i != string.length();i++) {
				if(string.charAt(i) == '\n') {
					tmp_height += line_height;
				}
			}
			return tmp_height;
		}
	}
}
