package jsettlers.logic.algorithms.landmarks;

import jsettlers.common.position.ISPosition2D;

/**
 * This interface specifies the methods needed by a map that should be handle with the LandmarksCorrectingThread.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ILandmarksThreadMap {

	public boolean isBlocked(short x, short y);

	public byte getPlayer(ISPosition2D position);

	public void setPlayerAt(short x, short y, byte newPlayer);
}
