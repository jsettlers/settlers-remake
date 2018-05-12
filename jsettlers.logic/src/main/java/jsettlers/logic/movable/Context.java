package jsettlers.logic.movable;

import java.io.Serializable;

import jsettlers.logic.movable.components.Component;

/**
 * Created by homoroselaps
 */
public final class Context implements Serializable {
	public final Entity    entity;
	public final Component component;

	public int debugLevel = 0;

	public Context(Entity entity, Component component) {
		this.entity = entity;
		this.component = component;
	}

	public Entity getEntity() { return entity; }

	public Component getComponent() { return component; }
}
