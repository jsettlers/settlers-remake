package jsettlers.graphics.utils;

import java.awt.geom.Rectangle2D;

import jsettlers.common.position.IntRectangle;

import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * This class is a text drawer used to wrap the text renderer.
 * 
 * @author michael
 */
public final class TextDrawer {

	private static TextDrawer[] instances =
	        new TextDrawer[EFontSize.values().length];

	private final TextRenderer renderer;

	/**
	 * Creates a new text drawer.
	 * 
	 * @param size
	 *            The size of the text.
	 */
	private TextDrawer(EFontSize size) {
		this.renderer =
		        new TextRenderer(size.getFont(), true, true, null, true);
	}

	/**
	 * Renders the given text at the center of the rect.
	 * 
	 * @param rect
	 *            The rect to render at. It is not guaranteed that the text does
	 *            not exceed the borders.
	 * @param text
	 *            The text to render.
	 */
	public void renderCentered(IntRectangle rect, String text) {
		renderCentered(rect.getCenterX(), rect.getCenterY(), text);
	}

	/**
	 * Renders the given text centered around cx, cy.
	 * 
	 * @param cx
	 *            The center in x direction.
	 * @param cy
	 *            The center in y direction.
	 * @param text
	 *            The text to render.
	 */
	public void renderCentered(int cx, int cy, String text) {
		Rectangle2D textBounds = this.renderer.getBounds(text);
		int halfWidth = (int) (textBounds.getWidth() / 2);
		int halfHeight = (int) (textBounds.getHeight() / 2);
		drawString(cx - halfWidth, cy - halfHeight, text);
	}

	/**
	 * Draws a string
	 * 
	 * @param x
	 *            Left bound.
	 * @param y
	 *            Bottom line.
	 * @param string
	 *            The string to render
	 */
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
			instances[size.ordinal()] = new TextDrawer(size);
		}
		return instances[size.ordinal()];
	}
}
