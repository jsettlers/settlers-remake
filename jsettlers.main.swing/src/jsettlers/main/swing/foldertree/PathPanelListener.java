package jsettlers.main.swing.foldertree;

/**
 * Listener for the Path panel
 * 
 * @author Andreas Butti
 *
 */
public interface PathPanelListener {

	/**
	 * Go to home
	 */
	void goToHome();

	/**
	 * Jump to a path
	 * 
	 * @param pathToJumpTo
	 *            The path
	 */
	void jumpTo(Object[] pathToJumpTo);

}
