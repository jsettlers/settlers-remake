package jsettlers.network.client.interfaces;

import jsettlers.network.synchronic.timer.INetworkTimerable;

/**
 * Interface acting as an access point to the network functionality needed by a starting and then active match.
 * 
 * @author Andreas Eberle
 * 
 */
public interface INetworkConnector {
	/**
	 * 
	 * @return Returns the {@link ITaskScheduler} of used for this match.
	 */
	ITaskScheduler getTaskScheduler();

	/**
	 * 
	 * @return Returns the {@link IGameClock} that can be used to attach {@link INetworkTimerable}s for synchronous execution.
	 */
	IGameClock getGameClock();

	/**
	 * Shuts down the network connector.
	 */
	void shutdown();

	void setStartFinished(boolean startFinished);

	boolean haveAllPlayersStartFinished();
}
