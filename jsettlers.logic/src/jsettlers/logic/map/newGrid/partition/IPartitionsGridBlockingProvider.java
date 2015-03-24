package jsettlers.logic.map.newGrid.partition;

import jsettlers.algorithms.partitions.IBlockingProvider;
import jsettlers.logic.map.newGrid.flags.IBlockingChangedListener;

/**
 * This is an extended {@link IBlockingProvider}. Implementors of this interface also need to supply the possibility to register a listener for
 * changes of the blocking state.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IPartitionsGridBlockingProvider extends IBlockingProvider {
	/**
	 * This is a default implementation of the {@link IPartitionsGridBlockingProvider} interface. It's {@link #isBlocked(int, int)} method always
	 * returns false and the {@link #registerBlockingChangedListener(IBlockingChangedListener)} ignores every listener.
	 */
	public static final IPartitionsGridBlockingProvider DEFAULT_IMPLEMENTATION = new IPartitionsGridBlockingProvider() {
		@Override
		public boolean isBlocked(int x, int y) {
			return false;
		}

		@Override
		public void registerBlockingChangedListener(IBlockingChangedListener listener) {
		}
	};

	/**
	 * Registers the given listener. (Only one listener can be registered).
	 * 
	 * @param listener
	 */
	void registerBlockingChangedListener(IBlockingChangedListener listener);
}
