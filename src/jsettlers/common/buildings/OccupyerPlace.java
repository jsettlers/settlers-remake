package jsettlers.common.buildings;

import java.io.Serializable;

/**
 * A place an occuping person can be in a building.
 * 
 * @author michael
 */
public class OccupyerPlace implements Serializable {
	private static final long serialVersionUID = 1355922428788608890L;

	private final ESoldierType type;
	private final int y;
	private final int x;

	public OccupyerPlace(int x, int y, ESoldierType type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}

	/**
	 * gets the type of the occupyer.
	 * 
	 * @return {@link ESoldierType#INFANTARY} if it is a person that is inside, {@link ESoldierType#BOWMAN} if it is a bowman on the roof.
	 */
	public ESoldierType getType() {
		return type;
	}

	/**
	 * Gets the x coordinate (in pixels) of the settler.
	 * 
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the y coordinate (in pixels) of the settler.
	 * 
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * The type a soldier can have.
	 * 
	 * @author michael
	 */
	public enum ESoldierType {
		INFANTARY,
		BOWMAN
	}

	public boolean looksRight() {
		return false; // TODO
	}
}
