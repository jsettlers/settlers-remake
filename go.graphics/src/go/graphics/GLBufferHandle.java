package go.graphics;

/**
 * This class represents an abstract buffer handle.
 * 
 * @author Michael Zangl
 */
public interface GLBufferHandle {
	/**
	 * Checks if this buffer is valid.
	 * 
	 * @return <code>true</code> if the buffer is valid and can be used.
	 */
	boolean isValid();

	/**
	 * Deletes the buffer represented by this handle. If the buffer is already deleted, this call is ignored.
	 */
	void delete();

	/**
	 * Gets the index by which this buffer is referenced internally. You should not need this.
	 * 
	 * @return Thge buffer id.
	 */
	int getInternalId();
}
