package jsettlers.logic.movable;

import java.io.Serializable;

public abstract class Component implements Serializable {
    public Entity entity;

    /**
     * Called once when the entity gets enabled for the first time
     */
    public void OnAwake() {}

    public void OnUpdate() {}
    public void OnLateUpdate() {}

    /**
     *  Called when the entity is set to active
     */
    public void OnEnable() {}

    /**
     *  Called when the entity is set to inactive
     */
    public void OnDisable() {}

    /**
     *  Called before the entity gets destroyed/killed
     */
    public void OnDestroy() {}
}
