package jsettlers.logic.map.save.loader;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;

import jsettlers.common.map.IMapData;
import jsettlers.common.map.MapLoadException;
import jsettlers.input.UIState;
import jsettlers.logic.map.newGrid.GameSerializer;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.timer.RescheduleTimer;

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
			ObjectInputStream ois = new ObjectInputStream(super.getMapDataStream());

			UIState uiState = UIState.readFrom(ois);
			GameSerializer gameSerializer = new GameSerializer();
			MainGrid mainGrid = gameSerializer.load(ois);
			RescheduleTimer.loadFrom(ois);

			ois.close();

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
