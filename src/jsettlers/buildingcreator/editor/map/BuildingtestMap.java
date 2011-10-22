package jsettlers.buildingcreator.editor.map;

import jsettlers.buildingcreator.editor.BuildingDefinition;
import jsettlers.common.Color;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;

public class BuildingtestMap implements IGraphicsGrid {

	private static final int TESTMAP_SIZE = 40;

	public static final int OFFSET = TESTMAP_SIZE / 2;

	private final PseudoTile[][] tiles;

	public BuildingtestMap(BuildingDefinition definition) {
		tiles = new PseudoTile[TESTMAP_SIZE][TESTMAP_SIZE];
		for (int x = 0; x < TESTMAP_SIZE; x++) {
			for (int y = 0; y < TESTMAP_SIZE; y++) {
				tiles[x][y] = new PseudoTile(x, y);
			}
		}
		PseudoTile middle = tiles[OFFSET][OFFSET];
		IBuilding building = new PseudoBuilding(definition.getType(), middle);
		middle.setBuilding(building);
	}

	@Override
	public short getHeight() {
		return TESTMAP_SIZE;
	}

	@Override
	public short getWidth() {
		return TESTMAP_SIZE;
	}

	@Override
	public IMovable getMovableAt(int x, int y) {
		return null;
	}

	@Override
	public IMapObject getMapObjectsAt(int x, int y) {
		if (tiles[x][y].getBuilding() != null) {
			return tiles[x][y].getBuilding();
		} else {
			return tiles[x][y].getHeadMapObject();
		}
	}

	@Override
	public byte getHeightAt(int x, int y) {
		return 0;
	}

	@Override
	public ELandscapeType getLandscapeTypeAt(int x, int y) {
		return ELandscapeType.GRASS;
	}

	@Override
	public Color getDebugColorAt(int x, int y) {
		return tiles[x][y].getDebugColor();
	}

	@Override
	public boolean isBorder(int x, int y) {
		return false;
	}

	@Override
	public byte getPlayerAt(int x, int y) {
		return 0;
	}

	public PseudoTile getTile(ISPosition2D pos) {
		return tiles[pos.getX()][pos.getY()];
	}

}
