package jsettlers.logic.map.save;

import java.io.IOException;
import java.io.InputStream;

public interface IListedMap {
	/**
	 * Returns the file name without the .map extension.
	 * @return
	 */
	String getFileName();
	
	/**
	 * Gets a stream for that map.
	 * @return
	 * @throws IOException 
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * Deletes the map from the disk storage.
	 * @throws UnsupportedOperationException if the file cannot be deleted.
	 */
	void delete();
}
