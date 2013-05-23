package networklib.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import networklib.NetworkConstants;
import networklib.TestUtils;
import networklib.channel.AsyncChannel;
import networklib.channel.Channel;
import networklib.channel.TestPacket;
import networklib.channel.TestPacketListener;
import networklib.channel.reject.RejectPacket;
import networklib.client.exceptions.InvalidStateException;
import networklib.client.receiver.BufferingPacketReceiver;
import networklib.client.task.TestTaskPacket;
import networklib.client.task.TestTaskScheduler;
import networklib.client.task.packets.SyncTasksPacket;
import networklib.client.time.TestClock;
import networklib.common.packets.ArrayOfMatchInfosPacket;
import networklib.common.packets.ChatMessagePacket;
import networklib.common.packets.MapInfoPacket;
import networklib.common.packets.MatchInfoPacket;
import networklib.common.packets.MatchInfoUpdatePacket;
import networklib.server.ServerManager;
import networklib.server.db.inMemory.InMemoryDB;
import networklib.server.game.EPlayerState;
import networklib.server.game.Player;

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

		MatchInfoPacket dbMatchInfo = new MatchInfoPacket(db.getJoinableMatches().get(0));
		assertEquals(dbMatchInfo, client1.getMatchInfo());
		assertEquals(1, client1.getMatchInfo().getPlayers().length);

		client2.requestJoinMatch(dbMatchInfo, null, null, null, new TestTaskScheduler());
		Thread.sleep(50);

		assertEquals(EPlayerState.IN_MATCH, client2.getState());
		dbMatchInfo = new MatchInfoPacket(db.getJoinableMatches().get(0));
		assertEquals(dbMatchInfo, client1.getMatchInfo());
		assertEquals(2, client1.getMatchInfo().getPlayers().length);

		client2.setReadyState(true);
		Thread.sleep(50);

		assertEquals(EPlayerState.IN_MATCH, client2.getState());
		dbMatchInfo = new MatchInfoPacket(db.getJoinableMatches().get(0));
		assertEquals(dbMatchInfo, client1.getMatchInfo());
		assertEquals(2, client1.getMatchInfo().getPlayers().length);
		assertTrue(client1.getMatchInfo().getPlayers()[1].isReady());

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
		client.requestOpenNewMatch(matchName, maxPlayers, mapInfo, null, matchUpdateListener, null, new TestTaskScheduler());

		Thread.sleep(100);

		List<MatchInfoUpdatePacket> matches = matchUpdateListener.popBufferedPackets();
		assertEquals(1, matches.size());
		MatchInfoPacket match = matches.get(0).getMatchInfo();
		assertEquals(matchName, match.getMatchName());
		assertEquals(maxPlayers, match.getMaxPlayers());
		assertEquals(mapInfo, match.getMapInfo());
		assertFalse(match.getPlayers()[0].isReady());

		client.setReadyState(true);

		Thread.sleep(150);
		matches = matchUpdateListener.popBufferedPackets();
		assertEquals(1, matches.size());
		match = matches.get(0).getMatchInfo();
		assertEquals(matchName, match.getMatchName());
		assertEquals(maxPlayers, match.getMaxPlayers());
		assertEquals(mapInfo, match.getMapInfo());
		assertTrue(match.getPlayers()[0].isReady());
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

		openMatch(client1); // open a new match.

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
		client1.requestOpenNewMatch("TestMatch", (byte) 4, new MapInfoPacket("", "", "", ""), null, null, chatReceiver, new TestTaskScheduler());

		Thread.sleep(80);
		assertEquals(EPlayerState.IN_MATCH, client1.getState());

		testSendAndReceiveChatMessage(chatReceiver);

		client1.setReadyState(true);
		Thread.sleep(50);

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

		TestClock clock1 = new TestClock(200);
		TestClock clock2 = new TestClock(210);

		client1.startTimeSynchronization(clock1);
		client2.startTimeSynchronization(clock2);

		Thread.sleep(NetworkConstants.Client.TIME_SYNC_SEND_INTERVALL + 20); // wait for 1 synchronizations
		assertEquals(0, clock1.popAdjustmentEvents().size()); // no adjustments should have happened, because the clocks are almost sync
		assertEquals(0, clock2.popAdjustmentEvents().size());

		clock1.setTime(2056); // put clock1 forward

		Thread.sleep(2 * NetworkConstants.Client.TIME_SYNC_SEND_INTERVALL + 20); // wait for 2 synchronizations
		assertTrue(clock1.getTime() - clock2.getTime() < NetworkConstants.Client.TIME_SYNC_TOLERATED_DIFFERENCE);
		assertTrue(clock1.popAdjustmentEvents().size() > 0);
		assertEquals(0, clock2.popAdjustmentEvents().size());

		clock2.setTime(23423423); // put clock2 forward

		Thread.sleep(6 * NetworkConstants.Client.TIME_SYNC_SEND_INTERVALL + 20); // wait for 6 synchronizations
		assertTrue("diff is to high: " + (clock2.getTime() - clock1.getTime()),
				clock2.getTime() - clock1.getTime() < NetworkConstants.Client.TIME_SYNC_TOLERATED_DIFFERENCE);
		assertTrue(clock2.popAdjustmentEvents().size() > 0);
		assertEquals(0, clock1.popAdjustmentEvents().size());
	}

	@Test
	public void testSyncTasksDistribution() throws InvalidStateException, InterruptedException {
		logIn(client1, "player1", "player1");
		logIn(client2, "player2", "player2");

		TestTaskScheduler taskScheduler1 = new TestTaskScheduler();
		TestTaskScheduler taskScheduler2 = new TestTaskScheduler();
		client1.requestOpenNewMatch("TestMatch", (byte) 4, new MapInfoPacket("", "", "", ""), null, null, null, taskScheduler1);

		Thread.sleep(70);
		assertEquals(EPlayerState.IN_MATCH, client1.getState());

		MatchInfoPacket matchInfo = client1.getMatchInfo();

		client2.requestJoinMatch(matchInfo, null, null, null, taskScheduler2);

		Thread.sleep(50);
		assertEquals(EPlayerState.IN_MATCH, client2.getState());

		client1.setReadyState(true);
		client2.setReadyState(true);
		Thread.sleep(30);
		client2.requestStartMatch();

		Thread.sleep(30); // Ensure that both clients are in a running match.
		assertEquals(EPlayerState.IN_RUNNING_MATCH, client1.getState());
		assertEquals(EPlayerState.IN_RUNNING_MATCH, client2.getState());

		// Set up and start the clock synchronization
		TestClock clock1 = new TestClock(0);
		TestClock clock2 = new TestClock(0);
		client1.startTimeSynchronization(clock1);
		client2.startTimeSynchronization(clock2);

		Thread.sleep(2 * NetworkConstants.Client.LOCKSTEP_PERIOD); // After two lockstep periods, there must be two locksteps.
		assertEquals(2, taskScheduler1.getUnlockedLockstepNumber());
		assertEquals(2, taskScheduler2.getUnlockedLockstepNumber());

		// After more than LOCKSTEP_DEFAULT_LEAD_STEPS periods, the lockstep counter must wait, to prevent it from running away.
		Thread.sleep((2 + NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS) * NetworkConstants.Client.LOCKSTEP_PERIOD);
		assertEquals(NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS, taskScheduler1.getUnlockedLockstepNumber());
		assertEquals(NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS, taskScheduler2.getUnlockedLockstepNumber());

		// Submit a task
		TestTaskPacket testTask = new TestTaskPacket("dsfsdf", 2342, (byte) -23);
		client2.submitTask(testTask);

		Thread.sleep(50);

		// The task may not be submitted to the clients yet, because the lockstep is blocked.
		assertEquals(0, taskScheduler1.popBufferedPackets().size());
		assertEquals(0, taskScheduler2.popBufferedPackets().size());

		// Now let one clock continue one lockstep period.
		clock1.setTime(NetworkConstants.Client.LOCKSTEP_PERIOD);

		Thread.sleep(NetworkConstants.Client.TIME_SYNC_SEND_INTERVALL + 30);

		List<SyncTasksPacket> packets1 = taskScheduler1.popBufferedPackets();
		assertEquals(1, packets1.size());
		assertEquals(1, packets1.get(0).getTasks().size());
		assertEquals(testTask, packets1.get(0).getTasks().get(0));
		List<SyncTasksPacket> packets2 = taskScheduler2.popBufferedPackets();
		assertEquals(1, packets2.size());
		assertEquals(1, packets2.get(0).getTasks().size());
		assertEquals(testTask, packets2.get(0).getTasks().get(0));

		Thread.sleep(2 * NetworkConstants.Client.LOCKSTEP_PERIOD); // Wait two more lockstep periods and check the run away protection again
		assertEquals(NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS + 1, taskScheduler1.getUnlockedLockstepNumber());
		assertEquals(NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS + 1, taskScheduler2.getUnlockedLockstepNumber());
	}

	@Test
	public void testStartMatchWithUnreadyPlayers() throws InvalidStateException, InterruptedException {
		logIn(client1, "player1", "player1");
		logIn(client2, "player2", "player2");

		openMatch(client1); // open match and join client2
		client2.requestJoinMatch(client1.getMatchInfo(), null, null, null, new TestTaskScheduler());

		BufferingPacketReceiver<RejectPacket> rejectReceiver2 = new BufferingPacketReceiver<RejectPacket>();
		client2.registerRejectReceiver(rejectReceiver2);

		Thread.sleep(50);
		assertEquals(0, rejectReceiver2.popBufferedPackets().size());

		client2.requestStartMatch(); // try to start match with unready player2 => match must not start
		Thread.sleep(50);

		assertSingleRejectPacket(rejectReceiver2, NetworkConstants.Keys.REQUEST_START_MATCH, NetworkConstants.Messages.NOT_ALL_PLAYERS_READY);

		client2.setReadyState(true); // set player2 ready and player1 unready => match must not start
		Thread.sleep(20);
		client1.setReadyState(false);
		Thread.sleep(20);
		client2.requestStartMatch();
		Thread.sleep(50);

		assertSingleRejectPacket(rejectReceiver2, NetworkConstants.Keys.REQUEST_START_MATCH, NetworkConstants.Messages.NOT_ALL_PLAYERS_READY);

		client1.setReadyState(true); // set player1 ready => must must start
		Thread.sleep(20);
		client2.requestStartMatch();
		Thread.sleep(50);

		assertEquals(EPlayerState.IN_RUNNING_MATCH, client1.getState());
		assertEquals(EPlayerState.IN_RUNNING_MATCH, client2.getState());
	}

	private void assertSingleRejectPacket(BufferingPacketReceiver<RejectPacket> rejectReceiver, int expectedRejectedKey, int expectedMessage) {
		List<RejectPacket> rejectPackets = rejectReceiver.popBufferedPackets();
		assertEquals(1, rejectPackets.size());
		assertEquals(expectedRejectedKey, rejectPackets.get(0).getRejectedKey());
		assertEquals(expectedMessage, rejectPackets.get(0).getErrorMessageId());
	}
}
