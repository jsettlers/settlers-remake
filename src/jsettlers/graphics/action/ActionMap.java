package jsettlers.graphics.action;

import go.graphics.UIPoint;

import java.util.ArrayList;

import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.utils.Button;

/**
 * This is a map of actions.
 * <p>
 * It assigns actions to rectangulr areas of the screen.
 * 
 * @author michael
 */
public class ActionMap {
	ArrayList<PositionedAction> actions = new ArrayList<PositionedAction>();

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
	 * @return The action, or<code>null</code> if there is no registered action
	 *         for that point.
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
	 * This is a combination of an action and its position.
	 * 
	 * @author michael
	 */
	private class PositionedAction {
		private final Action action;
		private final FloatRectangle position;
		private final Button button;

		public PositionedAction(Action action, FloatRectangle position) {
			this.action = action;
			this.position = position;
			this.button = null;
		}

		public PositionedAction(Action action, Button button) {
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

	public void removeAll() {
	    this.actions.clear();
    }

}
