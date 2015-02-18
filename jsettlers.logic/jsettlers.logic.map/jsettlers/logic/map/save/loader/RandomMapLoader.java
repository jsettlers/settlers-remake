package jsettlers.logic.map.save.loader;

import java.io.IOException;
import java.util.Random;

import jsettlers.common.map.IMapData;
import jsettlers.common.map.MapLoadException;
import jsettlers.logic.map.random.RandomMapEvaluator;
import jsettlers.logic.map.random.RandomMapFile;
import jsettlers.logic.map.save.IListedMap;
import jsettlers.logic.map.save.MapFileHeader;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class RandomMapLoader extends MapLoader {

	private IMapData mapData = null;

	public RandomMapLoader(IListedMap file, MapFileHeader header) {
		super(file, header);
	}

	@Override
	public IMapData getMapData() throws MapLoadException {
		if (mapData != null) {
			return mapData;
		}

		// TODO: arguments
		int players = 3;
		int randomSeed = 3;

		RandomMapFile file;
		try {
			file = RandomMapFile.loadFromStream(super.getMapDataStream());

			RandomMapEvaluator evaluator = new RandomMapEvaluator(file.getInstructions(), players);
			evaluator.createMap(new Random(randomSeed));
			mapData = evaluator.getGrid();

			return mapData;
		} catch (IOException e) {
			throw new MapLoadException(e);
		}
	}
}
