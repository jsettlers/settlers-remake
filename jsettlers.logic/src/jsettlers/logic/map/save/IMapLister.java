package jsettlers.logic.map.save;

import java.io.IOException;
import java.io.OutputStream;

public interface IMapLister {
	public interface IMapListerCallable {
		void foundMap(IListedMap map);
	}

	void getMaps(IMapListerCallable callable);

	/**
	 * Gets an output stream that can be used to store the map. The stream is to a file with a nice name and does not override any other file.
	 * 
	 * @param header
	 *            The header to create the file name from. It is not written to the stream.
	 * @return A output stream to a fresh generated file.
	 * @throws IOException
	 */
	OutputStream getOutputStream(MapFileHeader header)
			throws IOException;
}
