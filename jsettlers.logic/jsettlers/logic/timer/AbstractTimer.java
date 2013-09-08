package jsettlers.logic.timer;

import java.util.LinkedList;

import jsettlers.logic.constants.MatchConstants;
import networklib.synchronic.timer.INetworkTimerable;

public abstract class AbstractTimer<T extends ITimerable> implements INetworkTimerable {
	private static final LinkedList<AbstractTimer<? extends ITimerable>> timers = new LinkedList<AbstractTimer<? extends ITimerable>>();

	protected final LinkedList<T> timerables = new LinkedList<T>();
	private final LinkedList<T> newToList = new LinkedList<T>();
	private final LinkedList<T> removeList = new LinkedList<T>();

	private final String name;

	protected AbstractTimer(short interruptDelay) {
		this.name = this.getClass().getName();
		MatchConstants.clock.schedule(this, interruptDelay);

		timers.add(this);
	}

	@Override
	public void timerEvent() {
		try {
			synchronized (newToList) {
				timerables.addAll(newToList);
				newToList.clear();
			}
			synchronized (removeList) {
				timerables.removeAll(removeList);
				removeList.clear();
			}

			synchronized (timerables) {
				executeAction();
			}
		} catch (Throwable e) {
			System.err.println("AbstractTimer catched for " + name + ": ");
			e.printStackTrace();
		}

		// ticks++; // this code can be used to output the number of ticks per second of each timer.
		// System.out.println(name + ":\t\t" + (1000 * ticks / ((float) (System.currentTimeMillis() - start))));
		//
		// if (ticks >= 30) {
		// ticks = 0;
		// start = System.currentTimeMillis();
		// }
	}

	// private int ticks = 0;
	// private long start = System.currentTimeMillis();

	public void cancel() {
		timers.remove(this);
	}

	private final void executeAction() {
		for (ITimerable s : timerables) {
			try {
				s.timerEvent();
			} catch (Throwable t) {
				System.out.println("AbstractTimer catched for " + name + ": ");
				t.printStackTrace();
				s.kill();
			}
		}
	}

	protected void add_(T s) {
		synchronized (newToList) {
			newToList.add(s);
		}
	}

	protected void remove_(T s) {
		synchronized (removeList) {
			removeList.add(s);
		}
	}

}
