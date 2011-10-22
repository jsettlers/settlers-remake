package go.graphics.swing.text;

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
	
	private static JOGLTextDrawer[] instances =
	        new JOGLTextDrawer[EFontSize.values().length];

	private final TextRenderer renderer;

	/**
	 * Creates a new text drawer.
	 * 
	 * @param size
	 *            The size of the text.
	 */
	private JOGLTextDrawer(EFontSize size) {
		Font font = new Font(FONTNAME, Font.TRUETYPE_FONT, size.getSize());
		this.renderer =
		        new TextRenderer(font, true, true, null, true);
	}

	/* (non-Javadoc)
     * @see go.graphics.swing.text.TextDrawer#renderCentered(int, int, java.lang.String)
     */
	@Override
    public void renderCentered(int cx, int cy, String text) {
		Rectangle2D textBounds = this.renderer.getBounds(text);
		int halfWidth = (int) (textBounds.getWidth() / 2);
		int halfHeight = (int) (textBounds.getHeight() / 2);
		drawString(cx - halfWidth, cy - halfHeight, text);
	}

	/* (non-Javadoc)
     * @see go.graphics.swing.text.TextDrawer#drawString(int, int, java.lang.String)
     */
	@Override
    public void drawString(int x, int y, String string) {
		this.renderer.setColor(1, 1, 1, 1);
		this.renderer.begin3DRendering();
		this.renderer.draw3D(string, x, y, 0, 1);
		this.renderer.end3DRendering();
		this.renderer.flush();
	}

	/**
	 * Gets a text drawer for the given text size.
	 * 
	 * @param size
	 *            The size for the drawer.
	 * @return An instance of a drawer for that size.
	 */
	public static TextDrawer getTextDrawer(EFontSize size) {
		if (instances[size.ordinal()] == null) {
			instances[size.ordinal()] = new JOGLTextDrawer(size);
		}
		return instances[size.ordinal()];
	}
}
