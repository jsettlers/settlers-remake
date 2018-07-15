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
package go.graphics.swing.text;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import go.graphics.EGeometryFormatType;
import go.graphics.EGeometryType;
import go.graphics.GeometryHandle;
import go.graphics.SharedGeometry;
import go.graphics.TextureHandle;
import go.graphics.swing.opengl.LWJGLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.nio.ShortBuffer;

/**
 * This class is a text drawer used to wrap the text renderer.
 *
 * @author michael
 * @author paul
 */
public final class LWJGLTextDrawer {

	private static final String FONTNAME = "Arial";
	private static final int TEXTURE_GENERATION_SIZE = 30;

	private static final int    DEFAULT_DPI = 96;
	private static final float  SCALING_FACTOR = calculateScalingFactor();

	private GeometryHandle geometry;
	private TextureHandle font_tex;
	private final int gentex_line_height;
	private int tex_height;
	private int tex_width;
	private final int[] char_widths;

	private final static int char_spacing = 2; // spacing between two characters (otherwise j and f would overlap with the next character)

	private Color color = Color.WHITE;

	private final LWJGLDrawContext drawContext;

	private static float calculateScalingFactor() {
		int screenDPI = Toolkit.getDefaultToolkit().getScreenResolution();
		return Math.max((float) (screenDPI / DEFAULT_DPI), 1);
	}

	private final Font font;

	/**
	 * Creates a new text drawer.
	 *
	 */
	public LWJGLTextDrawer(LWJGLDrawContext drawContext) {
		this.drawContext = drawContext;
		font = new Font(FONTNAME, Font.PLAIN, TEXTURE_GENERATION_SIZE);

		BufferedImage tmp_bi = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics tmp_graph = tmp_bi.getGraphics();
		tmp_graph.setFont(font);
		FontMetrics fm = tmp_graph.getFontMetrics();
		char_widths = fm.getWidths();
		gentex_line_height = fm.getHeight();
		tmp_graph.dispose();

		if(char_widths.length != 256) {
			throw new IndexOutOfBoundsException("we only support 256 characters (256!="+char_widths.length);
		}

		generateTexture();
		generateGeometry(fm.getDescent());
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

		BufferedImage pre_render = new BufferedImage(tex_width, tex_height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graph = pre_render.createGraphics();
		graph.setColor(Color.WHITE);
		graph.setFont(font);

		for(int l = 0;l != 16;l++) {
			int line_offset = 0;
			for (int c = 0; c != 16; c++) {
				graph.drawChars(new char[]{(char) (l * 16 + c)}, 0, 1, line_offset, l * gentex_line_height);
				line_offset += char_widths[l*16+c]+char_spacing;
			}
		}
		graph.dispose();

		short[] short_tex_data = new short[tex_width*tex_height];

		final short alpha_channel = 0b1111;
		final short alpha_white = ~alpha_channel;
		for(int x = 0;x != tex_width;x++) {
			for (int y = 0; y != tex_height; y++) {
				int pixel = pre_render.getRGB(x, tex_height-y-1);

				short a = (short) ((pixel >> 24) != 0 ? alpha_channel : 0);
				short_tex_data[y*tex_width+x] = (short) (a | alpha_white);
			}
		}
		ShortBuffer bfr = ShortBuffer.wrap(short_tex_data);

		font_tex = drawContext.generateTexture(max_len, tex_height, bfr, font.getName());

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_LINEAR);
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

				float[] data = SharedGeometry.createQuadGeometry(0, 0,SCALING_FACTOR*dw/(float)gentex_line_height, SCALING_FACTOR, dx/tex_width, dy/tex_height, (dx+dw)/tex_width, (dy+dh)/tex_height);
				System.arraycopy(data, 0, geodata, (l*16+c)*4*4, 4*4);

				line_offset += char_widths[l*16+c]+char_spacing;
			}
		}
		geometry = drawContext.storeGeometry(geodata, EGeometryFormatType.Texture2D, font.getName());
	}

	public TextDrawer derive(EFontSize size) {
		return new SizedLWJGLTextDrawer(size);
	}


	private class SizedLWJGLTextDrawer implements TextDrawer {

		private final float widthFactor;
		private final int line_height;
		private final Font sizedFont;

		private SizedLWJGLTextDrawer(EFontSize size) {
			sizedFont = font.deriveFont(size.getSize());

			BufferedImage tmp_bi = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics tmp_graph = tmp_bi.getGraphics();
			tmp_graph.setFont(sizedFont);
			FontMetrics fm = tmp_graph.getFontMetrics();
			line_height = fm.getHeight();
			widthFactor = line_height/(float)gentex_line_height;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see go.graphics.swing.text.TextDrawer#renderCentered(int, int, java.lang.String)
		 */
		@Override
		public void renderCentered(float cx, float cy, String text) {
			drawString(cx-(getWidth(text)/2), cy-(getHeight(text)/2), text);
		}

		/**
		 * TODO: we should remove this.
		 */
		public void setColor(float red, float green, float blue, float alpha) {
			color = new Color(red, green, blue, alpha);
		}

		public void drawChar(float x, float y, char c) {
			drawContext.color(color.getRed()/255, color.getGreen()/255, color.getBlue()/255, color.getAlpha()/255);
			drawContext.glPushMatrix();
			drawContext.glTranslatef(x, y, 0);
			drawContext.glScalef(line_height, line_height, 0);
			drawContext.draw2D(geometry, font_tex, EGeometryType.Quad, c, 4);
			drawContext.glPopMatrix();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see go.graphics.swing.text.TextDrawer#drawString(int, int, java.lang.String)
		 */
		@Override
		public void drawString(float x, float y, String string) {
			float x_offset = 0;
			float y_offset = 0;

			for(int i = 0;i != string.length();i++) {
				if(string.charAt(i) == '\n') {
					y_offset += line_height;
				} else {
					drawChar(x+x_offset, y+y_offset, string.charAt(i));
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
			int tmp_height = line_height;
			for(int i = 0;i != string.length();i++) {
				if(string.charAt(i) == '\n') {
					tmp_height += line_height;
				}
			}
			return tmp_height;
		}
	}
}
