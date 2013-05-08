package networklib.client;

import static org.junit.Assert.assertEquals;

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
import networklib.server.ServerManager;
import networklib.server.db.inMemory.InMemoryDB;
import networklib.server.game.EPlayerState;
import networklib.server.game.Player;
import networklib.server.packets.ArrayOfMatchInfosPacket;
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

	private InMemoryDB db = new InMemoryDB();
	private ServerManager manager = new ServerManager(db);

	private AsyncChannel clientChannel;
	private Channel serverChannel;
	private NetworkClient client;

	@Before
	public void setUp() throws IOException {
		TestUtils util = new TestUtils();

		AsyncChannel[] channels = util.setUpAsyncLoopbackChannels();
		clientChannel = channels[0];
		serverChannel = channels[1];

		client = new NetworkClient(clientChannel, null);
		manager.identifyNewChannel(serverChannel);
	}

	@After
	public void tearDown() {
		client.close();
		serverChannel.close();
	}

	@Test
	public void testConnection() throws InterruptedException {
		TestPacketListener listener = new TestPacketListener(NetworkConstants.Keys.REQUEST_MATCHES);
		clientChannel.registerListener(listener);

		TestPacket testPacket = new TestPacket("sdlfjsh", 2324);
		serverChannel.sendPacket(NetworkConstants.Keys.REQUEST_MATCHES, testPacket);

		Thread.sleep(10);

		assertEquals(1, listener.packets.size());
		assertEquals(testPacket, listener.packets.get(0));
	}

	@Test
	public void testLogIn() throws InvalidStateException, InterruptedException {
		final String playerId = "id-32e24ewrfs";
		final String playerName = "Name)2020j3j";

		client.logIn(playerId, playerName);

		Thread.sleep(20);

		assertEquals(EPlayerState.LOGGED_IN, client.getState());

		assertEquals(1, db.getNumberOfPlayers());
		Player p = db.getPlayer(playerId);
		assertEquals(playerId, p.getId());
		assertEquals(playerName, p.getPlayerInfo().getName());
	}

	@Test(expected = InvalidStateException.class)
	public void testRequestMatchesInStateUnconnected() throws InvalidStateException {
		client.requestMatches(null);
	}

	@Test(expected = InvalidStateException.class)
	public void testRequestPlayersRunningMatchesInStateUnconnected() throws InvalidStateException {
		client.requestPlayersRunningMatches(null);
	}

	@Test(expected = InvalidStateException.class)
	public void testRequestOpenNewMatchInStateUnconnected() throws InvalidStateException {
		client.requestOpenNewMatch(null, (byte) 0, null, null, null, null);
	}

	@Test
	public void testLogInAndClose() throws InvalidStateException, InterruptedException {
		testLogIn();

		client.close();

		Thread.sleep(10);
		assertEquals(0, db.getNumberOfPlayers());
		assertEquals(EPlayerState.DISCONNECTED, client.getState());
	}

	@Test
	public void testCloseFromServerSide() throws InvalidStateException, InterruptedException {
		testLogIn();

		serverChannel.close();

		Thread.sleep(10);
		assertEquals(0, db.getNumberOfPlayers());
		assertEquals(EPlayerState.DISCONNECTED, client.getState());
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
		BufferingPacketReceiver<ArrayOfMatchInfosPacket> matchesListener = new BufferingPacketReceiver<ArrayOfMatchInfosPacket>();
		assertEquals(0, matchesListener.popBufferedPackets().size());

		client.requestMatches(matchesListener);

		Thread.sleep(100);

		List<ArrayOfMatchInfosPacket> arrayOfMatches = matchesListener.popBufferedPackets();
		assertEquals(1, arrayOfMatches.size()); // check that we got one result for the request
		assertEquals(0, arrayOfMatches.get(0).getMatches().length); // currently no matches should be in the result, because non should be open

		BufferingPacketReceiver<MatchInfoUpdatePacket> matchUpdateListener = new BufferingPacketReceiver<MatchInfoUpdatePacket>();
		final String matchName = "TestMatch";
		final byte maxPlayers = (byte) 5;
		final MapInfoPacket mapInfo = new MapInfoPacket("mapid92329", "mapName", "authorId", "authorName");
		client.requestOpenNewMatch(matchName, maxPlayers, mapInfo, null, matchUpdateListener, null);

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
		client.requestPlayersRunningMatches(listener);

		Thread.sleep(100);
		List<ArrayOfMatchInfosPacket> packets = listener.popBufferedPackets();
		assertEquals(1, packets.size());
		assertEquals(0, packets.get(0).getMatches().length);

		testOpenMatch(); // log in and open a new match.

		client.requestStartMatch();
		client.requestLeaveMatch();

		Thread.sleep(100);

		client.requestPlayersRunningMatches(listener);

		Thread.sleep(100);
		packets = listener.popBufferedPackets();
		assertEquals(1, packets.size());
		assertEquals(1, packets.get(0).getMatches().length);
	}
}
