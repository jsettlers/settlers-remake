package jsettlers.logic.movable.simplebehaviortree.nodes;

import jsettlers.common.movable.EMovableAction;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.Notification;
import jsettlers.logic.movable.simplebehaviortree.IBooleanConditionFunction;

public class NotificationCondition extends Condition<Context> {
	private static final long serialVersionUID = 1780756145252644771L;

	public NotificationCondition(Class<? extends Notification> type) {
		this(type, false);
	}

	public NotificationCondition(Class<? extends Notification> type, boolean consume) {
		super((context) -> context.component.hasNotificationOfType(type, consume));
	}

	public <T extends Notification> NotificationCondition(Class<T> type, IBooleanConditionFunction<T> predicate, boolean consume) {
		super((context) -> context.component.forFirstNotificationOfTypeP(type, predicate, consume));
	}
}
