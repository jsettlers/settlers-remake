package jsettlers.logic.movable.simplebehaviortree.nodes;

import java.util.Iterator;

import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.Notification;

public class NotificationCondition extends Condition<Context> {
	private static final long serialVersionUID = 1780756145252644771L;

	public NotificationCondition(Class<? extends Notification> type) {
		this(type, false);
	}

	public NotificationCondition(Class<? extends Notification> type, boolean consume) {
		super((context) -> {
			Iterator<? extends Notification> it = context.component.getNotificationsIterator(type);
			if (it.hasNext()) {
				if (consume) {
					context.component.consumeNotification(it.next());
				}
				return true;
			}
			return false;
		});
	}
}
