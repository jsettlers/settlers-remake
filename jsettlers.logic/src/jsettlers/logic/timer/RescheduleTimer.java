package jsettlers.logic.timer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import jsettlers.common.map.MapLoadException;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.network.client.interfaces.IGameClock;
import jsettlers.network.synchronic.timer.INetworkTimerable;

public final class RescheduleTimer implements INetworkTimerable, Serializable {
	private static final long serialVersionUID = -1962430988827211391L;

	private static final short FUTURE_TIME = 11000;
	private static final short TIME_SLICE = 25; // ms
	private static final short TIME_SLOTS = FUTURE_TIME / TIME_SLICE;

	private static RescheduleTimer uniIns;

	@SuppressWarnings("unchecked")
	private final ArrayList<IScheduledTimerable> timerables[] = new ArrayList[TIME_SLOTS];
	private int currTimeSlot = 0;

	protected RescheduleTimer() {
		for (int i = 0; i < TIME_SLOTS; i++) {
			timerables[i] = new ArrayList<IScheduledTimerable>();
		}
	}

	public static void stop() {
		if (uniIns != null) {
			MatchConstants.clock.remove(uniIns);
			uniIns = null;
		}
	}

	/**
	 * Schedules the given {@link IScheduledTimerable} in max delay milliseconds.
	 * 
	 * @param t
	 * @param delay
	 */
	public static void add(IScheduledTimerable t, int delay) {
		get().add_(t, delay);
	}

	private void add_(IScheduledTimerable t, int delay) {
		if (delay <= 0) {
			return; // don't schedule if requested delay is negative or zero
		}

		int delaySlots = delay / TIME_SLICE;
		delaySlots = delaySlots > 0 ? delaySlots : 1; // ensure at least one slot delay

		assert delaySlots < TIME_SLOTS : "SCHEDULED TO FAR IN THE FUTURE! " + delay + "ms";

		timerables[(currTimeSlot + delaySlots) % TIME_SLOTS].add(t);
	}

	private static synchronized RescheduleTimer get() {
		if (uniIns == null) {
			uniIns = new RescheduleTimer();
		}
		return uniIns;
	}

	@Override
	public void timerEvent() {
		ArrayList<IScheduledTimerable> queue = timerables[currTimeSlot];

		for (IScheduledTimerable curr : queue) {
			try {
				int delay = curr.timerEvent();
				add_(curr, delay);
			} catch (Throwable t) {
				System.err.println("RescheduleTimer catched: ");
				t.printStackTrace();
				try {
					curr.kill();
				} catch (Throwable t2) {
					System.err.println("RescheduleTimer had trouble killing bad timerable!");
					t2.printStackTrace();
				}
			}
		}

		queue.clear();
		currTimeSlot = (currTimeSlot + 1) % TIME_SLOTS;
	}

	public static void loadFrom(ObjectInputStream ois) throws MapLoadException {
		try {
			stop();
			uniIns = (RescheduleTimer) ois.readObject();
		} catch (Throwable t) {
			throw new MapLoadException(t);
		}
	}

	public static void saveTo(ObjectOutputStream oos) throws IOException {
		oos.writeObject(uniIns);
		oos.flush();
	}

	public static void schedule(IGameClock gameClock) {
		gameClock.schedule(get(), TIME_SLICE);
	}
}
