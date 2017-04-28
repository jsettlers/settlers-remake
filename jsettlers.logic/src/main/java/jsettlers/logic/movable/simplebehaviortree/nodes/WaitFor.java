package jsettlers.logic.movable.simplebehaviortree.nodes;

import java.util.Iterator;

import jsettlers.logic.movable.Entity;
import jsettlers.logic.movable.Notification;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;

/**
 * @author homoroselaps
 */

public class WaitFor extends Action<Entity> {
    private static final long serialVersionUID = 1780756145252644771L;

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
    }
}
