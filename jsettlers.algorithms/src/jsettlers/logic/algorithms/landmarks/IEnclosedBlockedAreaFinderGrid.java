package jsettlers.logic.algorithms.landmarks;

/**
 * This interface specifies the methods needed by a map that should be handle with the LandmarksCorrectingThread.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IEnclosedBlockedAreaFinderGrid {

	public boolean isBlocked(int x, int y);

	public short getPartitionAt(int x, int y);

	public boolean isInBounds(int x, int y);

	public void setPartitionAt(int x, int y, short newPartition);

	public short getHeight();

	public short getWidth();

}
