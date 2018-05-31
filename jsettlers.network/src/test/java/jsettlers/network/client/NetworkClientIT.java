/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.network.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import jsettlers.network.NetworkConstants;
import jsettlers.network.TestUtils;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.NetworkConstants.ENetworkMessage;
import jsettlers.network.client.NetworkClient;
import jsettlers.network.client.receiver.BufferingPacketReceiver;
import jsettlers.network.client.task.TestTaskPacket;
import jsettlers.network.client.task.packets.TaskPacket;
import jsettlers.network.common.packets.ArrayOfMatchInfosPacket;
import jsettlers.network.common.packets.ChatMessagePacket;
import jsettlers.network.common.packets.MapInfoPacket;
import jsettlers.network.common.packets.MatchInfoPacket;
import jsettlers.network.common.packets.MatchInfoUpdatePacket;
import jsettlers.network.infrastructure.channel.AsyncChannel;
import jsettlers.network.infrastructure.channel.Channel;
import jsettlers.network.infrastructure.channel.TestPacket;
import jsettlers.network.infrastructure.channel.TestPacketListener;
import jsettlers.network.infrastructure.channel.reject.RejectPacket;
import jsettlers.network.server.ServerManager;
import jsettlers.network.server.db.inMemory.InMemoryDB;
import jsettlers.network.server.match.EPlayerState;
import jsettlers.network.server.match.Player;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the {@link NetworkClient}s interaction with the server.
 * 
 * @author Andreas Eberle
 * 
 */
@Ignore
public class NetworkClientIT {
	private static final String TEST_PLAYER_ID = "id-testPlayer";

	private InMemoryDB db = new InMemoryDB();
	private ServerManager manager = new ServerManager(db);

	private Channel server1Channel;
	private AsyncChannel client1Channel;
	private NetworkClient client1;
	private NetworkClientClockMock clock1;

	private Channel server2Channel;
	private AsyncChannel client2Channel;
	private NetworkClient client2;
	private NetworkClientClockMock clock2;

	@Before
	public void setUp() throws IOException {
		manager.start();

		// set up first client
		AsyncChannel[] channels = TestUtils.setUpAsyncLoopbackChannels();
		client1Channel = channels[0];
		server1Channel = channels[1];

		clock1 = new NetworkClientClockMock();
		clock2 = new NetworkClientClockMock();

		client1 = new NetworkClient(client1Channel, null, clock1);
		manager.identifyNewChannel(server1Channel);

		// set up second client
		channels = TestUtils.setUpAsyncLoopbackChannels();
		client2Channel = channels[0];
		server2Channel = channels[1];

		client2 = new NetworkClient(client2Channel, null, clock2);
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
		TestPacketListener listener = new TestPacketListener(NetworkConstants.ENetworkKey.TEST_PACKET);
		client1Channel.registerListener(listener);

		TestPacket testPacket = new TestPacket("sdlfjsh", 2324);
		server1Channel.sendPacket(NetworkConstants.ENetworkKey.TEST_PACKET, testPacket);

		Thread.sleep(10L);

		assertEquals(1, listener.packets.size());
		assertEquals(testPacket, listener.packets.get(0));
	}

	@Test
	public void testLogIn() throws IllegalStateException, InterruptedException {
		final String playerName = "Name)2020j3j";

		logIn(client1, TEST_PLAYER_ID, playerName);
	}

	private void logIn(NetworkClient client, String playerId, String playerName) throws IllegalStateException, InterruptedException {
		int currentNumberOfPlayers = db.getNumberOfPlayers();

		client.logIn(playerId, playerName, null);

		Thread.sleep(40L);

		assertEquals(EPlayerState.LOGGED_IN, client.getState());

		assertEquals(currentNumberOfPlayers + 1, db.getNumberOfPlayers()); // ensure we have now one player more than before
		Player p = db.getPlayer(playerId);
		assertEquals(playerId, p.getId());
		assertEquals(playerName, p.getPlayerInfo().getName());
	}

	@Test(expected = IllegalStateException.class)
	public void testRequestOpenNewMatchInStateUnconnected() throws IllegalStateException {
		client1.openNewMatch(null, (byte) 0, null, 4711L, null, null, null);
	}

	@Test(expected = IllegalStateException.class)
	public void testRequestOpenNewMatchInStateInMatch() throws IllegalStateException, InterruptedException {
		testOpenMatchWithLogin();
		client1.openNewMatch(null, (byte) 0, null, 4711L, null, null, null);
	}

	@Test(expected = IllegalStateException.class)
	public void testRequestOpenNewMatchInStateInRunningMatch() throws IllegalStateException, InterruptedException {
		testOpenAndStartNewMatch();
		client1.openNewMatch(null, (byte) 0, null, 4711L, null, null, null);
	}

