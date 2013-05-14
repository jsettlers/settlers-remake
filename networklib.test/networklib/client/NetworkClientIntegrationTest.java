package networklib.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import networklib.NetworkConstants;
import networklib.TestUtils;
import networklib.channel.AsyncChannel;
import networklib.channel.Channel;
import networklib.channel.TestPacket;
import networklib.channel.TestPacketListener;
import networklib.client.exceptions.InvalidStateException;
import networklib.client.receiver.BufferingPacketReceiver;
import networklib.client.time.TestClock;
import networklib.server.ServerManager;
import networklib.server.db.inMemory.InMemoryDB;
import networklib.server.game.EPlayerState;
import networklib.server.game.Player;
import networklib.server.packets.ArrayOfMatchInfosPacket;
import networklib.server.packets.ChatMessagePacket;
import networklib.server.packets.MapInfoPacket;
import networklib.server.packets.MatchInfoPacket;
import networklib.server.packets.MatchInfoUpdatePacket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link NetworkClient}s interaction with the server.
 * 
 * @author Andreas Eberle
 * 
 */
public class NetworkClientIntegrationTest {
	private static final String TEST_PLAYER_ID = "id-testPlayer";

	private InMemoryDB db = new InMemoryDB();
	private ServerManager manager = new ServerManager(db);

	private Channel server1Channel;
	private AsyncChannel client1Channel;
	private NetworkClient client1;

	private Channel server2Channel;
	private AsyncChannel client2Channel;
	private NetworkClient client2;

	@Before
	public void setUp() throws IOException {
		// set up first client
		AsyncChannel[] channels = TestUtils.setUpAsyncLoopbackChannels();
		client1Channel = channels[0];
		server1Channel = channels[1];

		client1 = new NetworkClient(client1Channel, null);
		manager.identifyNewChannel(server1Channel);

		// set up second client
		channels = TestUtils.setUpAsyncLoopbackChannels();
		client2Channel = channels[0];
		server2Channel = channels[1];

		client2 = new NetworkClient(client2Channel, null);
		manager.identifyNewChannel(server2Channel);
	}

	@After
	public void tearDown() {
		client1.close();
		server1Channel.close();

		client2.close();
		server2Channel.close();
	}

	@Test
	public void testConnection() throws InterruptedException {
		TestPacketListener listener = new TestPacketListener(NetworkConstants.Keys.REQUEST_MATCHES);
		client1Channel.registerListener(listener);

		TestPacket testPacket = new TestPacket("sdlfjsh", 2324);
		server1Channel.sendPacket(NetworkConstants.Keys.REQUEST_MATCHES, testPacket);

		Thread.sleep(10);

		assertEquals(1, listener.packets.size());
		assertEquals(testPacket, listener.packets.get(0));
	}

	@Test
	public void testLogIn() throws InvalidStateException, InterruptedException {
		final String playerName = "Name)2020j3j";

		logIn(client1, TEST_PLAYER_ID, playerName);
	}

	private void logIn(NetworkClient client, String playerId, String playerName) throws InvalidStateException, InterruptedException {
		int currentNumberOfPlayers = db.getNumberOfPlayers();

		client.logIn(playerId, playerName);

		Thread.sleep(40);

		assertEquals(EPlayerState.LOGGED_IN, client1.getState());

		assertEquals(currentNumberOfPlayers + 1, db.getNumberOfPlayers()); // ensure we have now one player more than before
		Player p = db.getPlayer(playerId);
		assertEquals(playerId, p.getId());
		assertEquals(playerName, p.getPlayerInfo().getName());
	}

	@Test(expected = InvalidStateException.class)
	public void testRequestMatchesInStateUnconnected() throws InvalidStateException {
		client1.requestMatches(null);
	}

	@Test(expected = InvalidStateException.class)
	public void testRequestPlayersRunningMatchesInStateUnconnected() throws InvalidStateException {
		client1.requestPlayersRunningMatches(null);
	}

	@Test(expected = InvalidStateException.class)
	public void testRequestOpenNewMatchInStateUnconnected() throws InvalidStateException {
		client1.requestOpenNewMatch(null, (byte) 0, null, null, null, null, null);
	}

	@Test(expected = InvalidStateException.class)
	public void testRequestOpenNewMatchInStateInMatch() throws InvalidStateException, InterruptedException {
		testOpenMatchWithLogin();
		client1.requestOpenNewMatch(null, (byte) 0, null, null, null, null, null);
	}

