package jsettlers.logic.movable.simplebehaviortree.nodes;

import java.util.Iterator;
import java.util.function.Function;

import jsettlers.logic.movable.Entity;
import jsettlers.logic.movable.Notification;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;

/**
 * Created by jt-1 on 4/1/2017.
 */

public class WaitFor extends Action<Entity> {
    private boolean consume = false;
    public WaitFor(Class<? extends Notification> type) {
        this(type, false);
    }

    public WaitFor(Class<? extends Notification> type, boolean consume) {
        super((entity)->{
            Iterator<? extends Notification> it = entity.getNotificationsIt(type);
            if (it.hasNext()) {
                if (consume) entity.consumeNotification(it.next());
                return NodeStatus.Success;
            }
            return NodeStatus.Running();
        });
        this.consume = consume;
    }
}
