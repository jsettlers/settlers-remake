package jsettlers.logic.movable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jt-1 on 2/5/2017.
 * from: https://github.com/bakpakin/EGF
 */

public class Entity implements Serializable {
    private static int nextId = Integer.MIN_VALUE;
    private final int id;

    private static final long serialVersionUID = -5615478576016074072L;
    private Map<String, Component> components;
    private Map<String, Object> properties;

    boolean active;

    public Entity() {
        this.id = nextId++;
        this.active = true;
        this.components = new HashMap<String, Component>();
        this.properties = new HashMap<String, Object>();
    }

    public Entity(Component... cs) {
        this();
        for (Component c : cs) {
            add(c);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getID() {
        return id;
    }

    public void add(Component c) {
        components.put(c.getClass().getName(), c);

        c.entity = this;
    }

    public void remove(Class<? extends Component> c) {
        components.remove(c.toString());//.entity = null;
    }

    @SuppressWarnings("unchecked")
    public <C extends Component> C get(Class<C> c) {
        return (C) components.get(c.getName());
    }

    public Component get(String c) {
        return components.get(c);
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

    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }

    public void removeProperty(String name) {
        properties.remove(name);
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }
}
