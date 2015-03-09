package jsettlers.logic.map.newGrid.partition;

/**
 * An implementor of this interface may be called by the {@link PartitionsGrid} when a position changed it's player.
 * 
 * @author Andreas Eberle
 */
public interface IPlayerChangedListener {
	/**
	 * This is a default implementation doing nothing on calls to the methods of {@link IPlayerChangedListener}.
	 */
	public static final IPlayerChangedListener DEFAULT_IMPLEMENTATION = new IPlayerChangedListener() {
		@Override
		public void playerChangedAt(int x, int y, byte newPlayerId) {
		}
	};

	/**
	 * This method is called when the player of a position is changed.
	 * 
	 * @param x
	 *            x coordinate of the position.
	 * @param y
	 *            y coordinate of the position.
	 * @param newPlayerId
	 *            The id of the new player.
	 */
	void playerChangedAt(int x, int y, byte newPlayerId);
}
