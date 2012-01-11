package jsettlers.main;

import java.util.Date;

import jsettlers.common.map.MapLoadException;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.map.UIState;
import jsettlers.graphics.startscreen.IStartScreenConnector.ILoadableGame;
import jsettlers.logic.map.newGrid.GameSerializer;
import jsettlers.logic.map.newGrid.MainGrid;


/**
 * This is a saved game, that can be deserialized again.
 * @author michael
 *
 */
public class SavedGame implements ILoadableGame, IGameCreator {
	
	private final String filename;

	public SavedGame(String filename) {
		this.filename = filename;
		
	}
	
	@Override
    public String getName() {
	    return null;
    }

	@Override
    public Date getSaveTime() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public MainGrid getMainGrid() throws MapLoadException {
		GameSerializer gameSerializer = new GameSerializer();
		MainGrid grid = gameSerializer.load(filename);

	    return grid;
    }

	@Override
    public UIState getUISettings(int player) throws MapLoadException {
	    return new UIState(0, new ShortPoint2D(0, 0));
    }

}
