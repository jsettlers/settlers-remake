/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.network.server.match.lockstep;

import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

import jsettlers.network.NetworkConstants;
import jsettlers.network.infrastructure.channel.ping.IPingUpdateListener;
import jsettlers.network.infrastructure.log.Logger;
import jsettlers.network.infrastructure.utils.MaximumSlotBuffer;
import jsettlers.network.server.match.Match;
import jsettlers.network.server.packets.ServersideSyncTasksPacket;
import jsettlers.network.server.packets.ServersideTaskPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TaskSendingTimerTask extends TimerTask {
	private final Logger logger;
	private final TaskCollectingListener taskCollectingListener;
	private final Match match;

	private int lockstepCounter = 0;
	private int currentLockstepMax = NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS;

	private int minimumLeadTimeMs = NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS * NetworkConstants.Client.LOCKSTEP_PERIOD;
	private int leadSteps = minimumLeadTimeMs / NetworkConstants.Client.LOCKSTEP_PERIOD;

	public TaskSendingTimerTask(Logger logger, TaskCollectingListener taskCollectingListener, Match match) {
		this.logger = logger;
		this.taskCollectingListener = taskCollectingListener;
		this.match = match;
	}

	@Override
	public void run() {
		if (lockstepCounter > currentLockstepMax) {
			return;
		}

		List<ServersideTaskPacket> tasksList = taskCollectingListener.getAndResetTasks();
		ServersideSyncTasksPacket syncTasksPacket = new ServersideSyncTasksPacket(lockstepCounter++, tasksList);
		match.broadcastMessage(NetworkConstants.ENetworkKey.SYNCHRONOUS_TASK, syncTasksPacket);
	}

	public void receivedLockstepAcknowledge(int acknowledgedLockstep) {
		currentLockstepMax = Math.max(currentLockstepMax, acknowledgedLockstep + leadSteps);
		// logger.info("lead steps: " + leadSteps);
	}

	final void pingUpdated(int rtt, int jitter) {
		if (rtt < 0 || rtt > 10000 || jitter > 5000) {
			return; // this is an exceptional high rtt, we can not adapt to this
		}

		int newLeadTime = (int) (rtt / 2 * 1.1f + jitter * 2f + NetworkConstants.Client.LOCKSTEP_PERIOD * 1.5f);
		if (newLeadTime > minimumLeadTimeMs) {
			minimumLeadTimeMs = newLeadTime;
		} else {
			minimumLeadTimeMs -= (minimumLeadTimeMs - newLeadTime) / 4;
		}

		leadSteps = (int) Math.ceil(((float) minimumLeadTimeMs) / NetworkConstants.Client.LOCKSTEP_PERIOD);

		if (rtt > NetworkConstants.RTT_LOGGING_THRESHOLD || jitter > NetworkConstants.JITTER_LOGGING_THRESHOLD) {
			logger.info(String.format(Locale.ENGLISH, "rtt/2: %5d   jitter: %d   min lead time: %4d   lead steps: %2d",					rtt / 2, jitter, minimumLeadTimeMs, leadSteps));
		}
	}

	private MaximumSlotBuffer rttMaximum = new MaximumSlotBuffer(0);
	private MaximumSlotBuffer jitterMaximum = new MaximumSlotBuffer(0);

	public IPingUpdateListener getPingListener(final int index) {
		if (rttMaximum.getLength() <= index) {
			rttMaximum = new MaximumSlotBuffer(index + 1);
			jitterMaximum = new MaximumSlotBuffer(index + 1);
		}

		return rtt -> {
			rttMaximum.insert(index, rtt.getRtt());
			jitterMaximum.insert(index, rtt.getAveragedJitter());

			TaskSendingTimerTask.this.pingUpdated(rttMaximum.getMax(), jitterMaximum.getMax());
		};
	}
}
