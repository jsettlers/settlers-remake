package jsettlers.graphics.action;

/**
 * This is a action the user has requested.
 * <p>
 * Each Action has an active status, that indicates that it is currently
 * executed. When the execution of the action is begun, the flag should be set
 * so that the user interface enters a blocking mode, and goes back to normal
 * mode when the action is finished. It is not guaranteed that there is no other
 * action being sent during that time, e.g. an cancel-action.
 * <p>
 * Actions may be reused and fired multiple times by the interface, but they are
 * always inactive when being fired.
 * 
 * @author michael
 */
public class Action {
	private final EActionType actionType;
	private boolean active = false;

	/**
	 * Creates a new generic action.
	 * 
	 * @param actionType
	 *            The type the action should have.
	 */
	public Action(EActionType actionType) {
		this.actionType = actionType;
	}

	/**
	 * Sets the active flag of this action.
	 * 
	 * @param active
	 *            The flag that indicates if this action is active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Gets the type of the action.
	 * 
	 * @return The type.
	 */
	public EActionType getActionType() {
		return this.actionType;
	}

	/**
	 * Returns whether this action is active.
	 * 
	 * @return true if and only if the active flag is set.
	 */
	public boolean isActive() {
		return this.active;
	}
}
