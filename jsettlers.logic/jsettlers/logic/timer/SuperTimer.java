package jsettlers.logic.timer;

import jsettlers.common.SerializableLinkedList;

import synchronic.timer.INetworkTimerable;
import synchronic.timer.NetworkTimer;

public abstract class SuperTimer<T extends ITimerable> implements INetworkTimerable {
	private static final SerializableLinkedList<SuperTimer<? extends ITimerable>> timers = new SerializableLinkedList<SuperTimer<? extends ITimerable>>();

	protected final SerializableLinkedList<T> timerables = new SerializableLinkedList<T>();
	private final SerializableLinkedList<T> newToList = new SerializableLinkedList<T>();
	private final SerializableLinkedList<T> removeList = new SerializableLinkedList<T>();

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
