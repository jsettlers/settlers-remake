package jsettlers.logic.map.newGrid.newManager;

/**
 * This enum defines priority of material requests.
 * 
 * @author Andreas Eberle
 * 
 */
public enum EPriority {
	// NOTE: THE STOPPED PRIORITY MUST HAVE priorityIndex == 0
	STOPPED(0),

	LOW(1),
	HIGH(2);

	public static final EPriority[] values = EPriority.values();
	public static final int NUMBER_OF_PRIORITIES = values.length;

	public final int ordinal;
	private final int priorityIndex;

	private EPriority(int priorityIndex) {
		this.ordinal = ordinal();
		this.priorityIndex = priorityIndex;
	}

	public int getPriorityIndex() {
		return priorityIndex;
	}
}
