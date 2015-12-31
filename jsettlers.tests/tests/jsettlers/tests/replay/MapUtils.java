package jsettlers.tests.replay;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import jsettlers.common.map.MapLoadException;
import jsettlers.logic.constants.ExtendedRandom;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.MapLoader;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.loader.RemakeMapLoader;
import jsettlers.tests.utils.CountingInputStream;

public class MapUtils {

	static void compareMapFiles(RemakeMapLoader expectedSavegame, RemakeMapLoader actualSavegame) throws IOException, MapLoadException, ClassNotFoundException {
		System.out.println("Comparing expected '" + expectedSavegame + "' with actual '" + actualSavegame + "' (uncompressed!)");

		try (InputStream expectedStream = RemakeMapLoader.getMapInputStream(expectedSavegame.getListedMap());
			 CountingInputStream actualStream = new CountingInputStream(RemakeMapLoader.getMapInputStream(actualSavegame.getListedMap()))) {
			MapFileHeader expectedHeader = MapFileHeader.readFromStream(expectedStream);
			MatchConstants.deserialize(new ObjectInputStream(expectedStream));
			int expectedTime = MatchConstants.clock().getTime();
			ExtendedRandom expectedRandom = MatchConstants.random();

			MapFileHeader actualHeader = MapFileHeader.readFromStream(actualStream);
			MatchConstants.deserialize(new ObjectInputStream(actualStream));
			int actualTime = MatchConstants.clock().getTime();
			ExtendedRandom actualRandom = MatchConstants.random();

			assertEquals(expectedHeader.getBaseMapId(), actualHeader.getBaseMapId());
			assertEquals(expectedTime, actualTime);
			// Test the random behavior a bit to have a high probability of equality. An equals method does not exist for Random.
			assertEquals(expectedRandom.nextInt(), actualRandom.nextInt());
			assertEquals(expectedRandom.nextInt(), actualRandom.nextInt());
			assertEquals(expectedRandom.nextInt(), actualRandom.nextInt());

			int e, a;
			while (((e = expectedStream.read()) != -1) & ((a = actualStream.read()) != -1)) {
				assertEquals("difference at (uncompressed) byte " + actualStream.getByteCounter(), e, a);
			}
			assertEquals("files have different lengths (uncompressed)", e, a);
		}
	}

}