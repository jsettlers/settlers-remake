package jsettlers.logic.map.newGrid.newManager;

/**
 * This enum defines the priority of material requests.
 * 
 * @author Andreas Eberle
 * 
 */
public enum EMaterialPriority {
	// NOTE: THE STOPPED PRIORITY MUST HAVE priorityIndex == 0
	STOPPED(0),

	LOW(1),
	HIGH(2);

	public static final EMaterialPriority[] values = EMaterialPriority.values();
	public static final int NUMBER_OF_PRIORITIES = values.length;

	public final int ordinal;
	private final int priorityIndex;

	private EMaterialPriority(int priorityIndex) {
		this.ordinal = ordinal();
		this.priorityIndex = priorityIndex;
	}

	/**
	 * 
	 * @return Returns the index of the priority. 0 Means the request is stopped. Indexes >= 1 have increasing priority with increasing index.
	 */
	public int getPriorityIndex() {
		return priorityIndex;
	}
}
