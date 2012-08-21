package jsettlers.logic.objects.arrow;

/**
 * This interface is used by {@link ArrowObject}s to hit a position on the grid.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IArrowAttackableGrid {

	/**
	 * If a movable is currently standing on the given position, it will be hit with the given hitStrength.
	 * 
	 * @param x
	 *            X coordinate of the position.
	 * @param y
	 *            Y coordinate of the position.
	 * @param hitStrength
	 *            Strength of the hit.
	 */
	void hitWithArrowAt(short x, short y, float hitStrength);

}
