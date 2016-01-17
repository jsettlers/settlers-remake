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
package jsettlers.graphics.action;

import go.graphics.UIPoint;

import java.util.ArrayList;

import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.ui.Button;

/**
 * This is a map of actions.
 * <p>
 * It assigns actions to rectangular areas of the screen.
 * 
 * @author Michael Zangl
 */
public class ActionMap {
	private final ArrayList<PositionedAction> actions = new ArrayList<PositionedAction>();

	/**
	 * Creates an empty map.
	 */
	public ActionMap() {
	}

	/**
	 * Adds an action to the map for the given Button.
	 * 
	 * @param action
	 *            The action.
	 * @param position
	 *            The position this action is used for.
	 */
	public void addAction(Action action, Button position) {
		this.actions.add(new PositionedAction(action, position));
	}

	/**
	 * Adds an action to the map.
	 * <p>
	 * An action may be added multiple times for different points.
	 * 
	 * @param action
	 *            The action.
	 * @param position
	 *            The position this action is used for.
	 */
	public void addAction(Action action, FloatRectangle position) {
		this.actions.add(new PositionedAction(action, position));
	}

	/**
	 * Gets the first found action in the map for the given position.
	 * 
	 * @param pos
	 *            The position.
	 * @return The action, or<code>null</code> if there is no registered action for that point.
	 */
	public Action getAction(UIPoint pos) {
		for (PositionedAction action : this.actions) {
			if (!action.getAction().isActive()
					&& action.getPosition().contains((int) pos.getX(),
							(int) pos.getY())) {
				return action.getAction();
			}
		}
		return null;
	}

	/**
	 * This is a combination of an action and its position on screen.
	 * 
	 * @author Michael Zangl
	 */
	private class PositionedAction {
		private final Action action;
		private final FloatRectangle position;
		private final Button button;

		/**
		 * Creates a new positioned action.
		 * 
		 * @param action
		 *            The action
		 * @param position
		 *            The position the action is at.
		 */
		PositionedAction(Action action, FloatRectangle position) {
			this.action = action;
			this.position = position;
			this.button = null;
		}

		/**
		 * Creates a new positioned action.
		 * 
		 * @param action
		 *            The action
		 * @param position
		 *            The button the action is for.
		 */
		PositionedAction(Action action, Button button) {
			this.action = action;
			this.button = button;
			this.position = null;
		}

		public Action getAction() {
			return this.action;
		}

		public FloatRectangle getPosition() {
			if (this.button == null) {
				return this.position;
			} else {
				return this.button.getPosition();
			}
		}
	}

	/**
	 * Clears this action map.
	 */
	public void removeAll() {
		this.actions.clear();
	}

}
