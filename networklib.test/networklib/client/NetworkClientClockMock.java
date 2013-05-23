package networklib.client;

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
	public void stopClockFor(int timeDelta) {
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
		// TODO Auto-generated method stub

	}

	@Override
	public void multiplyGameSpeed(float factor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameSpeed(float speedFactor) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPausing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void invertPausing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPausing(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fastForward() {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(INetworkTimerable timerable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedule(INetworkTimerable timerable, short delay) {
		// TODO Auto-generated method stub

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

}
