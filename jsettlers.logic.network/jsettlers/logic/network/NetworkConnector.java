package jsettlers.logic.network;

import java.io.File;
import java.io.IOException;

import jsettlers.common.network.IMatch;
import jsettlers.common.network.IMatchSettings;
import jsettlers.common.network.INetworkConnector;
import jsettlers.common.network.INetworkConnectorListener;
import jsettlers.common.network.INetworkableMap;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.MapLoader;
import jsettlers.network.client.ClientThread;
import jsettlers.network.client.IClientThreadListener;
import jsettlers.network.client.INetworkableObject;
import jsettlers.network.client.request.EClientRequest;
import jsettlers.network.server.log.Log;
import jsettlers.network.server.match.Match;
import jsettlers.network.server.match.MatchDescription;
import jsettlers.network.server.response.MatchesInfoList;

public class NetworkConnector implements INetworkConnector, IClientThreadListener {

	private ClientThread clientThread = null;
	private INetworkConnectorListener listener;
	private IMatch[] matches;

	@Override
	public void connectToServer(String host) throws IOException {
		cancelConnection();
		clientThread = new ClientThread(host, this);
		clientThread.start();
	}

	@Override
	public void cancelConnection() {
		if (clientThread != null) {
			clientThread.cancelConnection();
			clientThread = null;
		}
	}

	@Override
	public void setPlayerName(String playerName) {
		try {
			clientThread.setPlayerName(playerName);
		} catch (IOException e) { // TODO implement correct error handling
			e.printStackTrace();
		}
	}

	@Override
	public void startMatch(IMatchSettings settings, INetworkableMap map) {
		try {
			clientThread.startNewMatch(new MatchDescription(settings, map.getUniqueID(), map.getName()), map.getFile());
		} catch (IOException e) { // TODO implement correct error handling
			e.printStackTrace();
		}
	}

	@Override
	public void leaveCurrentMatch() {
		try {
			clientThread.leaveMatch();
		} catch (IOException e) { // TODO implement correct error handling
			e.printStackTrace();
		}
	}

	@Override
	public IMatch[] getMatches() {
		return matches;
	}

	@Override
	public void joinMatch(IMatch match) {
		try {
			clientThread.joinMatch(match.getMatchID());

			for (MapLoader curr : MapList.getDefaultList().getFreshMaps()) {
				if (match.getMapID().equals(curr.getMapID())) {
					return;
				}
			}

			clientThread.requestMap();
		} catch (IOException e) { // TODO implement correct error handling
			e.printStackTrace();
		}
	}

	@Override
	public void setListener(INetworkConnectorListener listener) {
		this.listener = listener;
	}

	@Override
	public void removeListener() {
		this.listener = null;
	}

	// methods from interface IClientThreadListener----------------------------------------------------

	@Override
	public void requestFailedEvent(EClientRequest failedRequest) {
		Log.log("request failed: " + failedRequest);
		listener.requestFailed(failedRequest.toString());
	}

	@Override
	public void joinedMatchEvent(MatchDescription match) {
		listener.joinedMatch();
	}

	@Override
	public void retrievedMatchesEvent(MatchesInfoList matchesList) {
		IMatch[] matches = new IMatch[matchesList.getMatches().size()];
		int i = 0;
		for (Match m : matchesList.getMatches()) {
			matches[i] = new NetworkMatch(m.getMatchId(), m.getDescription().getMatchName(), m.getDescription().getMapId(), m.getDescription()
					.getMaxPlayers());
			i++;
		}
		this.matches = matches;

		listener.retrievedMatches();
	}

	@Override
	public void receivedProxiedObjectEvent(String sender, INetworkableObject proxiedObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerLeftEvent(String leavingPlayer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mapReceivedEvent() {
		Log.log("received map");
		listener.receivedMap();
	}

	@Override
	public File getMapFolder() {
		return MapList.getDefaultFolder();
	}

}
