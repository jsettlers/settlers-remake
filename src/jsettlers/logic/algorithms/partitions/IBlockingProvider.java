package jsettlers.logic.algorithms.partitions;

/**
 * This interface defines a provider for blocking information. It can be used to find out if a position is blocking or not.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IBlockingProvider {
	/**
	 * This {@link IBlockingProvider} always returns false, so that no position will be seen as blocked.
	 */
	public static final IBlockingProvider DEFAULT_PROVIDER = new IBlockingProvider() {
		@Override
		public boolean isBlocked(int x, int y) {
			return false;
		}
	};

	/**
	 * True if the given position is blocked.
	 * 
	 * @param x
	 *            X coordinate of the position.
	 * @param y
	 *            Y coordinate of the position.
	 * @return true if the position is blocked<br>
	 *         false otherwise.
	 */
	boolean isBlocked(int x, int y);
}
