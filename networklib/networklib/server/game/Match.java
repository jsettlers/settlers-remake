package networklib.server.game;

import java.util.LinkedList;
import java.util.Timer;
import java.util.UUID;

import networklib.NetworkConstants;
import networklib.NetworkConstants.ENetworkKey;
import networklib.NetworkConstants.ENetworkMessage;
import networklib.common.packets.MapInfoPacket;
import networklib.common.packets.MatchInfoPacket;
import networklib.common.packets.MatchInfoUpdatePacket;
import networklib.common.packets.MatchStartPacket;
import networklib.common.packets.PlayerInfoPacket;
import networklib.common.packets.TimeSyncPacket;
import networklib.infrastructure.channel.packet.Packet;
import networklib.server.exceptions.NotAllPlayersReadyException;
import networklib.server.lockstep.TaskCollectingListener;
import networklib.server.lockstep.TaskSendingTimerTask;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class Match {

	private final String id = UUID.randomUUID().toString();
	private final LinkedList<Player> players = new LinkedList<Player>();
	private final LinkedList<Player> leftPlayers = new LinkedList<Player>();
	private final int maxPlayers;
	private final MapInfoPacket map;
	private final String name;
	private final long randomSeed;

	private EMatchState state = EMatchState.OPENED;
	private TaskCollectingListener taskCollectingListener;
	private TaskSendingTimerTask taskSendingTimerTask;

	public Match(String name, int maxPlayers, MapInfoPacket map, long randomSeed) {
		this.maxPlayers = maxPlayers;
		this.map = map;
		this.name = name;
		this.randomSeed = randomSeed;
	}

	public EMatchState getState() {
		return state;
	}

	public boolean canJoin() {
		return state == EMatchState.OPENED && players.size() < maxPlayers;
	}

	public String getId() {
		return id;
	}

	public MapInfoPacket getMap() {
		return map;
	}

	public String getName() {
		return name;
	}

	public PlayerInfoPacket[] getPlayerInfos() {
		synchronized (players) {
			PlayerInfoPacket[] result = new PlayerInfoPacket[players.size()];
			int i = 0;
			for (Player curr : players) {
				result[i] = curr.getPlayerInfo();
				i++;
			}

			return result;
		}
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public long getRandomSeed() {
		return randomSeed;
	}

	public boolean isRunning() {
		return state == EMatchState.RUNNING;
	}

	public boolean hasPlayer(Player player) {
		return getPlayer(player.getId()) != null;
	}

	public boolean hasLeftPlayer(String playerId) {
		synchronized (leftPlayers) {
			for (Player curr : leftPlayers) {
				if (curr.getId().equals(playerId)) {
					return true;
				}
			}

			return false;
		}
	}

	public Player getPlayer(String playerId) {
		synchronized (players) {
			for (Player curr : players) {
				if (curr.getId().equals(playerId)) {
					return curr;
				}
			}

			return null;
		}
	}

	public void sendMatchInfoUpdate(ENetworkMessage updateReason, String idOfChangedPlayer) {
		broadcastMessage(NetworkConstants.ENetworkKey.MATCH_INFO_UPDATE, generateMatchInfoUpdate(updateReason, idOfChangedPlayer));
	}

	private MatchInfoUpdatePacket generateMatchInfoUpdate(ENetworkMessage updateReason, String idOfChangedPlayer) {
		return new MatchInfoUpdatePacket(updateReason, idOfChangedPlayer, new MatchInfoPacket(this));
	}

	public void broadcastMessage(ENetworkKey key, Packet packet) {
		sendMessage(null, key, packet);
	}

	/**
	 * 
	 * @param sendingPlayer
	 *            The sending player will not receive the message. If the message shall be send to all players in the match, <code>null</code> can be
	 *            used as value for this.
	 * @param key
	 * @param packet
	 */
	public void sendMessage(Player sendingPlayer, ENetworkKey key, Packet packet) {
		synchronized (players) {
			for (Player curr : players) {
				if (sendingPlayer == null || !curr.getId().equals(sendingPlayer.getId())) {
					curr.sendPacket(key, packet);
				}
			}
		}
	}

	public void join(Player player) {
		synchronized (players) {
			players.add(player);

			sendMatchInfoUpdate(NetworkConstants.ENetworkMessage.PLAYER_JOINED, player.getId());

			if (state == EMatchState.RUNNING) {
				sendMatchStartPacketToPlayer(player);
			}
		}
	}

	public void playerLeft(Player player) {
		synchronized (players) {
			players.remove(player);

			sendMatchInfoUpdate(NetworkConstants.ENetworkMessage.PLAYER_LEFT, player.getId());
			player.sendPacket(NetworkConstants.ENetworkKey.MATCH_INFO_UPDATE,
					generateMatchInfoUpdate(NetworkConstants.ENetworkMessage.PLAYER_LEFT, player.getId()));

			if (isRunning()) {
				synchronized (leftPlayers) {
					leftPlayers.add(player);
				}
			}
		}
	}

	public synchronized void startMatch(Timer timer) throws NotAllPlayersReadyException {
		if (state == EMatchState.RUNNING || state == EMatchState.FINISHED) {
			return; // match already started
		}

		synchronized (players) {
			for (Player player : players) {
				if (!player.getPlayerInfo().isReady()) {
					throw new NotAllPlayersReadyException(); // FIXME @Andreas Eberle this is only temporarily commented out!
				}
			}
		}

		state = EMatchState.RUNNING;

		this.taskCollectingListener = new TaskCollectingListener();
		this.taskSendingTimerTask = new TaskSendingTimerTask(taskCollectingListener, this);
		timer.schedule(taskSendingTimerTask, NetworkConstants.Client.LOCKSTEP_PERIOD, NetworkConstants.Client.LOCKSTEP_PERIOD);

		synchronized (players) {
			for (Player player : players) {
				sendMatchStartPacketToPlayer(player);
			}
		}
	}

	private void sendMatchStartPacketToPlayer(Player player) {
		player.matchStarted(taskCollectingListener);
		player.sendPacket(NetworkConstants.ENetworkKey.MATCH_STARTED, new MatchStartPacket(new MatchInfoPacket(this), 0L));
	}

	public void distributeTimeSync(Player player, TimeSyncPacket packet) {
		sendMessage(player, NetworkConstants.ENetworkKey.TIME_SYNC, packet);
		taskSendingTimerTask.receivedLockstepAcknowledge(packet.getTime() / NetworkConstants.Client.LOCKSTEP_PERIOD);
	}

}
