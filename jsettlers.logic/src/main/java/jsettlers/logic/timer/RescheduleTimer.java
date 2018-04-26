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
package jsettlers.logic.timer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.network.client.interfaces.IGameClock;
import jsettlers.network.synchronic.timer.INetworkTimerable;

public final class RescheduleTimer implements INetworkTimerable, Serializable {
	private static final long serialVersionUID = -1962430988827211391L;

	private static final int FUTURE_TIME = 32000;
	private static final short TIME_SLICE = 25; // ms
	private static final int TIME_SLOTS = FUTURE_TIME / TIME_SLICE;

	private static RescheduleTimer uniIns;

	@SuppressWarnings("unchecked")
	private final ArrayList<IScheduledTimerable> timerables[] = new ArrayList[TIME_SLOTS];
	private int currTimeSlot = 0;

	protected RescheduleTimer() {
		for (int i = 0; i < TIME_SLOTS; i++) {
			timerables[i] = new ArrayList<>();
		}
	}

	public static synchronized void stopAndClear() {
		if (uniIns != null) {
			if (MatchConstants.clock() != null) {
				MatchConstants.clock().remove(uniIns);
			}
			uniIns = null;
			try {
				Thread.sleep(100L); // stopping takes some time
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Schedules the given {@link IScheduledTimerable} in max delay milliseconds.
	 * 
	 * @param t
	 * @param delay
	 */
	public static void add(IScheduledTimerable t, int delay) {
		get().addTimerable(t, delay);
	}

	private void addTimerable(IScheduledTimerable t, int delay) {
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
			if (uniIns != this) { // fast stop when stopAndClear() is called.
				return;
			}

			try {
				int delay = curr.timerEvent();
				addTimerable(curr, delay);
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
			stopAndClear();
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
