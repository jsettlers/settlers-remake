package jsettlers.common.selectable;

/**
 * Types of selections. This defines groups that can be selected together. <br>
 * The order is given by the priority (high value = higher priority)
 * 
 * @author Andreas Eberle
 * 
 */
public enum ESelectionType {
	PEOPLE,
	BUILDING,
	SPECIALISTS,
	SOLDIERS;

	public final int priority;

	ESelectionType() {
		priority = super.ordinal();
	}
}
