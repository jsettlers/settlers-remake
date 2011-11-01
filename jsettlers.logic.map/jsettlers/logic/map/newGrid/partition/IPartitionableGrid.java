package jsettlers.logic.map.newGrid.partition;

import jsettlers.common.Color;

public interface IPartitionableGrid {

	boolean isBlocked(short currX, short currY);

	void changedPartitionAt(short x, short y);

	void setDebugColor(final short x, final short y, Color color);

}
