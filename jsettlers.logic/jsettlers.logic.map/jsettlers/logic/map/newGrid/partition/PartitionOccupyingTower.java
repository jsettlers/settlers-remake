package jsettlers.logic.map.newGrid.partition;

import java.io.Serializable;

import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.position.SRectangle;
import jsettlers.common.position.ShortPoint2D;

/**
 * This class holds the data of a tower occupying an area on the {@link PartitionsGrid}.
 * 
 * @author Andreas Eberle
 * 
 */
final class PartitionOccupyingTower implements Serializable {
	private static final long serialVersionUID = 8080783290542281254L;

	public final byte playerId;
	public final ShortPoint2D position;
	public final IMapArea area;
	public final SRectangle areaBorders;
	public final int radius;

	public PartitionOccupyingTower(byte playerId, ShortPoint2D position, IMapArea area, SRectangle areaBorders, int radius) {
		this.playerId = playerId;
		this.position = position;
		this.area = area;
		this.areaBorders = areaBorders;
		this.radius = radius;
	}

	/**
	 * Creates a new {@link PartitionOccupyingTower} object with the same data as the given tower but the newPlayerId as playerId.
	 * 
	 * @param newPlayerId
	 * @param tower
	 */
	public PartitionOccupyingTower(byte newPlayerId, PartitionOccupyingTower tower) {
		this(newPlayerId, tower.position, tower.area, tower.areaBorders, tower.radius);
	}

}
