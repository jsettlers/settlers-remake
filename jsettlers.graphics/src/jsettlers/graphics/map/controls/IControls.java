package jsettlers.graphics.map.controls;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.mouse.GODrawEvent;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapDrawContext;

/**
 * Classes that implement this are capable of displaying the full game controls
 * (minimap, ...) on the screen.
 *
 * @author michael
 */
public interface IControls extends IMapInterfaceListener {

	/**
	 * Draws the controls on the screen.
	 *
	 * @param gl
	 *            The gl context to draw at.
	 */
	void drawAt(GLDrawContext gl);

	/**
	 * Called when the screen was resized.
	 *
	 * @param newWidth
	 *            The new width of the screen
	 * @param newHeight
	 *            The new height
	 */
	void resizeTo(float newWidth, float newHeight);

	/**
	 * Checks if a point is on the ui
	 *
	 * @param position
	 *            The position in screen space to check.
	 * @return true if the point is in the ui.
	 */
	boolean containsPoint(UIPoint position);

	/**
	 * Gets the description for a given point, e.g. for tooltipps. May be called
	 * for any position.
	 *
	 * @param position
	 *            The position
	 * @return A string describing whatever there is on the ui. May be null.
	 */
	String getDescriptionFor(UIPoint position);

	/**
	 * Called whenever the map viewport changes.
	 *
	 * @param screenArea
	 *            The new area of the map.
	 */
	void setMapViewport(MapRectangle screenArea);

	/**
	 * Gets the action for the given ui position, that should be executed if the
	 * user clicked it.
	 *
	 * @param position
	 *            The positon.
	 * @param selecting
	 *            If the event is a select event.
	 * @return The action for the position.
	 */
	Action getActionFor(UIPoint position, boolean selecting);

	/**
	 * Handles a draw event. The event may be fired even if it is outside the
	 * interface.
	 *
	 * @param event
	 *            The event to handle
	 * @return If the event was handled.
	 */
	boolean handleDrawEvent(GODrawEvent event);

	/**
	 * Indicates that the user builds the building.
	 *
	 * @param type
	 *            The type the user wants to build.
	 */
	void displayBuildingBuild(EBuildingType type);

	/**
	 * Changes the selection for the map.
	 *
	 * @param selection
	 *            the selections.
	 */
	void displaySelection(ISelectionSet selection);

	/**
	 * Gives the ui access to the draw context that is used to draw the map.
	 * 
	 * @param actionFireable
	 *            An object we can fire actions to when we want to initiate an
	 *            action ourselves.
	 * @param context
	 *            The map context used.
	 */
	void setDrawContext(ActionFireable actionFireable, MapDrawContext context);

	/**
	 * Allows the controls to catch an action the gui would fire.
	 * <p>
	 * This can also be used for status info.
	 *
	 * @param action
	 *            The action.
	 * @return The new action to send. This is often just the old action.
	 */
	Action replaceAction(Action action);

	/**
	 * Gets a tooltip for the given map position.
	 * 
	 * @param point
	 *            The x/y coordinate, may be outside map.
	 * @return The tooltip text, or an empty String or <code>null</code> if
	 *         nothing should be displayed.
	 */
	String getMapTooltip(ShortPoint2D point);

	/**
	 * Stops all background threads the controls may use.
	 */
	void stop();
}
