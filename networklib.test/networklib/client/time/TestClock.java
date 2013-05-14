package networklib.client.time;

import java.util.LinkedList;

/**
 * Test mock for {@link ISynchronizableClock} interface used to test the time synchronization code.
 * 
 * @author Andreas Eberle
 * 
 */
public class TestClock implements ISynchronizableClock {

	private LinkedList<Integer> adjustmentEvents = new LinkedList<Integer>();
	private int time;

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

}
