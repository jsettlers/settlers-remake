package jsettlers.common.network;

/**
 * Interface for network matches to be able to view them on the GUI.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IMatch {
	/**
	 * 
	 * @return unique identifier of the match.
	 */
	String getMatchID();

	/**
	 * 
	 * @return name of the match.
	 */
	String getMatchName();

	/**
	 * 
	 * @return number of players that can join the match at maximum
	 */
	int getMaxPlayers();

	/**
	 * 
	 * @return unique id of the map.
	 */
	String getMapID();
}
