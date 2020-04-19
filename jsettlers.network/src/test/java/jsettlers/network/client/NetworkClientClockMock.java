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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import jsettlers.network.client.task.packets.SyncTasksPacket;
import jsettlers.network.client.task.packets.TaskPacket;
import jsettlers.network.synchronic.timer.INetworkTimerable;
import jsettlers.network.synchronic.timer.ITaskExecutor;

/**
 * This class is a mock of the {@link INetworkClientClock} interface.
 * 
 * @author Andreas Eberle
 * 
 */
public class NetworkClientClockMock implements INetworkClientClock {

	private LinkedList<Integer> adjustmentEvents = new LinkedList<>();
	private LinkedList<TaskPacket> bufferedTasks = new LinkedList<>();
	private int time;
	private int maxAllowedLockstep;

	public NetworkClientClockMock() {
		this(0);
	}

	public NetworkClientClockMock(int time) {
		this.time = time;
	}

	@Override
	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public int getTime() {
		return time;
	}

	@Override
	public void pauseClockFor(int timeDelta) {
		time -= timeDelta;
		adjustmentEvents.add(timeDelta);
	}

	public LinkedList<Integer> popAdjustmentEvents() {
		LinkedList<Integer> temp = adjustmentEvents;
		adjustmentEvents = new LinkedList<>();
		return temp;
	}

	@Override
	public void setTaskExecutor(ITaskExecutor taskExecutor) {
		throw new UnsupportedOperationException("not mocked");
	}

	@Override
	public void multiplyGameSpeed(float factor) {
		throw new UnsupportedOperationException("not mocked");
	}

	@Override
	public void setGameSpeed(float speedFactor) {
		throw new UnsupportedOperationException("not mocked");
	}

	@Override
	public float getGameSpeed() {
		throw new UnsupportedOperationException("not mocked");
	}

	@Override
	public boolean isPausing() {
		throw new UnsupportedOperationException("not mocked");
	}

	@Override
	public void invertPausing() {
		throw new UnsupportedOperationException("not mocked");
	}

	@Override
	public void setPausing(boolean b) {
		throw new UnsupportedOperationException("not mocked");
	}

	@Override
	public void fastForward() {
		throw new UnsupportedOperationException("not mocked");
	}

	@Override
	public void remove(INetworkTimerable timerable) {
		throw new UnsupportedOperationException("not mocked");
	}

	@Override
	public void schedule(INetworkTimerable timerable, short delay) {
		throw new UnsupportedOperationException("not mocked");
	}

	@Override
	public void scheduleSyncTasksPacket(SyncTasksPacket packet) {
		maxAllowedLockstep = Math.max(maxAllowedLockstep, packet.getLockstepNumber());
		bufferedTasks.addAll(packet.getTasks());
	}

	public int getAllowedLockstep() {
		return maxAllowedLockstep;
	}

	public LinkedList<TaskPacket> popBufferedTasks() {
		LinkedList<TaskPacket> temp = bufferedTasks;
		bufferedTasks = new LinkedList<>();
		return temp;
	}

	@Override
	public void startExecution() {
		throw new UnsupportedOperationException("not mocked");
	}

	@Override
	public void stopExecution() {
	}

	@Override
	public void setReplayLogStream(DataOutputStream replayFileStream) {
	}

	@Override
	public void loadReplayLogFromStream(DataInputStream dataInputStream) {
	}

	@Override
	public void saveRemainingTasks(DataOutputStream dos) throws IOException {
	}

	@Override
	public void fastForwardTo(int targetGameTime) {
		time = targetGameTime;
	}

}
