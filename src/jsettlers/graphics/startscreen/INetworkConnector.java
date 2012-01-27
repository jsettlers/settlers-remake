package jsettlers.graphics.startscreen;

import jsettlers.common.network.IMatch;

/**
 * This is the network connector the start screen needs.
 * <p>
 * It is used to display the list of active network games.
 * 
 * @author michael
 */
public interface INetworkConnector {
	/**
	 * Lets the user set the address to use. A match update event is sent
	 * shortly afterwards.
	 * 
	 * @param address
	 *            The address as String.
	 */
	public void setServerAddress(String address);

	/**
	 * Gets the currently used server address. Should be the last address set.
	 * 
	 * @return The address
	 */
	public String getServerAddress();

	/**
	 * Sets a listener to notify when the list changes.
	 * 
	 * @param listener
	 *            The lister. May be null, then no listenr is used.
	 */
	public void setListener(INetworkListener listener);

	/**
	 * Gets an array of matches. <br>
	 * NOTE: elements of the array might be null!!
	 * 
	 * @return the array of matches
	 */
	public IMatch[] getMatches();

	/**
	 * This listener is notified whenever the list of matches changed.
	 * 
	 * @author michael
	 */
	interface INetworkListener {
		public void matchListChanged(INetworkConnector connector);
	}
}
