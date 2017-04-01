package jsettlers.logic.movable.simplebehaviortree.nodes;

import java.util.function.Function;

import jsettlers.logic.movable.Entity;
import jsettlers.logic.movable.Notification;

/**
 * Created by jt-1 on 4/1/2017.
 */

public class Trigger extends Condition<Entity> {
    public Trigger(Class<? extends Notification> type) {
        super((entity)->entity.getNotificationsIt(type).hasNext());
    }
}
