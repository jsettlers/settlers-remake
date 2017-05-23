package jsettlers.common.movable;

/**
 * The classification of a soldier.
 * 
 * @author Andreas Eberle
 */
public enum ESoldierClass {
	INFANTRY,
	BOWMAN;

	public static final ESoldierClass[] VALUES = ESoldierClass.values();
	public static final int NUMBER_OF_VALUES = VALUES.length;

	public final int ordinal;

	ESoldierClass() {
		this.ordinal = ordinal();
	}
}