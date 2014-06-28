package jsettlers.logic.movable.interfaces;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;

/**
 * This interface defines the methods needed by soldiers to able to attack the implementor of this interface.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IAttackable extends ILocatable, IInformable {
	/**
	 * Hits this attackable with the given strength.
	 * 
	 * @param strength
	 *            The strength of the hit.
	 * @param attackerPos
	 *            The position of the attacker.
	 */
	void receiveHit(float strength, ShortPoint2D attackerPos, byte attackingPlayer);

	/**
	 * Get the health of this {@link IAttackable}.
	 * 
	 * @return Health of the {@link IAttackable} in the interval [0,1] where 1 is fully healthy and 0 is dead.
	 */
	float getHealth();

	/**
	 * Gets the Player of this {@link IAttackable}.
	 * 
	 * @return Player of this {@link IAttackable}.
	 */
	byte getPlayerId();

	/**
	 * 
	 * @return true if this movable is currently attackable.<br>
	 *         false otherwise.
	 */
	boolean isAttackable();

	/**
	 * 
	 * @return Gets the {@link EMovableType} of this {@link IAttackable}.
	 */
	EMovableType getMovableType();

}