	@Test(expected = InvalidStateException.class)
	public void testRequestOpenNewMatchInStateInRunningMatch() throws InvalidStateException, InterruptedException {
		testOpenAndStartNewMatch();
		client1.requestOpenNewMatch(null, (byte) 0, null, null, null, null, null);
	}

	@Test(expected = InvalidStateException.class)
	public void testRequestStartMatchInStateUnconnected() throws InvalidStateException {
		client1.requestStartMatch();
	}

	@Test(expected = InvalidStateException.class)
	public void testRequestStartMatchInStateLoggedIn() throws InvalidStateException, InterruptedException {
		testLogIn();
		client1.requestStartMatch();
	}

	@Test(expected = InvalidStateException.class)
	public void testRequestStartMatchInStateInRunningMatch() throws InvalidStateException, InterruptedException {
		testOpenAndStartNewMatch();

		client1.requestStartMatch();
	}

	@Test
	public void testOpenAndStartNewMatch() throws InvalidStateException, InterruptedException {
		logIn(client1, "id1", "player1");

		openMatch(client1);

		client1.requestStartMatch();
		Thread.sleep(50);
		assertEquals(EPlayerState.IN_RUNNING_MATCH, client1.getState());
	}

	@Test
	public void testOpenStartAndJoinNewMatch() throws InvalidStateException, InterruptedException {
		logIn(client1, "id1", "player1");
		logIn(client2, "id2", "player2");

		openMatch(client1);

		client2.reqeustJoinMatch(new MatchInfoPacket(db.getJoinableMatches().get(0)), null, null, null, null);
		Thread.sleep(50);

		assertEquals(EPlayerState.IN_MATCH, client2.getState());

		client1.requestStartMatch();
		Thread.sleep(50);
		assertEquals(EPlayerState.IN_RUNNING_MATCH, client1.getState());
		assertEquals(EPlayerState.IN_RUNNING_MATCH, client2.getState());
	}

	@Test
	public void testLogInAndClose() throws InvalidStateException, InterruptedException {
		testLogIn();

		client1.close();

		Thread.sleep(10);
		assertEquals(0, db.getNumberOfPlayers());
		assertEquals(EPlayerState.DISCONNECTED, client1.getState());
	}

	@Test
	public void testCloseFromServerSide() throws InvalidStateException, InterruptedException {
		logIn(client1, "id1", "player1");
		logIn(client2, "id2", "player2");

		assertEquals(2, db.getNumberOfPlayers());

		server1Channel.close();

		Thread.sleep(10);
		assertEquals(1, db.getNumberOfPlayers());
		assertEquals(EPlayerState.DISCONNECTED, client1.getState());
	}

	@Test
	public void testOpenMatchWithLogin() throws InvalidStateException, InterruptedException {
		testLogIn();

		testOpenMatch();
	}

	/**
	 * NOTE: The client must already be logged in!
	 * 
	 * @throws InvalidStateException
	 * @throws InterruptedException
	 */
	private void testOpenMatch() throws InvalidStateException, InterruptedException {
		openMatch(client1);
	}

	private void openMatch(NetworkClient client) throws InvalidStateException, InterruptedException {
		BufferingPacketReceiver<ArrayOfMatchInfosPacket> matchesListener = new BufferingPacketReceiver<ArrayOfMatchInfosPacket>();
		assertEquals(0, matchesListener.popBufferedPackets().size());

		client.requestMatches(matchesListener);

		Thread.sleep(50);

		List<ArrayOfMatchInfosPacket> arrayOfMatches = matchesListener.popBufferedPackets();
		assertEquals(1, arrayOfMatches.size()); // check that we got one result for the request
		assertEquals(0, arrayOfMatches.get(0).getMatches().length); // currently no matches should be in the result, because non should be open

		BufferingPacketReceiver<MatchInfoUpdatePacket> matchUpdateListener = new BufferingPacketReceiver<MatchInfoUpdatePacket>();
		final String matchName = "TestMatch";
		final byte maxPlayers = (byte) 5;
		final MapInfoPacket mapInfo = new MapInfoPacket("mapid92329", "mapName", "authorId", "authorName");
		client.requestOpenNewMatch(matchName, maxPlayers, mapInfo, null, matchUpdateListener, null, null);

		Thread.sleep(100);

		List<MatchInfoUpdatePacket> matches = matchUpdateListener.popBufferedPackets();
		assertEquals(1, matches.size());
		MatchInfoPacket match = matches.get(0).getMatchInfo();
		assertEquals(matchName, match.getMatchName());
		assertEquals(maxPlayers, match.getMaxPlayers());
		assertEquals(mapInfo, match.getMapInfo());
	}

