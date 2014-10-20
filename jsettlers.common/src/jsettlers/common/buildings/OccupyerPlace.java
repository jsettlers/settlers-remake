package jsettlers.common.buildings;

import java.io.Serializable;

import jsettlers.common.position.RelativePoint;

/**
 * A place an occuping person can be in a building.
 * 
 * @author michael
 */
public class OccupyerPlace implements Serializable {
	private static final long serialVersionUID = 1355922428788608890L;

	private final ESoldierType type;
	private final int offsetY;
	private final int offsetX;
	private final RelativePoint position;

	private final boolean looksRight;

	public OccupyerPlace(int offsetX, int offsetY, ESoldierType type, RelativePoint position, boolean looksRight) {
		if (position == null || type == null) {
			throw new NullPointerException();
		}
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.type = type;
		this.position = position;
		this.looksRight = looksRight;
	}

	/**
	 * gets the type of the occupyer.
	 * 
	 * @return {@link ESoldierType#INFANTRY} if it is a person that is inside, {@link ESoldierType#BOWMAN} if it is a bowman on the roof.
	 */
	public final ESoldierType getType() {
		return type;
	}

	/**
	 * Gets the x coordinate (in pixels) of the settler.
	 * 
	 * @return
	 */
	public final int getOffsetX() {
		return offsetX;
	}

	/**
	 * Gets the y coordinate (in pixels) of the settler.
	 * 
	 * @return
	 */
	public final int getOffsetY() {
		return offsetY;
	}

	/**
	 * The type a soldier can have.
	 * 
	 * @author michael
	 */
	public enum ESoldierType {
		INFANTRY,
		BOWMAN
	}

	/**
	 * Whether the solier should look to the right.
	 * 
	 * @return A boolean value.
	 */
	public final boolean looksRight() {
		return looksRight;
	}

	/**
	 * Gets the point over which the soldier is standing.
	 * 
	 * @return The position relative to the building.
	 */
	public final RelativePoint getPosition() {
		return position;
	}
}
