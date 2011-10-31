package jsettlers.logic.map.newGrid.partition;

public interface IPartitionableGrid {

	boolean isBlocked(short currX, short currY);

	void changedPartitionAt(short x, short y);

}
