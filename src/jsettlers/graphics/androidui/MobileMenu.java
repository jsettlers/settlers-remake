package jsettlers.graphics.androidui;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;

/**
 * This is the main mobile menu interface.
 * @author michael
 *
 */
public interface MobileMenu {

	/**
	 * Called on every frame to render the Menu to the screen, if the menu does
	 * not do this otherwise.
	 * 
	 * @param gl
	 *            The GL context.
	 */
	void drawAt(GLDrawContext gl);

	/**
	 * Gets the action if an User clocks on a given UI position.
	 * 
	 * @param position
	 *            The positon the user clicked on.
	 * @return The action for that point.
	 */
	Action getActionFor(UIPoint position);

	/**
	 * Sets the position relative to the screen.
	 * 
	 * @param floatRectangle
	 *            The position relative to the (0, 0, 1, 1) rect.
	 */
	void setPosition(FloatRectangle floatRectangle);

	/**
	 * Called when the menu is open and should then be displayed on the screen.
	 */
	void show();

	/**
	 * Called when the menu is going to be hidden.
	 */
	void hide();

}
