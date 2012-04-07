package go.graphics.swing.text;

import go.graphics.swing.opengl.JOGLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.awt.Font;
import java.awt.geom.Rectangle2D;

import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * This class is a text drawer used to wrap the text renderer.
 * 
 * @author michael
 */
public final class JOGLTextDrawer implements TextDrawer {

	private static final String FONTNAME = "Arial";

	private static JOGLTextDrawer[] instances = new JOGLTextDrawer[EFontSize
	        .values().length];

	private final TextRenderer renderer;

	private final JOGLDrawContext drawContext;

	/**
	 * Creates a new text drawer.
	 * 
	 * @param size
	 *            The size of the text.
	 * @param drawContext 
	 */
	private JOGLTextDrawer(EFontSize size, JOGLDrawContext drawContext) {
		this.drawContext = drawContext;
		Font font = new Font(FONTNAME, Font.TRUETYPE_FONT, size.getSize());
		this.renderer = new TextRenderer(font, true, true, null, true);
	}

	/*
	 * (non-Javadoc)
	 * @see go.graphics.swing.text.TextDrawer#renderCentered(int, int,
	 * java.lang.String)
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
	 * @see go.graphics.swing.text.TextDrawer#drawString(int, int,
	 * java.lang.String)
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
			//bad
		}
	}

	/**
	 * Gets a text drawer for the given text size.
	 * 
	 * @param size
	 *            The size for the drawer.
	 * @return An instance of a drawer for that size.
	 */
	public static TextDrawer getTextDrawer(EFontSize size, JOGLDrawContext drawContext) {
		if (instances[size.ordinal()] == null) {
			instances[size.ordinal()] = new JOGLTextDrawer(size, drawContext);
		}
		return instances[size.ordinal()];
	}

	@Override
	public double getWidth(String string) {
		Rectangle2D textBounds = this.renderer.getBounds(string);
		return textBounds.getWidth();
	}

	@Override
	public double getHeight(String string) {
		Rectangle2D textBounds = this.renderer.getBounds(string);
		return textBounds.getHeight();
	}
}
