package jsettlers.logic.movable.interfaces;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface IInformable {
	/**
	 * Informs this attackable of an attackable enemy.
	 * 
	 * @param attackable
	 *            The attackable enemy.
	 */
	void informAboutAttackable(IAttackable attackable);

}
