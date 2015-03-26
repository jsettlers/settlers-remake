package jsettlers.network.client.time;

import java.io.IOException;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.Client;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.common.packets.TimeSyncPacket;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;
import jsettlers.network.infrastructure.channel.ping.IRoundTripTimeSupplier;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TimeSynchronizationListener extends PacketChannelListener<TimeSyncPacket> {

	private final IRoundTripTimeSupplier rttSupplier;
	private final ISynchronizableClock clock;

	public TimeSynchronizationListener(IRoundTripTimeSupplier rttSupplier, ISynchronizableClock clock) {
		super(NetworkConstants.ENetworkKey.TIME_SYNC, new GenericDeserializer<TimeSyncPacket>(TimeSyncPacket.class));
		this.rttSupplier = rttSupplier;
		this.clock = clock;
	}

	@Override
	protected void receivePacket(ENetworkKey key, TimeSyncPacket packet) throws IOException {
		int expectedRemoteTime = packet.getTime() + rttSupplier.getRoundTripTime().getRtt() / 2;
		int localTime = clock.getTime();

		int deltaTime = localTime - expectedRemoteTime;

		if (deltaTime > Client.TIME_SYNC_TOLERATED_DIFFERENCE) {
			clock.pauseClockFor((int) (deltaTime * Client.TIME_SYNC_APPROACH_FACTOR));
		}
	}

}
