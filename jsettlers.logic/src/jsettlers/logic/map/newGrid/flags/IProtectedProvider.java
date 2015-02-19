package jsettlers.logic.map.newGrid.flags;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface IProtectedProvider {
	boolean isProtected(int x, int y);

	void setProtectedChangedListener(IProtectedChangedListener listener);

	/**
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	public interface IProtectedChangedListener {
		void protectedChanged(int x, int y, boolean newProtectedState);
	}
}
