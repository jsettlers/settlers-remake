package jsettlers.main;

import jsettlers.common.map.MapLoadException;
import jsettlers.input.UIState;
import jsettlers.logic.map.newGrid.MainGrid;

/**
 * Classes of this interface are capable of creating a game.
 * 
 * @author michael
 */
public interface IGameCreator {
	@Deprecated
	public MainGrid getMainGrid() throws MapLoadException;
	public MainGrid getMainGrid(byte player) throws MapLoadException;
	public UIState getUISettings(int player) throws MapLoadException;
}
