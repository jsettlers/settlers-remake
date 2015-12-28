package jsettlers.tests.replay;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import jsettlers.common.map.MapLoadException;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.tests.utils.CountingInputStream;

public class MapUtils {

	static void compareMapFiles(MapLoader expectedSavegame, MapLoader actualSavegame) throws IOException, MapLoadException {
		System.out.println("Comparing expected '" + expectedSavegame + "' with actual '" + actualSavegame + "' (uncompressed!)");
	
		try (InputStream expectedStream = MapLoader.getMapInputStream(expectedSavegame.getFile());
				CountingInputStream actualStream = new CountingInputStream(MapLoader.getMapInputStream(actualSavegame.getFile()))) {
			MapFileHeader expectedHeader = MapFileHeader.readFromStream(expectedStream);
			MapFileHeader actualHeader = MapFileHeader.readFromStream(actualStream);
	
			assertEquals(expectedHeader.getBaseMapId(), actualHeader.getBaseMapId());
	
			int e, a;
			while (((e = expectedStream.read()) != -1) & ((a = actualStream.read()) != -1)) {
				assertEquals("difference at (uncompressed) byte " + actualStream.getByteCounter(), e, a);
			}
			assertEquals("files have different lengths (uncompressed)", e, a);
		}
	}

}
