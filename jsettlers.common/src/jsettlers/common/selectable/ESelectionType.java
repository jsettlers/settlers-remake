package jsettlers.common.selectable;

/**
 * Types of selections. This defines groups that can be selected together. <br>
 * The order is given by the priority (high value = higher priority)
 * 
 * @author Andreas Eberle
 * 
 */
public enum ESelectionType {
	PEOPLE(Integer.MAX_VALUE),
	BUILDING(1),
	SPECIALISTS(Integer.MAX_VALUE),
	SOLDIERS(Integer.MAX_VALUE);

	public final int priority;
	public final int maxSelected;

	ESelectionType(int maxSelected) {
		this.maxSelected = maxSelected;
		this.priority = super.ordinal();
	}
}
