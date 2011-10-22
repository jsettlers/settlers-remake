package go.graphics.text;

public interface TextDrawer {

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
	public abstract void renderCentered(int cx, int cy, String text);

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
	public abstract void drawString(int x, int y, String string);

}