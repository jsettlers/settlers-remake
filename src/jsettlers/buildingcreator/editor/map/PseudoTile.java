package jsettlers.buildingcreator.editor.map;

import java.awt.Color;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IHexTile;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.IStack;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;

public class PseudoTile implements IHexTile {
	private final int x;
	private final int y;

	private IBuilding building;
	private Color debugColor;
	private IStack stack;

	public PseudoTile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public IBuilding getBuilding() {
		return building;
	}

	@Override
	public byte getConstructionMark() {
		return 0;
	}

	@Override
	public Color getDebugColor() {
		return debugColor;
	}

	@Override
	public byte getHeight() {
		return 0;
	}

	@Override
	public ELandscapeType getLandscapeType() {
		return ELandscapeType.DRY_GRASS;
	}

	@Override
	public IMapObject getHeadMapObject() {
		return null;
	}

	@Override
	public IMovable getMovable() {
		return null;
	}

	@Override
	public IStack getStack() {
		return stack;
	}

	@Override
	public byte getPlayer() {
		return 0;
	}

	@Override
	public boolean equals(ISPosition2D other) {
		return other.getX() == x && other.getY() == y;
	}
	
	public int hashCode() {
		return ShortPoint2D.hashCode(x, y);
	};

	@Override
	public short getX() {
		return (short) x;
	}

	@Override
	public short getY() {
		return (short) y;
	}

	public void setBuilding(IBuilding building) {
		this.building = building;
	}

	public void setDebugColor(Color debugColor) {
		this.debugColor = debugColor;
	}

	public void setStack(IStack stack) {
		this.stack = stack;
	}
}
