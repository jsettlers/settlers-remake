package jsettlers.network.test;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import jsettlers.network.client.ClientThread;
import jsettlers.network.client.IClientThreadListener;
import jsettlers.network.client.INetworkableObject;
import jsettlers.network.client.request.EClientRequest;
import jsettlers.network.server.log.Log;
import jsettlers.network.server.match.Match;
import jsettlers.network.server.match.MatchDescription;
import jsettlers.network.server.response.MatchesInfoList;

public class SimpleClientThreadTester {
	public static void main(String args[]) throws UnknownHostException, IOException, InterruptedException {
		ClientThread clientThread = new ClientThread("localhost", new ClientThreadListener());
		clientThread.setPlayerName("client1");
		clientThread.start();
		clientThread.sendTestToServer("(1) this is a test message from the client");
		Thread.sleep(1000);
		clientThread.sendTestToServer("(2) message from the client");
		Thread.sleep(1000);
		clientThread.requestMatchesList();
		Log.log("start new Match");
		clientThread.startNewMatch(new MatchDescription(new TestMatchSettings("match1"), "test1.map", "client map"), new File("maps/test1.map"));
		clientThread.requestMatchesList();
		Thread.sleep(2000);
		clientThread.requestMap();
		Thread.sleep(2000);
		clientThread.cancelConnection();

		Thread.sleep(10000);
	}

	private static class ClientThreadListener implements IClientThreadListener {

		@Override
		public void requestFailedEvent(EClientRequest failedRequest) {
			Log.error("request failed: " + failedRequest);
		}

		@Override
		public void joinedMatchEvent(MatchDescription match) {
			Log.log("joined match: " + match);
		}

		@Override
		public void retrievedMatchesEvent(MatchesInfoList matchesList) {
			Log.log("retrieved list of matches: ------------------------------");
			for (Match m : matchesList.getMatches()) {
				System.out.println(m);
			}
		}

		@Override
		public void receivedProxiedObjectEvent(String sender, INetworkableObject proxiedObject) {
			Log.log("received proxied object that was send by " + sender + ":\n" + proxiedObject);
		}

		@Override
		public void playerLeftEvent(String leavingPlayer) {
			Log.log("received message that player " + leavingPlayer + " left the match.");
		}

		@Override
		public void mapReceivedEvent() {
			Log.log("received map");
		}

		@Override
		public File getMapFolder() {
			return new File("received_maps/");
		}

		@Override
		public void receivedMatchAttendants(String[] matchAttendants) {
			Log.log("received match attendants list:----------------");
			for (String curr : matchAttendants) {
				System.out.println(curr);
			}
		}
	}

}