	@Test(expected = IllegalStateException.class)
	public void testRequestStartMatchInStateUnconnected() throws IllegalStateException {
		client1.startMatch();
	}

	@Test(expected = IllegalStateException.class)
	public void testRequestStartMatchInStateLoggedIn() throws IllegalStateException, InterruptedException {
		testLogIn();
		client1.startMatch();
	}

	@Test(expected = IllegalStateException.class)
	public void testRequestStartMatchInStateInRunningMatch() throws IllegalStateException, InterruptedException {
		testOpenAndStartNewMatch();

		client1.startMatch();
	}

	@Test
	public void testOpenAndStartNewMatch() throws IllegalStateException, InterruptedException {
		openMatch("id1", "player1", client1);

		client1.startMatch();
		Thread.sleep(50L);
		assertEquals(EPlayerState.IN_RUNNING_MATCH, client1.getState());
	}

	@Test
	public void testOpenStartAndJoinNewMatch() throws IllegalStateException, InterruptedException {
		logIn(client2, "id2", "player2");

		openMatch("id1", "player1", client1);

		MatchInfoPacket dbMatchInfo = new MatchInfoPacket(db.getJoinableMatches().get(0));
		assertEquals(dbMatchInfo, client1.getMatchInfo());
		assertEquals(1, client1.getMatchInfo().getPlayers().length);

		client2.joinMatch(dbMatchInfo.getId(), null, null, null);
		Thread.sleep(50L);

		assertEquals(EPlayerState.IN_MATCH, client2.getState());
		dbMatchInfo = new MatchInfoPacket(db.getJoinableMatches().get(0));
		assertEquals(dbMatchInfo, client1.getMatchInfo());
		assertEquals(2, client1.getMatchInfo().getPlayers().length);

		client2.setReadyState(true);
		Thread.sleep(50L);

		assertEquals(EPlayerState.IN_MATCH, client2.getState());
		dbMatchInfo = new MatchInfoPacket(db.getJoinableMatches().get(0));
		assertEquals(dbMatchInfo, client1.getMatchInfo());
		assertEquals(2, client1.getMatchInfo().getPlayers().length);
		assertTrue(client1.getMatchInfo().getPlayers()[1].isReady());

		client1.startMatch();
		Thread.sleep(50);
		assertEquals(EPlayerState.IN_RUNNING_MATCH, client1.getState());
		assertEquals(EPlayerState.IN_RUNNING_MATCH, client2.getState());
	}

	@Test
	public void testLogInAndClose() throws IllegalStateException, InterruptedException {
		testLogIn();

		client1.close();

		Thread.sleep(10L);
		assertEquals(0, db.getNumberOfPlayers());
		assertEquals(EPlayerState.DISCONNECTED, client1.getState());
	}

	@Test
	public void testCloseFromServerSide() throws IllegalStateException, InterruptedException {
		logIn(client1, "id1", "player1");
		logIn(client2, "id2", "player2");

		assertEquals(2, db.getNumberOfPlayers());

		server1Channel.close();

		Thread.sleep(10L);
		assertEquals(1, db.getNumberOfPlayers());
		assertEquals(EPlayerState.DISCONNECTED, client1.getState());
	}

	@Test
	public void testOpenMatchWithLogin() throws IllegalStateException, InterruptedException {
		openMatch("player1", "player1", client1);
	}

	private void openMatch(String id, String name, NetworkClient client) throws IllegalStateException, InterruptedException {
		BufferingPacketReceiver<ArrayOfMatchInfosPacket> matchesReceiver = new BufferingPacketReceiver<>();
		assertEquals(0, matchesReceiver.popBufferedPackets().size());

		client.logIn(id, name, matchesReceiver);

		Thread.sleep(50L);

		List<ArrayOfMatchInfosPacket> arrayOfMatches = matchesReceiver.popBufferedPackets();
		assertEquals(1, arrayOfMatches.size()); // check that we got one result for the request
		assertEquals(0, arrayOfMatches.get(0).getMatches().length); // currently no matches should be in the result, because non should be open

		BufferingPacketReceiver<MatchInfoUpdatePacket> matchUpdateListener = new BufferingPacketReceiver<>();
		final String matchName = "TestMatch";
		final byte maxPlayers = (byte) 5;
		final MapInfoPacket mapInfo = new MapInfoPacket("mapid92329", "mapName", "authorId", "authorName", 5);
		client.openNewMatch(matchName, maxPlayers, mapInfo, -4712L, null, matchUpdateListener, null);

		Thread.sleep(100L);

		List<MatchInfoUpdatePacket> matches = matchUpdateListener.popBufferedPackets();
		assertEquals(1, matches.size());
		MatchInfoPacket match = matches.get(0).getMatchInfo();
		assertEquals(matchName, match.getMatchName());
		assertEquals(maxPlayers, match.getMaxPlayers());
		assertEquals(mapInfo, match.getMapInfo());
		assertFalse(match.getPlayers()[0].isReady());

		client.setReadyState(true);

		Thread.sleep(150L);
		matches = matchUpdateListener.popBufferedPackets();
		assertEquals(1, matches.size());
		match = matches.get(0).getMatchInfo();
		assertEquals(matchName, match.getMatchName());
		assertEquals(maxPlayers, match.getMaxPlayers());
		assertEquals(mapInfo, match.getMapInfo());
		assertTrue(match.getPlayers()[0].isReady());
	}

