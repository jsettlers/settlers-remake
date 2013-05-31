package jsettlers.graphics.startscreen.interfaces;

import java.util.List;

/**
 * Defines a map that is loadable by us.
 * @author michael
 *
 */
public interface ILoadableMapDefinition extends IMapDefinition {
	/**
	 * Gets a list of players that played on the map.
	 * @return The players from that loadable game.
	 */
	public List<ILoadableMapPlayer> getPlayers();
}
