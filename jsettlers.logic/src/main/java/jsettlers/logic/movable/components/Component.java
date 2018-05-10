package jsettlers.logic.movable.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import jsettlers.logic.movable.Entity;
import jsettlers.logic.movable.Notification;

public abstract class Component implements Serializable {
	private static final long                  serialVersionUID      = -3071296154652495126L;
	public               Entity                entity;
	private              HashSet<Notification> consumedNotifications = new HashSet<>();

	/**
	 * Called once when the entity gets enabled for the first time
	 */
	public final void wakeUp() {
		onWakeUp();
	}

	protected void onWakeUp() {}

	public final void update() {
		consumedNotifications.clear();
		onUpdate();
	}

	protected void onUpdate() {}

	public final void lateUpdate() { onLateUpdate(); }

	protected void onLateUpdate() {}

	/**
	 *  Called when the entity is set to active
	 */
	public final void enable() { onEnable(); }

	protected void onEnable() {}

	/**
	 *  Called when the entity is set to inactive
	 */
	public final void disable() { onDisable(); }

	protected void onDisable() {}

	/**
	 *  Called before the entity gets destroyed/killed
	 */
	public final void destroy() { onDestroy(); }

	protected void onDestroy() {}

	public <T extends Notification> List<T> getNotifications(Class<T> type) {
		Iterator<T> it = getNotificationsIterator(type);
		List<T> result = new ArrayList<T>();
		while (it.hasNext()) {
			result.add(it.next());
		}
		return result;
	}

	public <T extends Notification> T getNextNotification(Class<T> type, boolean consume) {
		Iterator<T> it = getNotificationsIterator(type);
		T note = it.next();
		if (note != null) {
			if (consume) { consumeNotification(note); }
		}
		return null;
	}

	public <T extends Notification> boolean containsNotification(Class<T> type) {
		return getNotificationsIterator(type).hasNext();
	}

	public <T extends Notification> Iterator<T> getNotificationsIterator(Class<T> type) {
		class NotificationIterator implements Iterator<T> {
			private T                      nextItem;
			private Iterator<Notification> it       = entity.getAllNotifications().iterator();
			private boolean                consumed = false;

			private NotificationIterator() {
				findNext();
			}

			private void findNext() {
				consumed = false;
				nextItem = null;
				while (it.hasNext()) {
					Notification n = it.next();
					if (type.isInstance(n) && !consumedNotifications.contains(n)) {
						nextItem = (T) n;
						return;
					}
					nextItem = null;
				}
			}

			@Override
			public boolean hasNext() {
				if (consumed) { findNext(); }
				return nextItem != null;
			}

			@Override
			public T next() {
				consumed = true;
				return nextItem;
			}
		}
		return new NotificationIterator();
	}

	public boolean consumeNotification(Notification notification) {
		if (entity.getAllNotifications().contains(notification)) {
			consumedNotifications.add(notification);
			return true;
		}
		return false;
	}
}
