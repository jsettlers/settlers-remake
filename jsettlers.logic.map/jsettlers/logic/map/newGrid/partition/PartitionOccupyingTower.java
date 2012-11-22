package jsettlers.logic.map.newGrid.partition;

import java.io.Serializable;

import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ShortPoint2D;

/**
 * This class holds the data of a tower occupying an area on the {@link PartitionsGrid}.
 * 
 * @author Andreas Eberle
 * 
 */
final class PartitionOccupyingTower implements Serializable {
	private static final long serialVersionUID = 1102791996181571612L;

	public final ShortPoint2D position;
	public final byte playerId;
	public final MapCircle area;

	public PartitionOccupyingTower(byte playerId, MapCircle area) {
		this.playerId = playerId;
		this.area = area;
		this.position = new ShortPoint2D(area.getCenterX(), area.getCenterY());
	}

}
