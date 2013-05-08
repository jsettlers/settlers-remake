package networklib.server.game;

import java.util.LinkedList;
import java.util.UUID;

import networklib.NetworkConstants;
import networklib.channel.packet.Packet;
import networklib.server.packets.MapInfoPacket;
import networklib.server.packets.MatchInfoPacket;
import networklib.server.packets.MatchInfoUpdatePacket;
import networklib.server.packets.MatchStartPacket;
import networklib.server.packets.PlayerInfoPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class Match {

	private final String id = UUID.randomUUID().toString();
	private final LinkedList<Player> players = new LinkedList<Player>();
	private final LinkedList<Player> leftPlayers = new LinkedList<Player>();
	private final byte maxPlayers;
	private final MapInfoPacket map;
	private final String name;

	private EMatchState state = EMatchState.OPENED;

	public Match(String name, byte maxPlayers, MapInfoPacket map) {
		this.maxPlayers = maxPlayers;
		this.map = map;
		this.name = name;
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

	public byte getMaxPlayers() {
		return maxPlayers;
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

	private void sendMatchInfoUpdate(int updateReason) {
		sendMessage(NetworkConstants.Keys.MATCH_INFO_UPDATE, generateMatchInfoUpdate(updateReason));
	}

	private MatchInfoUpdatePacket generateMatchInfoUpdate(int updateReason) {
		return new MatchInfoUpdatePacket(updateReason, new MatchInfoPacket(this));
	}

	private void sendMessage(int key, Packet packet) {
		synchronized (players) {
			for (Player curr : players) {
				curr.sendPacket(key, packet);
			}
		}
	}

	public void join(Player player) {
		synchronized (players) {
			players.add(player);

			sendMatchInfoUpdate(NetworkConstants.Messages.PLAYER_JOINED);
		}
	}

	public void playerLeft(Player player) {
		synchronized (players) {
			players.remove(player);

			sendMatchInfoUpdate(NetworkConstants.Messages.PLAYER_LEFT);
			player.sendPacket(NetworkConstants.Keys.MATCH_INFO_UPDATE, generateMatchInfoUpdate(NetworkConstants.Messages.PLAYER_LEFT));

			if (isRunning()) {
				synchronized (leftPlayers) {
					leftPlayers.add(player);
				}
			}
		}
	}

	public void startMatch() {
		state = EMatchState.RUNNING;

		for (Player player : players) {
			player.matchStarted();
		}

		sendMessage(NetworkConstants.Keys.MATCH_STARTED, new MatchStartPacket(new MatchInfoPacket(this), 0L));
	}

}
