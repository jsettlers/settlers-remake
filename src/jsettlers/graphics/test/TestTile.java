package jsettlers.graphics.test;

import java.awt.Color;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;

public class TestTile implements ISPosition2D {
	private static final long serialVersionUID = 1L;

	private final short y;
	private final short x;
	private final byte height;

	private boolean river = false;

	private IMovable movable = null;
	private TestBuilding building = null;
	private IMapObject stack = null;
	private IMapObject object = null;
	private byte player = -1;

	public TestTile(short x, short y, byte height) {
		this.x = x;
		this.y = y;
		this.height = height;
	}

	public IBuilding getBuilding() {
		return this.building;
	}

	public byte getConstructionMark() {
		return 0;
	}

	public byte getHeight() {
		return this.height;
	}

	public ELandscapeType getLandscapeType() {
		if (isRiver()) {
			return ELandscapeType.RIVER1;
		} else if (this.height == 0) {
			return ELandscapeType.WATER;
		} else if (this.height <= 3) {
			return ELandscapeType.SAND;
		} else if (this.height <= 10) {
			return ELandscapeType.GRASS;
		} else if (this.height <= 15) {
			return ELandscapeType.MOUNTAIN;
		} else {
			return ELandscapeType.SNOW;
		}
	}

	public IMapObject getHeadMapObject() {
		return this.object;
	}

	public IMovable getMovable() {
		return this.movable;
	}

	public IMapObject getStack() {
		return this.stack;
	}

	public byte getPlayer() {
		return this.player;
	}

	public void setMovable(IMovable moveavble) {
		this.movable = moveavble;
	}

	@Override
	public short getX() {
		return this.x;
	}

	@Override
	public short getY() {
		return this.y;
	}

	@Override
	public boolean equals(ISPosition2D other) {
		return other.getX() == this.x && other.getY() == this.y;
	}

	@Override
	public int hashCode() {
		return ShortPoint2D.hashCode(x, y);
	}

	public void setBuilding(TestBuilding building) {
		this.building = building;
	}

	public void setRiver(boolean river) {
		this.river = river;
	}

	public boolean isRiver() {
		return this.river;
	}

	public void setStack(IMapObject stack) {
		this.stack = stack;
	}

	public void setMapObject(IMapObject object) {
		this.object = object;
	}

	public void setPlayer(byte player) {
		this.player = player;
	}

	public Color getDebugColor() {
		return null;
	}

}
