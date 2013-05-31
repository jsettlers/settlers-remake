package jsettlers.common.material;

/**
 * This enum defines the priority of material requests.
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

	public final byte ordinal;
	private final int priorityIndex;

	private EPriority(int priorityIndex) {
		this.ordinal = (byte) ordinal();
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
