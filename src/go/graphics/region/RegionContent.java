package go.graphics.region;

import go.graphics.GLDrawContext;
import go.graphics.event.GOEventHandlerProvoder;

/**
 * A region content is someting that can be drawn in a region
 * @author michael
 *
 */
public interface RegionContent extends GOEventHandlerProvoder {
	/**
	 * Draws the content at coordinates (0,0) to (width,height)
	 * @param gl2 The gl context to draw on.
	 * @param width The width of the region
	 * @param height The height of the region.
	 */
	public void drawContent(GLDrawContext gl2, int width, int height);
}
