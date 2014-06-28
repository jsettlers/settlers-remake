package jsettlers.logic.map.save;

import jsettlers.common.map.MapLoadException;
import jsettlers.input.PlayerState;
import jsettlers.logic.map.newGrid.MainGrid;

/**
 * Classes of this interface are capable of creating a game.
 * 
 * @author michael
 * @author Andreas Eberle
 */
public interface IGameCreator {

	public MainGridWithUiSettings loadMainGrid() throws MapLoadException;

	public String getMapName();

	public String getMapID();

	public class MainGridWithUiSettings {
		private final MainGrid mainGrid;
		private final PlayerState[] playerStates;

		public MainGridWithUiSettings(MainGrid mainGrid, PlayerState[] playerStates) {
			this.mainGrid = mainGrid;
			this.playerStates = playerStates;
		}

		public MainGrid getMainGrid() {
			return mainGrid;
		}

		public PlayerState[] getPlayerStates() {
			return playerStates;
		}

		public PlayerState getPlayerState(byte playerId) {
			return playerStates[playerId];
		}
	}
}
