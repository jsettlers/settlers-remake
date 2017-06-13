/*******************************************************************************
 * Copyright (c) 2015 - 2017
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

import com.jogamp.opengl.util.awt.TextRenderer;
import go.graphics.swing.opengl.JOGLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

/**
 * This class is a text drawer used to wrap the text renderer.
 *
 * @author michael
 */
public final class JOGLTextDrawer implements TextDrawer {

	private static final int INTERVAL_SCALING_UPDATE = 10000;
	private static final int DEFAULT_DPI = 96;
	private static final String FONTNAME = "Arial";

	private static ArrayList<JOGLTextDrawer> JOGLTextDrawers = new ArrayList<JOGLTextDrawer>();
	private static float scalingFactor = 1;

	static {
		Timer scalingUpdateTimer = new Timer();
		scalingUpdateTimer.schedule(new TimerTask() {
			public void run() {
				scalingFactor = Toolkit.getDefaultToolkit().getScreenResolution();
				scalingFactor /= DEFAULT_DPI;
				scalingFactor = Math.min(scalingFactor, 1);
				for (JOGLTextDrawer drawer : JOGLTextDrawers) {
					drawer.renderer.getFont().
				}
			}
		}, 0, INTERVAL_SCALING_UPDATE);
	}


	private final TextRenderer renderer;

	private final JOGLDrawContext drawContext;

	/**
	 * Creates a new text drawer.
	 *
	 * @param size
	 *            The size of the text.
	 * @param drawContext
	 */
	public JOGLTextDrawer(EFontSize size, JOGLDrawContext drawContext) {
		this.drawContext = drawContext;
		int scaledFontSize = Math.round(size.getSize() * scalingFactor);
		Font font = new Font(FONTNAME, Font.TRUETYPE_FONT, scaledFontSize);
		this.renderer = new TextRenderer(font, true, true, null, true);
		JOGLTextDrawers.add(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see go.graphics.swing.text.TextDrawer#renderCentered(int, int, java.lang.String)
	 */
	@Override
	public void renderCentered(float cx, float cy, String text) {
		Rectangle2D textBounds = this.renderer.getBounds(text);
		int halfWidth = (int) (textBounds.getWidth() / 2);
		int halfHeight = (int) (textBounds.getHeight() / 2);
		drawString(cx - halfWidth, cy - halfHeight, text);
	}

	/**
	 * TODO: we should remove this.
	 */
	public void setColor(float red, float green, float blue, float alpha) {
		this.renderer.setColor(red, green, blue, alpha);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see go.graphics.swing.text.TextDrawer#drawString(int, int, java.lang.String)
	 */
	@Override
	public void drawString(float x, float y, String string) {
		try {
			this.drawContext.prepareFontDrawing();
			this.renderer.begin3DRendering();
			this.renderer.draw3D(string, x, y, 0, 1);
			this.renderer.end3DRendering();
			this.renderer.flush();
		} catch (Throwable e) {
			// bad
		}
	}

	@Override
	public float getWidth(String string) {
		Rectangle2D textBounds = this.renderer.getBounds(string);
		return (float) textBounds.getWidth();
	}

	@Override
	public float getHeight(String string) {
		Rectangle2D textBounds = this.renderer.getBounds(string);
		return (float) textBounds.getHeight();
	}
}
