package jsettlers.logic.map.save;

import jsettlers.common.map.MapLoadException;
import jsettlers.input.UIState;
import jsettlers.logic.map.newGrid.MainGrid;

/**
 * Classes of this interface are capable of creating a game.
 * 
 * @author michael
 * @author Andreas Eberle
 */
public interface IGameCreator {

	public MainGrid getMainGrid(byte player) throws MapLoadException;

	public UIState getUISettings(int player) throws MapLoadException;
}
