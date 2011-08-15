package jsettlers.graphics.utils;

import javax.media.opengl.GL2;

import jsettlers.common.position.IntRectangle;

/**
 * This is an item that can be displayed in an ui list.
 * @author michael
 *
 */
public interface UIListItem {
	/**
	 * Gets the height of this item
	 * @return The height
	 */
	int getHeight();
	
	/**
	 * Draws the item at a given position
	 * @param rect The rect to draw at.
	 */
	void drawAt(GL2 gl, IntRectangle rect);
	
}
