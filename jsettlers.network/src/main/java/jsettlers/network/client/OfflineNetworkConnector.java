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

import java.util.Arrays;

import jsettlers.network.NetworkConstants;
import jsettlers.network.client.interfaces.IGameClock;
import jsettlers.network.client.interfaces.INetworkConnector;
import jsettlers.network.client.interfaces.ITaskScheduler;
import jsettlers.network.client.task.packets.SyncTasksPacket;
import jsettlers.network.client.task.packets.TaskPacket;
import jsettlers.network.synchronic.timer.NetworkTimer;

/**
 * This is a {@link ITaskScheduler} implementation that supports offline gameplay. It directly schedules the tasks in the {@link NetworkTimer}.
 * 
 * @author Andreas Eberle
 * 
 */
public class OfflineNetworkConnector implements ITaskScheduler, INetworkConnector {

	private final NetworkTimer networkTimer = new NetworkTimer(true);
	private boolean startFinished;

	@Override
	public void scheduleTask(TaskPacket task) {
		scheduleTaskAt(networkTimer.getTime() / NetworkConstants.Client.LOCKSTEP_PERIOD + 2, task);
	}

	/**
	 * Schedules the given task for execution in the given targetLockstep.
	 * 
	 * @param targetLockstep
	 *            Time the task should be scheduled in milliseconds.
	 * @param task
	 *            Task to be scheduled.
	 */
	public void scheduleTaskAt(int targetLockstep, TaskPacket task) {
		networkTimer.scheduleSyncTasksPacket(new SyncTasksPacket(targetLockstep, Arrays.asList(task)));
	}

	@Override
	public IGameClock getGameClock() {
		return networkTimer;
	}

	@Override
	public void shutdown() {
		networkTimer.stopExecution();
	}

	@Override
	public ITaskScheduler getTaskScheduler() {
		return this;
	}

	@Override
	public void setStartFinished(boolean startFinished) {
		this.startFinished = startFinished;
	}

	@Override
	public boolean haveAllPlayersStartFinished() {
		return startFinished;
	}

}
