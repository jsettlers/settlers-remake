package jsettlers.logic.timer;

import jsettlers.common.SerializableLinkedList;
import synchronic.timer.INetworkTimerable;
import synchronic.timer.NetworkTimer;

public abstract class AbstractTimer<T extends ITimerable> implements INetworkTimerable {
	private static final SerializableLinkedList<AbstractTimer<? extends ITimerable>> timers = new SerializableLinkedList<AbstractTimer<? extends ITimerable>>();

	protected final SerializableLinkedList<T> timerables = new SerializableLinkedList<T>();
	private final SerializableLinkedList<T> newToList = new SerializableLinkedList<T>();
	private final SerializableLinkedList<T> removeList = new SerializableLinkedList<T>();

	private final String name;

	protected AbstractTimer(short interruptDelay) {
		this.name = this.getClass().getName();
		NetworkTimer.schedule(this, interruptDelay);

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
