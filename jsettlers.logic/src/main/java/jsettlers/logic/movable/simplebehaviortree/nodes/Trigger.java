package jsettlers.logic.movable.simplebehaviortree.nodes;

import java.util.function.Function;

import jsettlers.logic.movable.Entity;
import jsettlers.logic.movable.Notification;

/**
 * @author homoroselaps
 */

public class Trigger extends Condition<Entity> {
    private static final long serialVersionUID = -2201228478067951215L;

    public Trigger(Class<? extends Notification> type) {
        super((entity)->entity.getNotificationsIt(type).hasNext());
    }
}
