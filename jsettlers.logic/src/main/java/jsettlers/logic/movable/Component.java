package jsettlers.logic.movable;

import java.io.Serializable;

/**
 * Created by jt-1 on 2/5/2017.
 */

public abstract class Component implements Serializable {
    public Entity entity;
    public void OnAwake() {}
    public void OnUpdate() {}
    public void OnLateUpdate() {}
}
