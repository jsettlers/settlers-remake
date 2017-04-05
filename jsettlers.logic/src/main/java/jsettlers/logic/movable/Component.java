package jsettlers.logic.movable;

import java.io.Serializable;

public abstract class Component implements Serializable {
    public Entity entity;

    /**
     * Called once when the entity gets enabled for the first time
     */
    public void onAwake() {}

    public void onUpdate() {}
    public void onLateUpdate() {}

    /**
     *  Called when the entity is set to active
     */
    public void onEnable() {}

    /**
     *  Called when the entity is set to inactive
     */
    public void onDisable() {}

    /**
     *  Called before the entity gets destroyed/killed
     */
    public void onDestroy() {}
}
