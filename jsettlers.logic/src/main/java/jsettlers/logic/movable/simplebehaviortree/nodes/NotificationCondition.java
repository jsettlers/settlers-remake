package jsettlers.logic.movable.simplebehaviortree.nodes;

import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.Notification;

public class NotificationCondition extends Condition<Context> {
	private static final long serialVersionUID = 1780756145252644771L;

	public NotificationCondition(Class<? extends Notification> type) {
		this(type, false);
	}

	public NotificationCondition(Class<? extends Notification> type, boolean consume) {
		super((context) -> context.component.hasNotificationOfType(type, consume));
	}
}
