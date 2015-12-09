package jsettlers.tests.autoreplay;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import jsettlers.logic.map.save.DirectoryMapLister;
import jsettlers.logic.map.save.MapFileHeader;

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
