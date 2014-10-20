package jsettlers.logic.buildings.workers;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.map.newGrid.objects.MapObjectsManager;
import jsettlers.logic.player.Player;

/**
 * This is a mine building. It's only difference to a {@link WorkerBuilding} is that it's ground won't be flattened.
 * 
 * @author Andreas Eberle
 * 
 */
public final class MineBuilding extends WorkerBuilding {
	private static final long serialVersionUID = 9201058266194063092L;

	public MineBuilding(EBuildingType type, Player player) {
		super(type, player);
	}

	@Override
	protected boolean shouldBeFlatened() {
		return false;
	}

	@Override
	protected void placeAdditionalMapObjects(IBuildingsGrid grid, ShortPoint2D pos, boolean place) {
		if (place) {
			MapObjectsManager objectsManager = grid.getMapObjectsManager();
			for (ShortPoint2D currPos : new FreeMapArea(pos, super.getBuildingType().getProtectedTiles())) {
				objectsManager.removeMapObjectType(currPos.x, currPos.y, EMapObjectType.FOUND_COAL);
				objectsManager.removeMapObjectType(currPos.x, currPos.y, EMapObjectType.FOUND_GOLD);
				objectsManager.removeMapObjectType(currPos.x, currPos.y, EMapObjectType.FOUND_IRON);
				objectsManager.removeMapObjectType(currPos.x, currPos.y, EMapObjectType.FOUND_BRIMSTONE);
				objectsManager.removeMapObjectType(currPos.x, currPos.y, EMapObjectType.FOUND_GEMSTONE);
				objectsManager.removeMapObjectType(currPos.x, currPos.y, EMapObjectType.FOUND_NOTHING);
			}
		}
	}
}
