package jsettlers.main;

import java.util.Date;

import jsettlers.common.map.MapLoadException;
import jsettlers.input.UIState;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.save.IGameCreator;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapLoader;

/**
 * This is a saved game, that can be deserialized again.
 * 
 * @author michael
 */
public class SavedGame implements IGameCreator {

	private final MapLoader loader;

	public SavedGame(MapLoader loader) {
		this.loader = loader;
	}

	public String getName() {
		try {
			return loader.getFileHeader().getName();
		} catch (MapLoadException e) {
			return "load error.";
		}
	}

	public Date getSaveTime() {
		try {
			return loader.getFileHeader().getDate();
		} catch (MapLoadException e) {
			return new Date();
		}
	}

	@Override
	public MainGrid getMainGrid(byte player) throws MapLoadException {
		return loader.getMainGrid(player);
	}

	@Override
	public UIState getUISettings(int player) throws MapLoadException {
		return loader.getUISettings(player);
	}

	public short[] getImage() {
		try {
			return loader.getFileHeader().getBgimage();
		} catch (MapLoadException e) {
			return new short[MapFileHeader.PREVIEW_IMAGE_SIZE * MapFileHeader.PREVIEW_IMAGE_SIZE];
		}
	}

}
