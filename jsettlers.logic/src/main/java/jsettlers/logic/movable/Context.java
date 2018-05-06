package jsettlers.logic.movable;

import java.io.Serializable;

import jsettlers.logic.movable.components.Component;

/**
 * Created by homoroselaps
 */
public final class Context implements Serializable {
	public final Entity    entity;
	public final Component comp;

	public Entity getEntity() { return entity; }

	public Component getComponent() { return comp; }

	public Context(Entity entity, Component component) {
		this.entity = entity;
		this.comp = component;
	}
}
