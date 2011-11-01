package jsettlers.logic.algorithms.landmarks;


/**
 * This interface specifies the methods needed by a map that should be handle with the LandmarksCorrectingThread.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ILandmarksThreadMap {

	public boolean isBlocked(short x, short y);

	public short getPartitionAt(short x, short y);

	public boolean isInBounds(short x, short y);

	public void setPartitionAndPlayerAt(short x, short y, short startPartition);
}
