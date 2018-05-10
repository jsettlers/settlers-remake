package jsettlers.logic.movable.components;

import java.io.Serializable;
import java.util.HashSet;

import java8.util.Optional;
import java8.util.function.Consumer;
import java8.util.stream.Stream;
import jsettlers.logic.movable.Entity;
import jsettlers.logic.movable.Notification;

import static java8.util.stream.StreamSupport.stream;

public abstract class Component implements Serializable {
	private static final long serialVersionUID = -3071296154652495126L;

	public  Entity                entity;
	private HashSet<Notification> consumedNotifications = new HashSet<>();

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

	<T extends Notification> Optional<T> getNextNotification(Class<T> type, boolean consume) {
		Optional<T> result = getNotificationsOfType(type).findFirst();
		if (consume) {
			result.ifPresent(this::consumeNotification);
		}
		return result;
	}

	public <T extends Notification> boolean hasNotificationOfType(Class<T> type) {
		return getNotificationsOfType(type).findAny().isPresent();
	}

	public <T extends Notification> boolean hasNotificationOfType(Class<T> type, boolean consume) { // this implementation uses findFirst to guarantee determinism
		return getNextNotification(type, consume).isPresent();
	}

	private <T extends Notification> Stream<T> getNotificationsOfType(Class<T> type) {
		//noinspection unchecked
		return stream(entity.getAllNotifications())
			.parallel()
			.filter(notification -> !consumedNotifications.contains(notification))
			.filter(type::isInstance).map(notification -> (T) notification);
	}

	<T extends Notification> void forFirstNotificationOfType(Class<T> type, Consumer<T> consumer) {
		getNotificationsOfType(type).findFirst().ifPresent(consumer);
	}

	boolean consumeNotification(Notification notification) {
		if (entity.getAllNotifications().contains(notification)) {
			consumedNotifications.add(notification);
			return true;
		}
		return false;
	}
}
