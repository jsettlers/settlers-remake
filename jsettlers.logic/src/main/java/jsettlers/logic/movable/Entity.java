package jsettlers.logic.movable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jsettlers.logic.constants.Constants;
import jsettlers.logic.timer.IScheduledTimerable;
import jsettlers.logic.timer.RescheduleTimer;

/**
 * Created by jt-1 on 2/5/2017.
 */

public class Entity implements Serializable, IScheduledTimerable {
    private static Integer nextId = Movable.nextID;
    private final int id;

    private static final long serialVersionUID = -5615478576016074072L;
    private Map<Class<? extends Component>, Component> components;
    private Set<Notification> notificationsCurrent;
    private Set<Notification> notificationsLast;

    public enum State { ACTIVE, INACTIVE, UNINITALIZED }
    private State state;
    private int invokationDelay;

    public Entity() {
        id = nextId++;
        state = State.UNINITALIZED;
        components = new IdentityHashMap<>();
        notificationsCurrent = new HashSet<>();
        notificationsLast = new HashSet<>();
    }

    public Entity(Component... cs) {
        this();
        for (Component c : cs) {
            add(c);
        }
    }

    private int resetInvokationDelay() {
        int lastValue = invokationDelay;
        invokationDelay = Constants.MOVABLE_INTERRUPT_PERIOD;
        return lastValue;
    }

    public void setInvokationDelay(int delay) {
        invokationDelay = Math.max(invokationDelay, delay);
    }

    public boolean isActive() {
        return state == State.ACTIVE;
    }

    public void setActive(boolean active) {
        if (active == isActive()) return;
        if (state == State.UNINITALIZED) {
            initialize();
        }

        if (active) {
            state = State.ACTIVE;
            for (Component c : components.values()) {
                c.onEnable();
            }
        } else {
            state = State.INACTIVE;
            for (Component c : components.values()) {
                c.onDisable();
            }
        }
    }

    private void initialize() {
        for (Component component : components.values()) {
            component.onAwake();
        }
        RescheduleTimer.add(this, Constants.MOVABLE_INTERRUPT_PERIOD);
    }

    private void invokeUpdate() {
        for (Component component : components.values()) {
            component.onUpdate();
        }
        for (Component component : components.values()) {
            component.onLateUpdate();
        }
    }

    private void invokeDestroy() {
        for (Component component : components.values()) {
            component.onDestroy();
        }
    }

    public int getID() {
        return id;
    }

    public void add(Component c) {
        Class cls = c.getClass();
        components.put(cls, c);
        c.entity = this;
        // Iterate over all super classes
        cls = cls.getSuperclass();
        do {
            components.put(cls, c);
            cls = cls.getSuperclass();
        } while (cls != null && cls != Component.class);
    }

    public void remove(Class<? extends Component> c) {
        components.remove(c);
        Class cls = c.getSuperclass();
        while (cls != null && cls != Component.class) {
            components.remove(cls);
            cls = cls.getSuperclass();
        }
    }

    @SuppressWarnings("unchecked")
    public <C extends Component> C get(Class<C> c) {
        return (C) components.get(c);
    }

    public boolean containsComponent(Class<? extends Component> c) {
        return components.containsKey(c.getName());
    }

    public <T extends Notification> Iterator<T> getNotificationsIt(Class<T> type) {
        class NotificationIterator implements Iterator<T> {
            T nextItem;
            Iterator<Notification> it = notificationsLast.iterator();
            boolean consumed = false;

            public NotificationIterator() {
                findNext();
            }

            private void findNext() {
                consumed = false;
                nextItem = null;
                while (it.hasNext()) {
                    try {
                        nextItem = (T)it.next();
                        return;
                    } catch (ClassCastException e) {
                        nextItem = null;
                    }
                }
            }

            @Override
            public boolean hasNext() {
                if (consumed) findNext();
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

    public <T extends Notification> List<T> getNotifications(Class<T> type) {
        Iterator<Notification> it = notificationsLast.iterator();
        List<T> result = new ArrayList<T>();
        while (it.hasNext()) {
            try {
                result.add((T)it.next());
            } catch (ClassCastException e) {}
        }
        return result;
    }

    public void raiseNotification(Notification note) {
        notificationsCurrent.add(note);
    }

    public void convertTo(Entity blueprint) {
        // remove all unused components
        for (Class<? extends Component> cls : components.keySet()) {
            if (!blueprint.containsComponent(cls)) remove(cls);
        }
        // add all new components
        List<Component> newComponents = new ArrayList<>();
        for (Class<? extends Component> cls : blueprint.components.keySet()) {
            Component c = blueprint.get(cls);
            blueprint.remove(cls);
            newComponents.add(c);
            add(c);
        }
        if (state != State.UNINITALIZED) {
            // initialize all new components
            for (Component c : newComponents) {
                c.onAwake();
            }
            if (state == State.ACTIVE) {
                for (Component c : newComponents) {
                    c.onEnable();
                }
            }
        }
    }

    private final void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        Entity.nextId = Math.max(Entity.nextId, this.id + 1);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(30);
        sb.append("Entity");
        sb.append("(").append(id).append(")");
        sb.append("[");
        for (Component c : components.values()) {
            sb.append(c).append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public int timerEvent() {
        if (!isActive()) return -1;

        invokeUpdate();

        notificationsLast = notificationsCurrent;
        notificationsCurrent = new HashSet<>();

        return resetInvokationDelay();
    }

    @Override
    public void kill() {
        setActive(false);
        invokeDestroy();
    }
}
