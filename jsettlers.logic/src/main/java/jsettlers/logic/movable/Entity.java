package jsettlers.logic.movable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import jsettlers.logic.constants.Constants;
import jsettlers.logic.timer.IScheduledTimerable;

/**
 * Created by jt-1 on 2/5/2017.
 * from: https://github.com/bakpakin/EGF
 */

public class Entity implements Serializable, IScheduledTimerable {
    private static int nextId = Integer.MIN_VALUE;
    private final int id;

    private static final long serialVersionUID = -5615478576016074072L;
    private Map<Class, Component> components;

    private boolean active;
    private int invokationDelay;

    public Entity() {
        this.id = nextId++;
        this.active = true;
        this.components = new IdentityHashMap<Class, Component>();
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
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void InvokeAwake() {
        for (Component component : components.values()) {
            component.OnAwake();
        }
    }

    public void InvokeUpdate() {
        for (Component component : components.values()) {
            component.OnUpdate();
        }
        for (Component component : components.values()) {
            component.OnLateUpdate();
        }
    }

    public int getID() {
        return id;
    }

    public void add(Component c) {
        Class cls = c.getClass();
        components.put(cls, c);
        c.entity = this;
        //Iterate over all super classes
        cls = cls.getSuperclass();
        do {
            components.put(cls, c);
            cls = cls.getSuperclass();
        } while (cls != null);
    }

    public void remove(Class<? extends Component> c) {
        components.remove(c.toString());//.entity = null;
    }

    @SuppressWarnings("unchecked")
    public <C extends Component> C get(Class<C> c) {
        return (C) components.get(c);
    }

    public boolean containsComponent(Class<? extends Component> c) {
        return components.containsKey(c.getName());
    }

    public boolean containsComponent(String c) {
        return components.containsKey(c);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(30);
        sb.append("Entity[");
        sb.append(id);
        for (Component c : components.values()) {
            sb.append(c);
            sb.append(", ");
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

        InvokeUpdate();

        return resetInvokationDelay();
    }

    @Override
    public void kill() {
        setActive(false);
    }
}
