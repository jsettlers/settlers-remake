package jsettlers.graphics.startscreen.interfaces;

/**
 * This is a game we are currently joining to.
 * 
 * @author michael
 */
public interface IJoiningGame {
	void setListener(IJoiningGameListener l);

	void abort();
}
