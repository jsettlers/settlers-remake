package jsettlers.logic.map.save.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jsettlers.common.map.IMapData;
import jsettlers.common.map.MapLoadException;
import jsettlers.input.UIState;
import jsettlers.logic.map.newGrid.GameSerializer;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.save.MapFileHeader;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class SavegameLoader extends MapLoader {

	public SavegameLoader(File file, MapFileHeader header) {
		super(file, header);
	}

	@Override
	public MainGridWithUiSettings loadMainGrid(byte player) throws MapLoadException {
		try {
			InputStream inStream = super.getMapDataStream();

			UIState uiState = UIState.readFrom(inStream);
			GameSerializer gameSerializer = new GameSerializer();
			MainGrid mainGrid = gameSerializer.load(inStream);

			return new MainGridWithUiSettings(mainGrid, uiState);
		} catch (IOException ex) {
			throw new MapLoadException(ex);
		}
	}

	@Override
	public IMapData getMapData() throws MapLoadException {
		throw new UnsupportedOperationException("A savegame can't supply IMapData");
	}
}
