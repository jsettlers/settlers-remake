package jsettlers.logic.movable;

import java.io.Serializable;

public abstract class Component implements Serializable {
    public Entity entity;
    public void OnAwake() {}
    public void OnStart() {}
    public void OnUpdate() {}
    public void OnLateUpdate() {}
}
