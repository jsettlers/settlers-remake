package jsettlers.main.network;

import jsettlers.graphics.INetworkScreenAdapter;
import jsettlers.network.client.ClientThread;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface INetworkStartScreenEndListener {
	void leftMatch(INetworkScreenAdapter networkScreenAdapter);

	void networkMatchStarting(INetworkScreenAdapter networkScreenAdapter, ClientThread client);
}
