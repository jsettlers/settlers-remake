package jsettlers.common.movable;

/**
 * The classification of a soldier.
 * 
 * @author Andreas Eberle
 */
public enum ESoldierClass {
	INFANTRY,
	BOWMAN;

	public static final ESoldierClass[] values = ESoldierClass.values();
	public static final int NUMBER_OF_VALUES = values.length;

	public final int ordinal;

	private ESoldierClass() {
		this.ordinal = ordinal();
	}
}