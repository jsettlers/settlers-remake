package jsettlers.logic.newmovable.interfaces;

public interface IInformable {
	/**
	 * Informs this attackable of an attackable enemy.
	 * 
	 * @param attackable
	 *            The attackable enemy.
	 */
	void informAboutAttackable(IAttackable attackable);

}
