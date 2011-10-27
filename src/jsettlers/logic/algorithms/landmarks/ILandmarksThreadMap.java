package jsettlers.logic.algorithms.landmarks;

import jsettlers.common.Color;

/**
 * This interface specifies the methods needed by a map that should be handle with the LandmarksCorrectingThread.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ILandmarksThreadMap {

	public boolean isBlocked(short x, short y);

	public byte getPlayerAt(short x, short y);

	public short getPartitionAt(short x, short y);

	public void setPlayerAt(short x, short y, byte newPlayer, short partition);

	public void setDebugColor(short x, short y, Color color);

	public boolean isInBounds(short x, short y);
}
