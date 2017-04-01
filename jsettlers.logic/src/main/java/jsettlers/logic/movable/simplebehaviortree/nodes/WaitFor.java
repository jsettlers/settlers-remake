package jsettlers.logic.movable.simplebehaviortree.nodes;

import java.util.function.Function;

import jsettlers.logic.movable.Entity;
import jsettlers.logic.movable.Notification;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;

/**
 * Created by jt-1 on 4/1/2017.
 */

public class WaitFor extends Action<Entity> {
    public WaitFor(Class<? extends Notification> type) {
        super((entity)->{
            if (entity.getNotificationsIt(type).hasNext()) {
                return NodeStatus.Success;
            }
            return NodeStatus.Running();
        });
    }
}
