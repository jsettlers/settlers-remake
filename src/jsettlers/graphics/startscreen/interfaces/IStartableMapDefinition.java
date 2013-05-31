package jsettlers.graphics.startscreen.interfaces;


/**
 * This is a map that is startable for a new game.
 * <p>
 * Currently, we only support normal maps, but this might as well be a random
 * map generator.
 * 
 * @author michael
 */
public interface IStartableMapDefinition extends IMapDefinition {
	/**
	 * Gets the minimum number of players that can play this map.
	 * @return That number.
	 */
	int getMinPlayers();

	/**
	 * Gets the maximum number of players supported by this map.
	 * @return The number of players supported by this map.
	 */
	int getMaxPlayers();
	
	/* XXX: getUUID() */

}
