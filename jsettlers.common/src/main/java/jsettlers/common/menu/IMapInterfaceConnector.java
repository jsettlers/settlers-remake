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
package jsettlers.common.menu;

import jsettlers.common.action.IAction;
import jsettlers.common.menu.messages.IMessenger;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;

/**
 * This is the interface that the map logic uses to access the UI.
 * 
 * @author Andreas Eberle
 */
public interface IMapInterfaceConnector extends IMessenger {

	/**
	 * Gets the current state of the UI.
	 * 
	 * @return A state that can later be passed to {@link #loadUIState(UIState)}
	 */
	UIState getUIState();

	/**
	 * Restores a UI state.
	 * 
	 * @param uiStateData
	 *            The state to restore.
	 * @see #getUIState()
	 */
	void loadUIState(UIState uiStateData);

	/**
	 * Adds a new {@link IMapInterfaceListener} to listen to UI actions.
	 * 
	 * @param listener
	 *            The listener
	 */
	void addListener(IMapInterfaceListener listener);

	/**
	 * Removes a listener added by {@link #addListener(IMapInterfaceListener)}.
	 * 
	 * @param guiInterface
	 *            The listener
	 */
	void removeListener(IMapInterfaceListener guiInterface);

	/**
	 * Moves the center of the view to a given point.
	 * 
	 * @param point
	 *            The position to scroll to.
	 * @param mark
	 *            <code>true</code> if we should highlight the exact point to the user.
	 */
	void scrollTo(ShortPoint2D point, boolean mark);

	/**
	 * Updates the selection the UI should display.
	 * 
	 * @param selection
	 *            The selection.
	 */
	void setSelection(ISelectionSet selection);

	/**
	 * Kills the UI and releases all resources. Should only be called once.
	 */
	void shutdown();

	void fireAction(IAction action);

	void playSound(int soundId, float volume);
}
