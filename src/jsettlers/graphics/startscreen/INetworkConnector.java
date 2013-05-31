package jsettlers.graphics.startscreen;

import java.io.IOException;
import java.net.UnknownHostException;

import jsettlers.common.network.IMatch;

/**
 * This is the network connector the start screen needs.
 * <p>
 * It is used to display the list of active network games.
 * 
 * @author michael
 * @author Andreas Eberle
 */
public interface INetworkConnector {
	/**
	 * Lets the user set the address to use and inits retrieving of the open
	 * matches. A match update should be sent shortly afterwards.
	 * 
	 * @param address
	 *            The address as String.
	 * @throws UnknownHostException
	 *             If the given server can not be found.
	 * @throws IOException
	 *             If the connection could not be established.
	 */
	public void setServerAddress(String address) throws UnknownHostException,
	        IOException;

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
	 * terminates the used thread.
	 */
	public void disconnect();

	/**
	 * This listener is notified whenever the list of matches changed.
	 * 
	 * @author michael
	 */
	interface INetworkListener {
		public void matchListChanged(INetworkConnector connector);
	}
}
