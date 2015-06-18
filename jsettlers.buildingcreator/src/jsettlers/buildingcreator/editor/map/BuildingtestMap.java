/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.buildingcreator.editor.map;

import jsettlers.buildingcreator.editor.BuildingDefinition;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.EDebugColorModes;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.partition.IPartitionSettings;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;

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
		IBuilding building = new PseudoBuilding(definition.getType(), middle.getPos());
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
		}

		return tiles[x][y].getHeadMapObject();
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
	public int getDebugColorAt(int x, int y, EDebugColorModes debugColorMode) {
		return tiles[x][y].getDebugColor();
	}

	@Override
	public boolean isBorder(int x, int y) {
		return false;
	}

	@Override
	public byte getPlayerIdAt(int x, int y) {
		return 0;
	}

	public PseudoTile getTile(ShortPoint2D pos) {
		return tiles[pos.x][pos.y];
	}

	@Override
	public byte getVisibleStatus(int x, int y) {
		return CommonConstants.FOG_OF_WAR_VISIBLE;
	}

	@Override
	public void setBackgroundListener(IGraphicsBackgroundListener backgroundListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public int nextDrawableX(int x, int y, int maxX) {
		return x + 1;
	}

	@Override
	public IPartitionSettings getPartitionSettings(int x, int y) {
		return null;
	}

}
