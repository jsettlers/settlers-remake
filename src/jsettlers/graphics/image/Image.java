package jsettlers.graphics.image;

import go.graphics.Color;
import go.graphics.GLDrawContext;

public interface Image {

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
	 * Draws the image around the center of the given gl context.
	 * 
	 * @param gl
	 *            The gl context
	 */
	public abstract void draw(GLDrawContext gl);

	/**
	 * Draws the image around 0,0 with the given color.
	 * 
	 * @param gl
	 *            The gl context
	 * @param color
	 *            The color to use. If it is <code>null</code>, white is used.
	 */
	public abstract void draw(GLDrawContext gl, Color color);
	
	public int getWidth();
	public int getHeight();

}