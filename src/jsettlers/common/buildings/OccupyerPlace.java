package jsettlers.common.buildings;

/**
 * A place an occuping person can be in a building.
 * 
 * @author michael
 */
public class OccupyerPlace {

	private final EType type;
	private final int y;
	private final int x;

	public OccupyerPlace(int x, int y, EType type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}

	/**
	 * gets the type of the occupyer.
	 * 
	 * @return {@link EType#INSIDE} if it is a person that is inside,
	 *         {@link EType#BOWMAN} if it is a bowman on the roof.
	 */
	public EType getType() {
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
	 * The type a place can have.
	 * 
	 * @author michael
	 */
	public enum EType {
		INSIDE, BOWMAN
	}
}
