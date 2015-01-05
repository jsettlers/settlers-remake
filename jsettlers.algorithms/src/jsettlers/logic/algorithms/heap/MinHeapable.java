package jsettlers.logic.algorithms.heap;

/**
 * This interface defines what an element, that may be stored in a heap, must provide for the heap to work.
 * 
 * @author andreas
 */
public interface MinHeapable {
	/**
	 * Gets the rank of the element.
	 * <p>
	 * The rank may not change without noticing the heap of the change.
	 * 
	 * @return The rank. Any float value.
	 */
	float getRank();

	/**
	 * Gets the last value given to {@link #setHeapIdx(int)}. If the index was not set, it must return -1.
	 * 
	 * @return The heap index.
	 */
	int getHeapIdx();

	/**
	 * Sets the heap index.
	 * 
	 * @param idx
	 *            The heap index.
	 */
	void setHeapIdx(int idx);
}
