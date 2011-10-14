package jsettlers.logic.map.hex;

import java.awt.Color;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IHexTile;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.hex.interfaces.IHexStack;
import jsettlers.logic.map.newGrid.interfaces.AbstractHexMapObject;
import jsettlers.logic.map.newGrid.interfaces.IHexMovable;
import jsettlers.logic.materials.stack.single.EStackType;
import jsettlers.logic.materials.stack.single.SingleMaterialStack;
import jsettlers.logic.objects.IMapObjectsManagerTile;

/**
 * This is a tile of the map grid.
 * <p>
 * It holds two flags that state if it is blocked:
 * <p>
 * The protected flag states that walking and placing materials is allowed, but it is forbidden to build something here or add protecting objects.
 * <p>
 * The blocked flag means that no one may enter this place. If it is set, the protected flag is set automatically. To set/unset you should use
 * {@link #setBlockedAndProtected(boolean)}
 * 
 * @author Andreas
 */
public class HexTile implements IHexTile, ILocatable, IMapObjectsManagerTile {

	public static final float TILE_PATHFINDER_COST = 1.0f;
	public static final float TILE_HEURISTIC_DIST = 1f;

	private static final Color CLOSED_COLOR = new Color(255, 0, 0, 210);
	private static final Color OPEN_COLOR = new Color(0, 0, 255, 210);
	private static final Color BLOCKED_COLOR = new Color(0, 0, 0, 200);

	private final short hexX, hexY;
	private byte height = 0;
	private ELandscapeType landscapeType = ELandscapeType.GRASS;

	private boolean blocked = false;
	private boolean isProtected = false;
	private Color debugColor;

	private byte constructionMarkValue = -1;
	private byte player = -1;

	private IBuilding building;

	private IHexStack stack;
	private AbstractHexMapObject mapObjectHead = null;
	private IHexMovable movable = null;

	private boolean marked = false;

	public HexTile(short x, short y) {
		this.hexX = x;
		this.hexY = y;
	}

	@Override
	public short getX() {
		return hexX;
	}

	@Override
	public short getY() {
		return hexY;
	}

