package jsettlers.main;

import jsettlers.common.map.MapLoadException;
import jsettlers.graphics.map.UIState;
import jsettlers.logic.map.newGrid.MainGrid;

/**
 * Classes of this interface are capable of creating a game.
 * 
 * @author michael
 */
public interface IGameCreator {
	public MainGrid getMainGrid() throws MapLoadException;
	public UIState getUISettings(int player) throws MapLoadException;
}
