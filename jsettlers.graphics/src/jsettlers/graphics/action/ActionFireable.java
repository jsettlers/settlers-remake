package jsettlers.graphics.action;

/**
 * This interface provides a method that allows you to fire an action on the object that implements the listener.
 * 
 * @author michael
 */
public interface ActionFireable {

	/**
	 * Fires the given action.
	 * 
	 * @param action
	 *            The action to fire.
	 */
	void fireAction(Action action);
}
