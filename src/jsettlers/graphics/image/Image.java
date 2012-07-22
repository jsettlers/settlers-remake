package jsettlers.graphics.image;

import go.graphics.GLDrawContext;
import jsettlers.common.Color;
import jsettlers.graphics.map.draw.DrawBuffer;

public abstract class Image {

	/**
	 * Convenience method, calls drawAt(gl, x, y, -1);
	 * 
	 * @param gl
	 *            The context.
	 * @param x
	 *            The x position of the center.
	 * @param y
	 *            The y position of the center
	 */
	public abstract void drawAt(GLDrawContext gl, float x, float y);

	/**
	 * Draws an object for a given player. The player -1 means no player.
	 * 
	 * @param gl
	 *            The gl context.
	 * @param x
	 *            The x coordinate on the screen.
	 * @param y
	 *            The y coordinate on the screen.
	 * @param color
	 *            The player number.
	 */
	public abstract void drawAt(GLDrawContext gl, float x, float y, Color color);

	/**
	 * Draws the image around 0,0 with the given color.
	 * 
	 * @param gl
	 *            The gl context
	 * @param color
	 *            The color to use. If it is <code>null</code>, white is used.
	 */
	public abstract void draw(GLDrawContext gl, Color color);

	/**
	 * Draws the image around 0,0 with the given color.
	 * 
	 * @param gl
	 *            The gl context
	 * @param color
	 *            The color to use. If it is <code>null</code>, white is used.
	 * @param multiply
	 *            A number to multiply all color values with.
	 */
	public abstract void draw(GLDrawContext gl, Color color, float multiply);

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract void drawImageAtRect(GLDrawContext gl, float minX,
	        float minY, float maxX, float maxY);

	public abstract void drawAt(GLDrawContext gl, DrawBuffer buffer,
	        float viewX, float viewY, int iColor);

	public void drawAt(GLDrawContext gl, DrawBuffer buffer, float viewX,
	        float viewY, Color color, float multiply) {
		int iColor;
		if (multiply == 1) {
			iColor = color.getARGB();
		} else {
			iColor =
			        Color.getARGB(color.getRed() * multiply, color.getGreen()
			                * multiply, color.getBlue() * multiply,
			                color.getAlpha());
		}
		drawAt(gl, buffer, viewX, viewY, iColor);

	}

}