package jsettlers.graphics.map.controls;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.mouse.GODrawEvent;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.map.IMapInterfaceListener;

/**
 * Classes that implement this are capable of displaying the full game controls
 * (minimap, ...) on the screen.
 * 
 * @author michael
 */
public interface IControls extends IMapInterfaceListener {

	void drawAt(GLDrawContext gl);

	void resizeTo(int newWidth, int newHeight);

	boolean containsPoint(UIPoint position);

	String getDescriptionFor(UIPoint position);

	void setMapViewport(MapRectangle screenArea);

	Action getActionFor(UIPoint position);

	/**
	 * Handles a draw event. The event may be fired even if it is outside the
	 * interface.
	 * 
	 * @param event
	 *            The event to handle
	 * @return If the event was handled.
	 */
	boolean handleDrawEvent(GODrawEvent event);
}
