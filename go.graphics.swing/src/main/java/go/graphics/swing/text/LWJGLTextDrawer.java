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

import go.graphics.GeometryHandle;
import go.graphics.IllegalBufferException;
import go.graphics.TextureHandle;
import go.graphics.swing.opengl.LWJGLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ShortBuffer;

/**
 * This class is a text drawer used to wrap the text renderer.
 *
 * @author michael
 * @author paul
 */
public final class LWJGLTextDrawer implements TextDrawer {

	private static final String FONTNAME = "Arial";

	private final GeometryHandle geometry;
	private final TextureHandle font_tex;
	private final int line_height;
	private final int tex_height;
	private final int tex_width;
	private final int[] char_widths;

	private final static int char_spacing = 2; // spacing between two characters (otherwise j and f would overlap with the next character)

	private Color color = Color.WHITE;

	private final LWJGLDrawContext drawContext;

	/**
	 * Creates a new text drawer.
	 * 
	 * @param size
	 *            The size of the text.
	 * @param drawContext
	 */
	public LWJGLTextDrawer(EFontSize size, LWJGLDrawContext drawContext) {
		this.drawContext = drawContext;
		Font font = new Font(FONTNAME, Font.TRUETYPE_FONT, size.getSize());

		BufferedImage tmp_bi = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics tmp_graph = tmp_bi.getGraphics();
		tmp_graph.setFont(font);
		FontMetrics fm = tmp_graph.getFontMetrics();
		char_widths = fm.getWidths();
		line_height = fm.getHeight();
		tmp_graph.dispose();


		if(char_widths.length != 256) {
			throw new IndexOutOfBoundsException("we only support 256 characters (256!="+char_widths.length);
		}

		int max_len = 0;
		for(int l = 0;l != 16;l++) {
			int current_len = 0;
			for(int c = 0;c != 16;c++) {
				current_len += char_widths[l*16+c]+char_spacing;
				max_len = Math.max(max_len, current_len);
			}
		}

		tex_width = max_len;
		tex_height = line_height*16;
		BufferedImage pre_render = new BufferedImage(tex_width, tex_height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graph = pre_render.createGraphics();
		graph.setColor(Color.WHITE);
		graph.setFont(font);

		float[] geodata = new float[256*5*4];
		for(int l = 0;l != 16;l++) {
			int line_offset = 0;
			for(int c = 0;c != 16;c++) {
				graph.drawChars(new char[]{(char)(l*16+c)}, 0, 1, line_offset, l*line_height);

				float dx = line_offset;
				float dy = tex_height-(l*line_height+fm.getDescent());

				float dw = char_widths[l*16+c];
				float dh = line_height;

				float[] data = new float[] {
						0, 	0,  0, dx/tex_width		, dy/tex_height,
						0, 	dh, 0, dx/tex_width		, (dy+dh)/tex_height,
						dw, dh, 0, (dx+dw)/tex_width, (dy+dh)/tex_height,
						dw, 0,  0, (dx+dw)/tex_width, dy/tex_height
				};
				System.arraycopy(data, 0, geodata, (l*16+c)*5*4, 5*4);

				line_offset += char_widths[l*16+c]+char_spacing;
			}
		}
		geometry = drawContext.storeGeometry(geodata);
		graph.dispose();

		short[] short_tex_data = new short[tex_width*tex_height];

		double f85 = 31.0 / 255.0;

		for(int x = 0;x != tex_width;x++) {
			for (int y = 0; y != tex_height; y++) {
				int pixel = pre_render.getRGB(x, tex_height-y-1);
				short b = (short) ((pixel&0xFF)*f85);
				short g = (short) (((pixel >> 8) & 0xFF)*f85);
				short r = (short) (((pixel >> 16) & 0xFF)*f85);
				short a = (short) ((pixel >> 24) != 0 ? 1 : 0);
				short_tex_data[y*tex_width+x] = (short) ((a&1) | (b<<1) | (g<<6) | (r<<11));
			}
		}
		ShortBuffer bfr = ShortBuffer.wrap(short_tex_data);

		font_tex = drawContext.generateTexture(max_len, tex_height, bfr);
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
		try {
			drawContext.drawQuadWithTexture(font_tex, geometry, c);
		} catch (IllegalBufferException e) {
			e.printStackTrace();
		}
		drawContext.glPopMatrix();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see go.graphics.swing.text.TextDrawer#drawString(int, int, java.lang.String)
	 */
	@Override
	public void drawString(float x, float y, String string) {
		this.drawContext.prepareFontDrawing();

		int x_offset = 0;
		int y_offset = 0;

		for(int i = 0;i != string.length();i++) {
			if(string.charAt(i) == '\n') {
				y_offset += line_height;
			} else {
				drawChar(x+x_offset, y+y_offset, string.charAt(i));
				x_offset += char_widths[string.charAt(i)];
			}
		}
	}

	@Override
	public float getWidth(String string) {
		int tmp_width = 0;
		for(int i = 0;i != string.length();i++) {
			if(string.charAt(i) != '\n') {
				tmp_width += char_widths[string.charAt(i)];
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
