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
package jsettlers.graphics.map;

import java.util.LinkedList;

import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IMapInterfaceListener;
import jsettlers.common.menu.UIState;
import jsettlers.common.action.IAction;
import jsettlers.common.menu.messages.IMessage;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.ActionFirerer;

/**
 * This is the main interface connector.
 * <p>
 * It holds the current selection displayed in the interface (not on the map). See {@link #setSelection(ISelectionSet)}.
 * <p>
 * It also propagates interface events, to get them you can add a interface listener. See {@link IMapInterfaceListener}.
 * 
 * @author michael
 */
public class MapInterfaceConnector implements ActionFireable, IMapInterfaceConnector {

	private final LinkedList<IMapInterfaceListener> listeners = new LinkedList<>();

	private final ActionFirerer actionFirerer = new ActionFirerer(
			action -> {
				synchronized (listeners) {
					for (IMapInterfaceListener listener : listeners) {
						listener.action(action);
					}
				}
			});

	private final MapContent content;

	/**
	 * Creates a new connector for the given interface.
	 * 
	 * @param content
	 *            The map.
	 */
	public MapInterfaceConnector(MapContent content) {
		this.content = content;
		actionFirerer.setBlockingListener(content);
	}

	/**
	 * Sets the current selection that should be displayed in the side panel.
	 * 
	 * @param selection
	 *            The selection.
	 */
	@Override
	public void setSelection(ISelectionSet selection) {
		this.content.setSelection(selection);
	}

	/**
	 * Scrolls a given point to the center of the view. It needn't be on the map.
	 * 
	 * @param point
	 *            The point to show.
	 * @param mark
	 *            If there should be a mark displayed at the point.
	 */
	@Override
	public void scrollTo(ShortPoint2D point, boolean mark) {
		this.content.scrollTo(point, mark);
	}

	/**
	 * Adds a listener that listens to interface commands.
	 * 
	 * @see IMapInterfaceListener
	 * @param listener
	 *            The listener.
	 * @see #removeListener(IMapInterfaceListener)
	 */
	@Override
	public void addListener(IMapInterfaceListener listener) {
		synchronized (this.listeners) {
			if (listener != null && !this.listeners.contains(listener)) {
				this.listeners.add(listener);
			}
		}
	}

	/**
	 * Removes a given Listener, if it is regisered.
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	@Override
	public void removeListener(IMapInterfaceListener listener) {
		synchronized (this.listeners) {
			this.listeners.remove(listener);
		}
	}

	@Override
	public void fireAction(IAction action) {
		this.actionFirerer.fireAction(action);
	}

	@Override
	public void playSound(int soundId, float volume) {
		content.playSound(soundId, volume);
	}

	@Override
	public void showMessage(IMessage message) {
		content.addMessage(message);
	}

	/**
	 * Stops all threads related to the graphics display. You may experience crazy results when trying to use the map view afterwards.
	 */
	@Override
	public void shutdown() {
		actionFirerer.stop();
		content.stop();
	}

	/**
	 * Gets the state of the content.
	 * 
	 * @return The state of the UI so that it can be restored by setting it on {@link MapContent} creation.
	 * @see #loadUIState(UIState)
	 */
	@Override
	public UIState getUIState() {
		return content.getUIState();
	}

	@Override
	public void loadUIState(UIState state) {
		content.loadUIState(state);
	}
}
