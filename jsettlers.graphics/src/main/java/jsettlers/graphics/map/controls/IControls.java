/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.graphics.map.controls;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.mouse.GODrawEvent;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.menu.IMapInterfaceListener;
import jsettlers.common.action.IAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.common.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.map.MapDrawContext;

/**
 * Classes that implement this are capable of displaying the full game controls (minimap, ...) on the screen.
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
	 * Gets the description for a given point, e.g. for tooltipps. May be called for any position.
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
	 * Gets the action for the given ui position, that should be executed if the user clicked it.
	 *
	 * @param position
	 *            The positon.
	 * @param selecting
	 *            If the event is a select event.
	 * @return The action for the position.
	 */
	Action getActionFor(UIPoint position, boolean selecting);

	/**
	 * Handles a draw event. The event may be fired even if it is outside the interface.
	 *
	 * @param event
	 *            The event to handle
	 * @return If the event was handled.
	 */
	boolean handleDrawEvent(GODrawEvent event);

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
	 *            An object we can fire actions to when we want to initiate an action ourselves.
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
	IAction replaceAction(IAction action);

	/**
	 * Gets a tooltip for the given map position.
	 * 
	 * @param point
	 *            The x/y coordinate, may be outside map.
	 * @return The tooltip text, or an empty String or <code>null</code> if nothing should be displayed.
	 */
	String getMapTooltip(ShortPoint2D point);

	/**
	 * Stops all background threads the controls may use.
	 */
	void stop();
}
