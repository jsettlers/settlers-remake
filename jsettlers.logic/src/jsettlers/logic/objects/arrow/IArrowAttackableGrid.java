package jsettlers.logic.objects.arrow;

/**
 * This interface is used by {@link ArrowObject}s to hit a position on the grid.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IArrowAttackableGrid {

	/**
	 * If a movable is currently standing on the given position, it will be hit with the given arrow.
	 * 
	 * @param the
	 *            arrow hitting the position.
	 */
	void hitWithArrowAt(ArrowObject arrowObject);

}
