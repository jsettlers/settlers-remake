package jsettlers.graphics.startscreen.interfaces;

import jsettlers.graphics.progress.EProgressState;

/**
 * This listener is notified about the progress of a join operation.
 * 
 * @author michael
 */
public interface IJoiningGameListener {
	public void joinProgressChanged(EProgressState state, float progress);

	void gameJoined(IJoinPhaseMultiplayerGameConnector connector);
}
