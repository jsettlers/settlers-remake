package jsettlers;

import java.io.File;

import jsettlers.common.map.MapLoadException;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.save.MapLoader;
import random.RandomSingleton;

public final class TestUtils {
	private TestUtils() {
	}

	public static MainGrid getMap() throws MapLoadException {
		RandomSingleton.load(123456L);
		MapLoader loader = new MapLoader(new File("../jsettlers.common/resources/maps/bigmap-2012-04-17_09-26-33.map"));
		return loader.getMainGrid();
	}

}
