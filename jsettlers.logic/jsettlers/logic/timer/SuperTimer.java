package jsettlers.logic.timer;

import java.util.LinkedList;

import synchronic.timer.INetworkTimerable;
import synchronic.timer.NetworkTimer;

public abstract class SuperTimer<T extends ITimerable> implements INetworkTimerable {
	private static final LinkedList<SuperTimer<? extends ITimerable>> timers = new LinkedList<SuperTimer<? extends ITimerable>>();

	protected final LinkedList<T> timerables = new LinkedList<T>();
	private final LinkedList<T> newToList = new LinkedList<T>();
	private final LinkedList<T> removeList = new LinkedList<T>();

	protected SuperTimer(short interruptDelay) {
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
			System.err.println("super timer catched");
			e.printStackTrace();
		}
	}

	public void cancel() {
		timers.remove(this);
	}

	protected abstract void executeAction();

	public void add_(T s) {
		synchronized (newToList) {
			newToList.add(s);
		}
	}

	public void remove_(T s) {
		synchronized (removeList) {
			removeList.add(s);
		}
	}

}
