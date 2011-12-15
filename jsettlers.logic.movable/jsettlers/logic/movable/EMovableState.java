package jsettlers.logic.movable;

/**
 * these are the internal states of a Movable
 * 
 * @author Andreas Eberle
 * 
 */
enum EMovableState {
	NO_ACTION,
	EXECUTING_ACTION,
	FINISHED_ACTION,
	WAITING_FOR_FREE_TILE,
	PUSHED_AND_WAITING,
	SLEEPING
}
