package jsettlers.logic.algorithms.landmarks;

/**
 * This interface specifies the methods needed by a map that should be handle with the LandmarksCorrectingThread.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ILandmarksThreadGrid {

	public boolean isBlocked(short x, short y);

	public short getPartitionAt(short x, short y);

	public boolean isInBounds(short x, short y);

	public void setPartitionAndPlayerAt(short x, short y, short newPartition);

	public short getHeight();

	public short getWidth();

	/**
	 * Gives the id of the blocked partition of the given position.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public short getBlockedPartition(short x, short y);

}
