package jsettlers.common.network;

import java.io.File;

/**
 * A map that can be played over the network as multiplayer map.
 * 
 * @author Andreas Eberle
 * 
 */
public interface INetworkableMap {
	/**
	 * 
	 * @return unique identifier of the map
	 */
	String getUniqueID();

	/**
	 * 
	 * @return name of the map that's visible to the user
	 */
	String getName();

	/**
	 * 
	 * @return file object of this map
	 */
	File getFile();

	/**
	 * 
	 * @return maximum number of players that can play on this map.
	 */
	int getMaxPlayers();
}