	@Override
	public ISPosition2D getPos() {
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof ISPosition2D)) {
			return false;
		}

		return equals((ISPosition2D) o);
	}

	@Override
	public boolean equals(ISPosition2D o) {
		return o.getX() == getX() && o.getY() == getY();
	}

	public boolean equals(HexTile tile) {
		return tile != null && hexX == tile.hexX && hexY == tile.hexY;
	}

	@Override
	public int hashCode() {
		return ShortPoint2D.hashCode(hexX, hexY);
	}

	@Override
	public String toString() {
		return "   (" + hexX + "|" + hexY + ")";
	}

	public boolean isMarked() {
		return this.marked;
	}

	void setMarked(boolean setMarker) {
		assert !this.marked || !setMarker : "can not mark a marked tile";
		this.marked = setMarker;
	}

	@Override
	public boolean isBlocked() {
		return blocked;
	}

	public void markAsOpen() {
		debugColor = OPEN_COLOR;
	}

	public void markAsClosed() {
		debugColor = CLOSED_COLOR;
	}

	// Everything that belongs to
	// buildings------------------------------------------------------------------------------------------
	public void setBuilding(IBuilding building) {
		this.building = building;
	}

	public void setBlockedByBuilding(IBuilding building) {
		setBlocked(true);
		this.building = building;
	}

	public void removeBuilding() {
		this.blocked = false;
		this.building = null;
	}

	// everything that belongs to the stack
	// ----------------------------------------------------------------------------------------------

	/**
	 * can be used to set the given stack to this tile.
	 * 
	 * @param stack
	 *            stack to be set or null if the stack should be removed.
	 */
	void setStack(IHexStack stack) {
		if (this.stack != null && this.stack != stack) {
			this.stack.destroy();
		}

		this.stack = stack;
	}

	void removeStack() {
		this.stack.destroy();
		this.stack = null;
	}

	// everything to work with the Moveables
	// -----------------------------------------------------------------

	void setConstructMarking(byte constructionMarkValue) {
		this.constructionMarkValue = constructionMarkValue;
		if (constructionMarkValue >= 0) {
			setDebugColor(Color.GREEN);
		}
	}

	public byte getConstructMarking() {
		return this.constructionMarkValue;
	}

	@Override
	public byte getPlayer() {
		return player;
	}

	@Override
	public IBuilding getBuilding() {
		return building;
	}

	@Override
	public byte getConstructionMark() {
		return this.constructionMarkValue;
	}

	@Override
	public byte getHeight() {
		return height;
	}

	@Override
	public ELandscapeType getLandscapeType() {
		return landscapeType;
	}

	@Override
	public IHexStack getStack() {
		return stack;
	}

	/**
	 * Sets the blocked status of the tile.
	 * <p>
	 * If the new status is blocked, isProtected is automatically set.
	 * 
	 * @param blocked
	 */
	@Override
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
		if (blocked) {
			setProtected(true);
		}
		if (blocked) {
			this.debugColor = BLOCKED_COLOR;
		} else {
			this.debugColor = null;
		}
	}

	public void setProtected(boolean isProtected) {
		this.isProtected = isProtected;
		if (!isProtected) {
			setBlocked(false);
		}
	}

	public boolean isProtected() {
		return isProtected;
	}

	@Override
	public Color getDebugColor() {
		return debugColor;
	}

	public void moveableLeft(IHexMovable movable) {
		if (this.movable == movable) {
			this.movable = null;
		}
	}

	/**
	 * This method overrides the movable that's currently located at this position.
	 * 
	 * @param movable
	 */
	public void moveableEntered(IHexMovable movable) {
		this.movable = movable;
	}

	@Override
	public IHexMovable getMovable() {
		return movable;
	}

	void pushMovable(IHexMovable from) {
		if (movable != null) {
			movable.push(from);
		}
	}

	public void setDebugColor(Color color) {
		this.debugColor = color;
	}

	@Override
	public void setLandscape(ELandscapeType landscapeType) {
		this.landscapeType = landscapeType;

		switch (landscapeType) {
		case WATER:
		case SNOW:
			setBlocked(true);
			break;
		default:
			setBlocked(false);
			break;
		}
	}

	/**
	 * checks if this position has any map object on it
	 * 
	 * @return true if there is any map object<br>
	 *         false otherwise
	 */
	boolean hasMapObject() {
		return mapObjectHead != null;
	}

	/**
	 * Use {@link HexGrid#addMapObject(ISPosition2D, AbstractHexMapObject)}
	 * 
	 * @param mapObject
	 */
	@Override
	public void addMapObject(AbstractHexMapObject mapObject) {
		if (this.mapObjectHead == null) {
			this.mapObjectHead = mapObject;
		} else {
			this.mapObjectHead.addMapObject(mapObject);
		}
	}

	/**
	 * <<<<<<< .mine Removes a map object, but does not update blocking, so you should use
	 * {@link HexGrid#removeMapObject(ISPosition2D, AbstractHexMapObject)} instead ======= Removes a map object, but does not update blocking, so you
	 * should use {@link HexGrid#removeMapObject(ISPosition2D, AbstractHexMapObject)} instead >>>>>>> .r512
	 * 
	 * @param mapObject
	 */
	@Override
	public boolean removeMapObject(AbstractHexMapObject mapObject) {
		if (this.mapObjectHead != null) {
			boolean removed;
			if (this.mapObjectHead == mapObject) {
				this.mapObjectHead = this.mapObjectHead.getNextObject();
				removed = true;
			} else {
				removed = this.mapObjectHead.removeMapObject(mapObject);
			}

			return removed;
		}
		return false;
	}

	/**
	 * Use {@link HexGrid#removeMapObjectType(ISPosition2D, EMapObjectType)}
	 * 
	 * @param mapObjectType
	 * @return
	 */
	@Override
	public AbstractHexMapObject removeMapObjectType(EMapObjectType mapObjectType) {
		AbstractHexMapObject removed = null;
		if (this.mapObjectHead != null) {
			if (this.mapObjectHead.getObjectType() == mapObjectType) {
				removed = this.mapObjectHead;
				this.mapObjectHead = this.mapObjectHead.getNextObject();
			} else {
				removed = this.mapObjectHead.removeMapObjectType(mapObjectType);
			}
		}
		return removed;
	}

	void setPlayer(byte newPlayer) {
		if (this.player != newPlayer) {
			this.player = newPlayer;
			if (stack != null) {
				IHexStack oldStack = stack;
				// TODO: multi-material stacks
				EMaterialType materialType = oldStack.getMaterial();
				stack = new SingleMaterialStack(materialType, this, EStackType.OFFER, newPlayer);
				for (int i = 0; i < oldStack.getNumberOfElements(); i++) {
					stack.push(materialType);
				}
				oldStack.destroy();

			}
		}
	}

	boolean hasCuttableObject(EMapObjectType mapObjectType) {
		return mapObjectHead != null && mapObjectHead.hasCuttableObject(mapObjectType);
	}

	/**
	 * @param mapObjectType
	 *            type to be looked for
	 * @return true if at least one of the map objects fits the given EMapObjectType
	 */
	boolean hasMapObjectType(EMapObjectType mapObjectType) {
		return mapObjectHead != null && mapObjectHead.hasMapObjectType(mapObjectType);
	}

	/**
	 * @param type
	 *            type to be looked for
	 * @return the first map object with the given type or null if none has been found.
	 */
	@Override
	public AbstractHexMapObject getMapObject(EMapObjectType type) {
		return mapObjectHead != null ? mapObjectHead.getMapObject(type) : null;
	}

	@Override
	public IMapObject getHeadMapObject() {
		return mapObjectHead;
	}

	void setHeight(byte height) {
		this.height = height;
	}

	public void setBlockedAndProtected(boolean blocked2) {
		this.blocked = blocked2;
		this.isProtected = blocked2;
	}
}
