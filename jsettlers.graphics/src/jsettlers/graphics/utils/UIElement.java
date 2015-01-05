package jsettlers.graphics.utils;

import go.graphics.GLDrawContext;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;

public interface UIElement {
	void setPosition(FloatRectangle position);

	/**
	 * Draws the element at the given position.
	 * 
	 * @param gl
	 */
	void drawAt(GLDrawContext gl);

	Action getAction(float relativex, float relativey);

	String getDescription(float relativex, float relativey);

	/**
	 * Called once to indicate that this element is not attached to the gui and could be visible.
	 */
	void onAttach();

	/**
	 * The opposite of {@link #onAttach()}.
	 */
	void onDetach();
}
