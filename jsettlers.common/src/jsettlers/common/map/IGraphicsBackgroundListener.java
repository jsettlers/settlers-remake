package jsettlers.common.map;

/**
 * This interface can be used by the user of the IGraphicsGrid to get notified if the background (landscape type or height) has changed.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IGraphicsBackgroundListener {
	/**
	 * This method is called if the landscape type or height has changed at the given position.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	void backgroundChangedAt(int x, int y);
}
