package networklib.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.LinkedList;

import networklib.client.task.packets.SyncTasksPacket;
import networklib.client.task.packets.TaskPacket;
import networklib.synchronic.timer.INetworkTimerable;
import networklib.synchronic.timer.ITaskExecutor;

/**
 * This class is a mock of the {@link INetworkClientClock} interface.
 * 
 * @author Andreas Eberle
 * 
 */
public class NetworkClientClockMock implements INetworkClientClock {

	private LinkedList<Integer> adjustmentEvents = new LinkedList<Integer>();
	private LinkedList<TaskPacket> bufferedTasks = new LinkedList<TaskPacket>();
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
		adjustmentEvents = new LinkedList<Integer>();
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
		bufferedTasks = new LinkedList<TaskPacket>();
		return temp;
	}

	@Override
	public void startExecution() {
		throw new UnsupportedOperationException("not mocked");
	}

	@Override
	public void stopExecution() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReplayLogStream(DataOutputStream replayFileStream) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadReplayLogFromStream(DataInputStream dataInputStream) {
		// TODO Auto-generated method stub

	}

}
