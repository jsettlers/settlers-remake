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

public class MultiClientServerTest {
	private static MatchesInfoList globalMatchesList;;

	public static void main(String args[]) throws UnknownHostException, IOException, InterruptedException {
		ClientThread c1 = new ClientThread("localhost", new ClientThreadListener());
		ClientThread c2 = new ClientThread("localhost", new ClientThreadListener());
		ClientThread c3 = new ClientThread("localhost", new ClientThreadListener());
		ClientThread c4 = new ClientThread("localhost", new ClientThreadListener());
		c1.start();
		c2.start();
		c3.start();
		c4.start();
		Thread.sleep(1000);
		c1.setPlayerName("client1");
		c2.setPlayerName("client2");
		c3.setPlayerName("client3");
		c4.setPlayerName("client4");
		Thread.sleep(1000);

		c1.startNewMatch(new MatchDescription(new TestMatchSettings("match 1"), "map of 1", "map1"), new File("maps/test1.map"));
		Thread.sleep(200);
		c2.requestMatchesList();
		c3.startNewMatch(new MatchDescription(new TestMatchSettings("match of 3"), "map of 3", "map2"), new File("maps/test2.map"));
		Thread.sleep(100);
		c4.requestMatchesList();
		Thread.sleep(400);

		c4.joinMatch(globalMatchesList.getMatches().get(0).getDescription().getMatchId());

		Thread.sleep(100);

		c2.requestMatchesList();

		Thread.sleep(100);

		c2.joinMatch(globalMatchesList.getMatches().get(0).getDescription().getMatchId());

		Thread.sleep(300);
		c4.proxyObjectToTeammates(new NetworkableTestObject("test object 1"));

		Thread.sleep(400);
		c4.leaveMatch();
		c1.requestMatchAttendants();

		Thread.sleep(10000);
		System.exit(0);
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
			globalMatchesList = matchesList;
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
			Log.log("received match attendants list:------------");
			for (String curr : matchAttendants) {
				System.out.println(curr);
			}
		}
	}
}
