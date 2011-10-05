package jsettlers.logic.map.newGrid;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.IHexMap;
import jsettlers.common.map.IHexTile;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.newGrid.blocked.BlockedGrid;
import jsettlers.logic.map.newGrid.movable.MovableGrid;
import jsettlers.logic.map.newGrid.objects.ObjectsGrid;
import jsettlers.logic.map.newGrid.partition.PartitionsGrid;

/**
 * This is the main grid offering an interface for interacting with it.
 * 
 * @author Andreas Eberle
 * 
 */
public class MainGrid implements IHexMap {
	private final ObjectsGrid objectsGrid;
	private final PartitionsGrid partitionsMap;
	private final MovableGrid movableGrid;
	private final BlockedGrid blockedGrid;

	private final short width;
	private final short height;

	public MainGrid(short width, short height) {
		this.width = width;
		this.height = height;
		this.objectsGrid = new ObjectsGrid(width, height);
		this.movableGrid = new MovableGrid(width, height);
		this.blockedGrid = new BlockedGrid(width, height);
		this.partitionsMap = null;
	}

	@Override
	public EBuildingType getConstructionPreviewBuilding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getWidth() {
		return width;
	}

	@Override
	public short getHeight() {
		return height;
	}

	@Override
	public IHexTile getTile(ISPosition2D pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IHexTile getTile(short x, short y) {
		// TODO Auto-generated method stub
		return null;
	}

}
