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
package jsettlers.network.client.task;

import java.util.LinkedList;
import java.util.List;

import jsettlers.network.client.task.ISyncTasksPacketScheduler;
import jsettlers.network.client.task.packets.SyncTasksPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class SyncTasksPacketSchedulerMock implements ISyncTasksPacketScheduler {
	private LinkedList<SyncTasksPacket> buffer;
	private int unlockedLockstep = 0;

	public SyncTasksPacketSchedulerMock() {
		this.buffer = new LinkedList<>();
	}

	/**
	 * 
	 * @return Returns all {@link SyncTasksPacket}s that were received and had at least one task packaged in them.
	 */
	public List<SyncTasksPacket> popBufferedPackets() {
		List<SyncTasksPacket> temp = buffer;
		buffer = new LinkedList<>();
		return temp;
	}

	@Override
	public void scheduleSyncTasksPacket(SyncTasksPacket tasksPacket) {
		unlockedLockstep = tasksPacket.getLockstepNumber();

		if (!tasksPacket.getTasks().isEmpty()) {
			buffer.add(tasksPacket);
		}
	}

	public int getUnlockedLockstepNumber() {
		return unlockedLockstep;
	}
}