package jsettlers.graphics.utils;

import go.graphics.GLDrawContext;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;

public interface UIElement {
	void setPosition(FloatRectangle position);

	/**
	 * Draws the element at the given position.
	 * @param gl
	 */
	void drawAt(GLDrawContext gl);
	
	Action getAction(float relativex, float relativey);

	String getDescription(float relativex, float relativey);
}
