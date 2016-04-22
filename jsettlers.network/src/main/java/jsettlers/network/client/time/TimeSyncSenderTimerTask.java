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