	@Test
	public void testChatMessaging() throws IllegalStateException, InterruptedException {
		testLogIn();

		BufferingPacketReceiver<ChatMessagePacket> chatReceiver = new BufferingPacketReceiver<>();
		client1.openNewMatch("TestMatch", 4, new MapInfoPacket("", "", "", "", 9), 923409340394293842L, null, null, chatReceiver);

		Thread.sleep(80L);
		assertEquals(EPlayerState.IN_MATCH, client1.getState());

		testSendAndReceiveChatMessage(chatReceiver);

		client1.setReadyState(true);
		Thread.sleep(50L);

		testSendAndReceiveChatMessage(chatReceiver);

		client1.startMatch();

		Thread.sleep(50L);
		assertEquals(EPlayerState.IN_RUNNING_MATCH, client1.getState());

		testSendAndReceiveChatMessage(chatReceiver);
	}

	private void testSendAndReceiveChatMessage(BufferingPacketReceiver<ChatMessagePacket> chatReceiver) throws IllegalStateException,
			InterruptedException {
		final String testMessage = "TestChatMessage���lL���LP?=))(=)(�\"\\`!)�$";
		client1.sendChatMessage(testMessage);

		assertEquals(0, chatReceiver.popBufferedPackets().size());

		Thread.sleep(50L);
		List<ChatMessagePacket> chatMessages = chatReceiver.popBufferedPackets();
		assertEquals(1, chatMessages.size());
		assertEquals(TEST_PLAYER_ID, chatMessages.get(0).getAuthorId());
		assertEquals(testMessage, chatMessages.get(0).getMessage());
	}

	@Test
	public void testTimeSynchronization() throws IllegalStateException, InterruptedException {
		testOpenStartAndJoinNewMatch();

		clock1.setTime(200);
		clock2.setTime(210);

		Thread.sleep(NetworkConstants.Client.TIME_SYNC_SEND_INTERVALL + 20L); // wait for 1 synchronizations
		assertEquals(0, clock1.popAdjustmentEvents().size()); // no adjustments should have happened, because the clocks are almost sync
		assertEquals(0, clock2.popAdjustmentEvents().size());

		clock1.setTime(2056); // put clock1 forward

		Thread.sleep(3L * NetworkConstants.Client.TIME_SYNC_SEND_INTERVALL + 20L); // wait for 3 synchronizations
		int diff = Math.abs(clock1.getTime() - clock2.getTime());
		assertTrue("diff is to high: " + diff, diff < NetworkConstants.Client.TIME_SYNC_TOLERATED_DIFFERENCE);
		assertTrue(clock1.popAdjustmentEvents().size() > 0);
		assertEquals(0, clock2.popAdjustmentEvents().size());

		clock2.setTime(423423); // put clock2 forward

		Thread.sleep(6L * NetworkConstants.Client.TIME_SYNC_SEND_INTERVALL + 20L); // wait for 6 synchronizations
		diff = Math.abs(clock2.getTime() - clock1.getTime());
		assertTrue("diff is to high: " + diff, diff < NetworkConstants.Client.TIME_SYNC_TOLERATED_DIFFERENCE);
		assertTrue(clock2.popAdjustmentEvents().size() > 0);
		assertEquals(0, clock1.popAdjustmentEvents().size());
	}

