package networklib.client.interfaces;

import networklib.client.exceptions.InvalidStateException;
import networklib.client.receiver.IPacketReceiver;
import networklib.common.packets.ArrayOfMatchInfosPacket;
import networklib.common.packets.ChatMessagePacket;
import networklib.common.packets.MapInfoPacket;
import networklib.common.packets.MatchInfoPacket;
import networklib.common.packets.MatchInfoUpdatePacket;
import networklib.common.packets.MatchStartPacket;
import networklib.common.packets.PlayerInfoPacket;
import networklib.infrastructure.channel.reject.RejectPacket;
import networklib.server.game.EPlayerState;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface INetworkClient extends ITaskScheduler {

	public abstract void logIn(String id, String name, IPacketReceiver<ArrayOfMatchInfosPacket> listener) throws InvalidStateException;

	public abstract MatchInfoPacket getMatchInfo();

	public abstract PlayerInfoPacket getPlayerInfo();

	public abstract void close();

	public abstract EPlayerState getState();

	public abstract void registerRejectReceiver(IPacketReceiver<RejectPacket> rejectListener);

	public abstract void sendChatMessage(String message) throws InvalidStateException;

	public abstract void setReadyState(boolean ready) throws InvalidStateException;

	public abstract void requestStartMatch() throws InvalidStateException;

	public abstract void requestLeaveMatch() throws InvalidStateException;

	public abstract void requestJoinMatch(MatchInfoPacket match, IPacketReceiver<MatchStartPacket> matchStartedListener,
			IPacketReceiver<MatchInfoUpdatePacket> matchInfoUpdatedListener, IPacketReceiver<ChatMessagePacket> chatMessageReceiver)
			throws InvalidStateException;

	public abstract void requestOpenNewMatch(String matchName, int maxPlayers, MapInfoPacket mapInfo, long randomSeed,
			IPacketReceiver<MatchStartPacket> matchStartedListener, IPacketReceiver<MatchInfoUpdatePacket> matchInfoUpdatedListener,
			IPacketReceiver<ChatMessagePacket> chatMessageReceiver)
			throws InvalidStateException;

}
