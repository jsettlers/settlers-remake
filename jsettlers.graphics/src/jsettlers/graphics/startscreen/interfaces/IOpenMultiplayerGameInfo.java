package jsettlers.graphics.startscreen.interfaces;

/**
 * This interface defines methods to retrieve the data needed to open up a new multiplayer game.
 * 
 * @author Andreas Eberle
 */
public interface IOpenMultiplayerGameInfo {

	String getMatchName();

	IMapDefinition getMapDefinition();

	int getMaxPlayers();
}
