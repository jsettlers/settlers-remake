package jsettlers.buildingcreator.editor.map;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;

public class PseudoTile {

	private final int x;
	private final int y;

	private IBuilding building;
	private int debugColor;
	private IMapObject stack;

	public PseudoTile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public IBuilding getBuilding() {
		return building;
	}

	public int getDebugColor() {
		return debugColor;
	}

	public byte getHeight() {
		return 0;
	}

	public ELandscapeType getLandscapeType() {
		return ELandscapeType.DRY_GRASS;
	}

	public IMapObject getHeadMapObject() {
		return null;
	}

	public IMovable getMovable() {
		return null;
	}

	public IMapObject getStack() {
		return stack;
	}

	public byte getPlayer() {
		return 0;
	}

	public boolean equals(ShortPoint2D other) {
		return other.getX() == x && other.getY() == y;
	}

	@Override
	public int hashCode() {
		return ShortPoint2D.hashCode(x, y);
	};

	public void setBuilding(IBuilding building) {
		this.building = building;
	}

	public void setDebugColor(int debugColor) {
		this.debugColor = debugColor;
	}

	public void setStack(IMapObject stack) {
		this.stack = stack;
	}

	public ShortPoint2D getPos() {
		return new ShortPoint2D(x, y);
	}
}