	@Test
	public void testSyncTasksDistribution() throws IllegalStateException, InterruptedException {
		logIn(client1, "player1", "player1");
		logIn(client2, "player2", "player2");

		client1.openNewMatch("TestMatch", 4, new MapInfoPacket("", "", "", "", 4), 34L, null, null, null);

		Thread.sleep(150L);
		assertEquals(EPlayerState.IN_MATCH, client1.getState());

		MatchInfoPacket matchInfo = client1.getMatchInfo();

		client2.joinMatch(matchInfo.getId(), null, null, null);

		Thread.sleep(50L);
		assertEquals(EPlayerState.IN_MATCH, client2.getState());

		client1.setReadyState(true);
		client2.setReadyState(true);
		Thread.sleep(30L);
		client2.startMatch();

		Thread.sleep(30 + NetworkConstants.Client.LOCKSTEP_PERIOD); // Ensure that both clients are in a running match.
		assertEquals(EPlayerState.IN_RUNNING_MATCH, client1.getState());
		assertEquals(EPlayerState.IN_RUNNING_MATCH, client2.getState());

		Thread.sleep(2 * NetworkConstants.Client.LOCKSTEP_PERIOD); // After two lockstep periods, there must be two locksteps.
		assertEquals(NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS, clock1.getAllowedLockstep());
		assertEquals(NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS, clock2.getAllowedLockstep());

		// After more than LOCKSTEP_DEFAULT_LEAD_STEPS periods, the lockstep counter must wait, to prevent it from running away.
		Thread.sleep((2 + NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS) * NetworkConstants.Client.LOCKSTEP_PERIOD);
		assertEquals(NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS, clock1.getAllowedLockstep());
		assertEquals(NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS, clock2.getAllowedLockstep());

		// Submit a task
		TestTaskPacket testTask = new TestTaskPacket("dsfsdf", 2342, (byte) -23);
		client2.scheduleTask(testTask);

		Thread.sleep(2L * NetworkConstants.Client.TIME_SYNC_SEND_INTERVALL);

		// The task must not have been submitted to the clients yet, because the lockstep is blocked.
		assertEquals(0, clock1.popBufferedTasks().size());
		assertEquals(0, clock2.popBufferedTasks().size());

		// Now let one clock continue one lockstep period.
		clock1.setTime(NetworkConstants.Client.LOCKSTEP_PERIOD + NetworkConstants.Client.TIME_SYNC_TOLERATED_DIFFERENCE + 10);

		Thread.sleep(NetworkConstants.Client.TIME_SYNC_SEND_INTERVALL + 40L);

		List<TaskPacket> packets1 = clock1.popBufferedTasks();
		assertEquals(1, packets1.size());
		assertEquals(testTask, packets1.get(0));
		List<TaskPacket> packets2 = clock2.popBufferedTasks();
		assertEquals(1, packets2.size());
		assertEquals(testTask, packets2.get(0));

		Thread.sleep(2 * NetworkConstants.Client.LOCKSTEP_PERIOD); // Wait two more lockstep periods and check the run away protection again
		assertEquals(NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS + 1, clock1.getAllowedLockstep());
		assertEquals(NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS + 1, clock2.getAllowedLockstep());
	}

	@Test
	public void testStartMatchWithUnreadyPlayers() throws IllegalStateException, InterruptedException {
		logIn(client2, "id2", "player2");

		openMatch("id1", "player1", client1); // open match and join client2
		client2.joinMatch(client1.getMatchInfo().getId(), null, null, null);

		BufferingPacketReceiver<RejectPacket> rejectReceiver2 = new BufferingPacketReceiver<>();
		client2.registerRejectReceiver(rejectReceiver2);

		Thread.sleep(50L);
		assertEquals(0, rejectReceiver2.popBufferedPackets().size());

		client2.startMatch(); // try to start match with unready player2 => match must not start
		Thread.sleep(50L);

		assertSingleRejectPacket(rejectReceiver2, ENetworkKey.REQUEST_START_MATCH, ENetworkMessage.NOT_ALL_PLAYERS_READY);

		client2.setReadyState(true); // set player2 ready and player1 unready => match must not start
		Thread.sleep(20L);
		client1.setReadyState(false);
		Thread.sleep(20L);
		client2.startMatch();
		Thread.sleep(50L);

		assertSingleRejectPacket(rejectReceiver2, ENetworkKey.REQUEST_START_MATCH, ENetworkMessage.NOT_ALL_PLAYERS_READY);

		client1.setReadyState(true); // set player1 ready => must must start
		Thread.sleep(20L);
		client2.startMatch();
		Thread.sleep(50L);

		assertEquals(EPlayerState.IN_RUNNING_MATCH, client1.getState());
		assertEquals(EPlayerState.IN_RUNNING_MATCH, client2.getState());
	}

	private void assertSingleRejectPacket(BufferingPacketReceiver<RejectPacket> rejectReceiver, ENetworkKey expectedRejectedKey,
			ENetworkMessage expectedMessage) {
		List<RejectPacket> rejectPackets = rejectReceiver.popBufferedPackets();
		assertEquals(1, rejectPackets.size());
		assertEquals(expectedRejectedKey, rejectPackets.get(0).getRejectedKey());
		assertEquals(expectedMessage, rejectPackets.get(0).getErrorMessageId());
	}
}
