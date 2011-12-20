package jsettlers.logic.movable;

/**
 * these are the internal states of a Movable
 * 
 * @author Andreas Eberle
 * 
 */
enum EMovableState {
	NO_ACTION(true, true),
	EXECUTING_ACTION(false, false),
	FINISHED_ACTION(true, false),
	WAITING_FOR_FREE_TILE(false, false),
	PUSHED_AND_WAITING(false, false),

	/**
	 * The movable does not get events any more.<br>
	 * It can only be waked up by and outer event.
	 */
	SLEEPING(true, true),

	/**
	 * Used for example for the bowmans in the tower that need to get the noActionEvent() event but should not move.
	 */
	DONT_MOVE(true, true);

	/**
	 * if true this attribute shows that this state can be exchanged by others by setting a new action to the movable.<br>
	 * if false and any change to the action of the movable, will result in an exception.
	 */
	final boolean canSetAction;
	/**
	 * if true the movable switchs it's action to EAction.NO_ACTION when set to this state.
	 */
	final boolean isLazyState;

	private EMovableState(boolean canSetAction, boolean isLazyState) {
		this.canSetAction = canSetAction;
		this.isLazyState = isLazyState;
	}
}
