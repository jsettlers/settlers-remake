package jsettlers.network.client.time;

import java.util.TimerTask;

import jsettlers.network.NetworkConstants;
import jsettlers.network.common.packets.TimeSyncPacket;
import jsettlers.network.infrastructure.channel.AsyncChannel;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TimeSyncSenderTimerTask extends TimerTask {

	private final AsyncChannel channel;
	private final ISynchronizableClock clock;

	public TimeSyncSenderTimerTask(AsyncChannel channel, ISynchronizableClock clock) {
		this.channel = channel;
		this.clock = clock;
	}

	@Override
	public void run() {
		int localTime = clock.getTime();
		int expectedTimeAtServer = localTime + channel.getRoundTripTime().getRtt() / 2;

		channel.sendPacketAsync(NetworkConstants.ENetworkKey.TIME_SYNC, new TimeSyncPacket(expectedTimeAtServer));
	}

}
