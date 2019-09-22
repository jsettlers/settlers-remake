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

import go.graphics.swing.opengl.LWJGLDrawContext;
import go.graphics.text.AbstractTextDrawer;
import go.graphics.text.EFontSize;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public final class LWJGLTextDrawer extends AbstractTextDrawer<LWJGLDrawContext> {
	private static final int DEFAULT_DPI = 96;
	private static final Font FONT = new Font("Arial", Font.PLAIN, TEXTURE_GENERATION_SIZE);

	/**
	 * Creates a new text drawer.
	 *
	 */
	public LWJGLTextDrawer(LWJGLDrawContext drawContext, float guiScale) {
		super(drawContext, guiScale);
	}

	@Override
	protected float calculateScalingFactor() {
		int screenDPI = Toolkit.getDefaultToolkit().getScreenResolution();
		return Math.max((float) (screenDPI / DEFAULT_DPI), 1);
	}

	@Override
	protected int init() {
		BufferedImage tmp_bi = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics tmp_graph = tmp_bi.getGraphics();
		tmp_graph.setFont(FONT);
		FontMetrics fm = tmp_graph.getFontMetrics();
		for(int i = 0;i != CHARACTER_COUNT; i++) char_widths[i] = fm.charWidth(CHARACTERS.charAt(i));
		gentex_line_height = fm.getHeight();

		EFontSize[] values = EFontSize.values();
		for(int i = 0; i != values.length; i++) {
			tmp_graph.setFont(FONT.deriveFont(values[i].getSize()));
			heightPerSize[i] = tmp_graph.getFontMetrics().getHeight();
		}

		tmp_graph.dispose();

		return fm.getDescent();
	}

	@Override
	protected int[] getRGB() {
		return pre_render.getRGB(0, 0, tex_width, tex_height, null, 0, tex_width);
	}

	private BufferedImage pre_render;
	private Graphics2D graph;

	@Override
	protected void setupBitmapDraw() {
		pre_render = new BufferedImage(tex_width, tex_height, BufferedImage.TYPE_INT_ARGB);
		graph = pre_render.createGraphics();
		graph.setColor(Color.WHITE);
		graph.setFont(FONT);
	}

	@Override
	protected void drawChar(char[] character, int x, int y) {
		graph.drawChars(character, 0, 1, x, y);
	}

	@Override
	protected void endDraw() {
		graph.dispose();
		graph = null;
		pre_render = null;
	}

	@Override
	protected void setTexParams() {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	}
}
