package jsettlers.testutils.map;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import jsettlers.logic.map.loading.list.DirectoryMapLister;
import jsettlers.logic.map.loading.newmap.MapFileHeader;

/**
 * 
 * @author Andreas Eberle
 *
 */
public class DebugMapLister extends DirectoryMapLister {

	public DebugMapLister(File directory, boolean createIfMissing) {
		super(directory, createIfMissing);
	}

	@Override
	public OutputStream getOutputStream(MapFileHeader header) throws IOException {
		return new DebugOutputStream(super.getOutputStream(header));
	}
}
