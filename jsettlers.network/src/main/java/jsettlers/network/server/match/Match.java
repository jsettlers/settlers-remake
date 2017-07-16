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
package jsettlers.network.server.match;

import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.UUID;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.NetworkConstants.ENetworkMessage;
import jsettlers.network.common.packets.MapInfoPacket;
import jsettlers.network.common.packets.MatchInfoPacket;
import jsettlers.network.common.packets.MatchInfoUpdatePacket;
import jsettlers.network.common.packets.MatchStartPacket;
import jsettlers.network.common.packets.PlayerInfoPacket;
import jsettlers.network.common.packets.TimeSyncPacket;
import jsettlers.network.infrastructure.channel.packet.Packet;
import jsettlers.network.infrastructure.log.Logger;
import jsettlers.network.infrastructure.log.LoggerManager;
import jsettlers.network.server.exceptions.NotAllPlayersReadyException;
import jsettlers.network.server.match.lockstep.TaskCollectingListener;
import jsettlers.network.server.match.lockstep.TaskSendingTimerTask;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class Match {

	private final Logger logger;
	private final Date date;
	private final String id;
	private final LinkedList<Player> players;
	private final LinkedList<Player> leftPlayers;
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
		this.id = UUID.randomUUID().toString();
		this.players = new LinkedList<>();
		this.leftPlayers = new LinkedList<>();
		this.logger = LoggerManager.getMatchLogger(id, name);
		this.date = new Date();
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

	public void sendMatchInfoUpdate(ENetworkMessage updateReason, PlayerInfoPacket updatedPlayer) {
		broadcastMessage(NetworkConstants.ENetworkKey.MATCH_INFO_UPDATE, generateMatchInfoUpdate(updateReason, updatedPlayer));
	}

	private MatchInfoUpdatePacket generateMatchInfoUpdate(ENetworkMessage updateReason, PlayerInfoPacket updatedPlayer) {
		return new MatchInfoUpdatePacket(updateReason, updatedPlayer, new MatchInfoPacket(this));
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

			sendMatchInfoUpdate(NetworkConstants.ENetworkMessage.PLAYER_JOINED, player.getPlayerInfo());

			if (state == EMatchState.RUNNING) {
				sendMatchStartPacketToPlayer(player);
			}
		}
	}

	public void playerLeft(Player player) {
		synchronized (players) {
			players.remove(player);

			sendMatchInfoUpdate(NetworkConstants.ENetworkMessage.PLAYER_LEFT, player.getPlayerInfo());
			player.sendPacket(NetworkConstants.ENetworkKey.MATCH_INFO_UPDATE,
					generateMatchInfoUpdate(NetworkConstants.ENetworkMessage.PLAYER_LEFT, player.getPlayerInfo()));

			if (isRunning()) {
				synchronized (leftPlayers) {
					leftPlayers.add(player);
				}
			}

			if (players.isEmpty()) {
				shutdownMatch();
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
					throw new NotAllPlayersReadyException();
				}
			}
		}

		state = EMatchState.RUNNING;

		this.taskCollectingListener = new TaskCollectingListener();
		this.taskSendingTimerTask = new TaskSendingTimerTask(logger, taskCollectingListener, this);
		timer.schedule(taskSendingTimerTask, NetworkConstants.Client.LOCKSTEP_PERIOD, NetworkConstants.Client.LOCKSTEP_PERIOD / 2 - 2);

		synchronized (players) {
			int i = 0;
			for (Player player : players) {
				sendMatchStartPacketToPlayer(player);

				// needed so that the sending task can adapt to the ping
				player.getChannel().setPingUpdateListener(taskSendingTimerTask.getPingListener(i));
				i++;
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

	public Logger getMatchLogger() {
		return logger;
	}

	private void shutdownMatch() {
		if (state == EMatchState.RUNNING) {
			taskSendingTimerTask.cancel();
			taskSendingTimerTask = null;

			synchronized (players) {
				if (players.size() > 0) {
					logger.warn("Closing match with active players...");

					for (Player player : players) {
						player.getChannel().close();
					}
				}
			}
			taskCollectingListener = null;
		}

		state = EMatchState.FINISHED;
	}

	@Override
	public String toString() {
		return state + ": name: '" + name + "' opened: '" + date + "' numberOfPlayers: " + players.size() + " map: '" + map.getName() + "' ('"
				+ map.getId() + "')";
	}
}