	@Test
	public void testGetPlayersRunningMatches() throws InvalidStateException, InterruptedException {
		testLogIn();

		BufferingPacketReceiver<ArrayOfMatchInfosPacket> listener = new BufferingPacketReceiver<ArrayOfMatchInfosPacket>();
		client1.requestPlayersRunningMatches(listener);

		Thread.sleep(50);
		List<ArrayOfMatchInfosPacket> packets = listener.popBufferedPackets();
		assertEquals(1, packets.size());
		assertEquals(0, packets.get(0).getMatches().length);

		testOpenMatch(); // open a new match.

		client1.requestStartMatch();
		client1.requestLeaveMatch();

		Thread.sleep(50);

		client1.requestPlayersRunningMatches(listener);

		Thread.sleep(50);
		packets = listener.popBufferedPackets();
		assertEquals(1, packets.size());
		assertEquals(1, packets.get(0).getMatches().length);
	}

	@Test
	public void testChatMessaging() throws InvalidStateException, InterruptedException {
		testLogIn();

		BufferingPacketReceiver<ChatMessagePacket> chatReceiver = new BufferingPacketReceiver<ChatMessagePacket>();
		client1.requestOpenNewMatch("TestMatch", (byte) 4, new MapInfoPacket("", "", "", ""), null, null, chatReceiver, null);

		Thread.sleep(50);
		assertEquals(EPlayerState.IN_MATCH, client1.getState());

		testSendAndReceiveChatMessage(chatReceiver);

		client1.requestStartMatch();

		Thread.sleep(50);
		assertEquals(EPlayerState.IN_RUNNING_MATCH, client1.getState());

		testSendAndReceiveChatMessage(chatReceiver);
	}

	private void testSendAndReceiveChatMessage(BufferingPacketReceiver<ChatMessagePacket> chatReceiver) throws InvalidStateException,
			InterruptedException {
		final String testMessage = "TestChatMessage‰ˆ¸lL‹‹÷LP?=))(=)(ß\"\\`!)ß$";
		client1.sendChatMessage(testMessage);

		assertEquals(0, chatReceiver.popBufferedPackets().size());

		Thread.sleep(50);
		List<ChatMessagePacket> chatMessages = chatReceiver.popBufferedPackets();
		assertEquals(1, chatMessages.size());
		assertEquals(TEST_PLAYER_ID, chatMessages.get(0).getAuthorId());
		assertEquals(testMessage, chatMessages.get(0).getMessage());
	}

	@Test
	public void testTimeSynchronization() throws InvalidStateException, InterruptedException {
		testOpenStartAndJoinNewMatch();

		TestClock clock1 = new TestClock();
		TestClock clock2 = new TestClock();

		client1.startTimeSynchronization(clock1);
		client2.startTimeSynchronization(clock2);

		Thread.sleep(NetworkConstants.Client.TIME_SYNC_SEND_INTERVALL + 20);
		assertEquals(0, clock1.popAdjustmentEvents().size());
		assertEquals(0, clock2.popAdjustmentEvents().size());

		clock1.setTime(1056);

		Thread.sleep(2 * NetworkConstants.Client.TIME_SYNC_SEND_INTERVALL + 20);
		assertTrue(clock1.getTime() - clock2.getTime() < NetworkConstants.Client.TIME_SYNC_TOLERATED_DIFFERENCE);
		assertTrue(clock1.popAdjustmentEvents().size() > 0);
		assertEquals(0, clock2.popAdjustmentEvents().size());

		clock2.setTime(23423423);

		Thread.sleep(6 * NetworkConstants.Client.TIME_SYNC_SEND_INTERVALL + 20); // wait for 6 synchronizations
		assertTrue("diff is to high: " + (clock2.getTime() - clock1.getTime()),
				clock2.getTime() - clock1.getTime() < NetworkConstants.Client.TIME_SYNC_TOLERATED_DIFFERENCE);
		assertTrue(clock2.popAdjustmentEvents().size() > 0);
		assertEquals(0, clock1.popAdjustmentEvents().size());
	}
}
