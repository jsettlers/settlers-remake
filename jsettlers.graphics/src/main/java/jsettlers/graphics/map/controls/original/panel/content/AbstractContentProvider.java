/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.graphics.map.controls.original.panel.content;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.action.EActionType;
import jsettlers.common.action.IAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.common.action.PointAction;
import jsettlers.graphics.map.controls.original.panel.MainPanel;
import jsettlers.graphics.ui.UIPanel;

/**
 * This is a content that can be displayed on the {@link MainPanel}.
 *
 * @author Michael Zangl
 */
public abstract class AbstractContentProvider {

	/**
	 * Gets the UI panel that should be displayed. It fills the whole main panel.
	 *
	 * @return The UI panel to display.
	 */
	public abstract UIPanel getPanel();

	/**
	 * Gets the type of the tabs.
	 *
	 * @return The type. Never <code>null</code>
	 */
	public ESecondaryTabType getTabs() {
		return ESecondaryTabType.NONE;
	}

	/**
	 * Check whether this panel is selection sensitive
	 *
	 * @return <code>true</code> if this panel is for the current selection.
	 */
	public boolean isForSelection() {
		return false;
	}

	/**
	 * Called whenever the map position changed.
	 *
	 * @param position
	 * 		The new map position-
	 * @param grid
	 * 		The map grid.
	 */
	public void showMapPosition(ShortPoint2D position, IGraphicsGrid grid) {
	}

	/**
	 * This allows the current panel to catch actions that are executed.
	 *
	 * @param action
	 * 		The action to catch.
	 * @return The action to override with. May be <code>null</code> to cancel.
	 */
	public IAction catchAction(IAction action) {
		PointAction overrideAction;
		if (action.getActionType() == EActionType.SELECT_POINT
				&& (overrideAction = getSelectAction(((PointAction) action).getPosition())) != null) {
			return overrideAction;
		} else {
			return action;
		}
	}

	/**
	 * Gets a action that should be executed when the user clicks on a point.
	 *
	 * @param position
	 * 		The point
	 * @return The new action, <code>null</code> to indicate no change.
	 */
	protected PointAction getSelectAction(ShortPoint2D position) {
		return null;
	}

	/**
	 * Called whenever the content is hiding.
	 *
	 * @param actionFireable
	 * 		An {@link ActionFireable} to send actions to.
	 * @param nextContent
	 * 		The content that will be displayed afterwards.
	 */
	public void contentHiding(ActionFireable actionFireable, AbstractContentProvider nextContent) {
	}

	/**
	 * Called whenever the content is showing.
	 *
	 * @param actionFireable
	 * 		An {@link ActionFireable} to send actions to.
	 */
	public void contentShowing(ActionFireable actionFireable) {
	}

}
