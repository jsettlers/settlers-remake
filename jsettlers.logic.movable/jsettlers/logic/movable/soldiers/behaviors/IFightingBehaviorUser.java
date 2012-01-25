package jsettlers.logic.movable.soldiers.behaviors;

/**
 * This interface specifies methods for a user of the FightingBehavior class
 * 
 * @author Andreas Eberle
 * 
 */
public interface IFightingBehaviorUser {

	/**
	 * 
	 * @return soldier behavior to be used after the action had been finished.
	 */
	SoldierBehavior getFinishedBehavior();

	/**
	 * set the action of the movable when no enemy is found enemy more.
	 */
	void setFinishedMovableAction();

}
