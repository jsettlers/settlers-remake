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

	public MainGridWithUiSettings loadMainGrid(byte player) throws MapLoadException;

	public String getMapName();

	public String getMapID();

	public class MainGridWithUiSettings {
		private final MainGrid mainGrid;
		private final UIState uiState;

		public MainGridWithUiSettings(MainGrid mainGrid, UIState uiState) {
			this.mainGrid = mainGrid;
			this.uiState = uiState;
		}

		public MainGrid getMainGrid() {
			return mainGrid;
		}

		public UIState getUiState() {
			return uiState;
		}
	}
}
