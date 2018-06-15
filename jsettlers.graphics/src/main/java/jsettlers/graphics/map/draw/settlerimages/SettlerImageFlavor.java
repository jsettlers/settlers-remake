package jsettlers.graphics.map.draw.settlerimages;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;

import java.util.Objects;

public final class SettlerImageFlavor {

    public static final SettlerImageFlavor NONE = new SettlerImageFlavor(null, null, null, null);
    private final EMovableType type;
    private final EMovableAction action;
    private final EMaterialType material;
    private final EDirection direction;

    public SettlerImageFlavor(EMovableType type, EMovableAction action, EMaterialType material, EDirection direction) {
        this.type = type;
        this.action = action;
        this.material = material;
        this.direction = direction;
    }

    static SettlerImageFlavor createFromMovable(IMovable movable) {
        return new SettlerImageFlavor(movable.getMovableType(), movable.getAction(), movable.getMaterial(), movable.getDirection());
    }

    public EMovableType getType() {
        return type;
    }

    public EMovableAction getAction() {
        return action;
    }

    public EMaterialType getMaterial() {
        return material;
    }

    public EDirection getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SettlerImageFlavor that = (SettlerImageFlavor) o;
        return type == that.type &&
                action == that.action &&
                material == that.material &&
                direction == that.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, action, material, direction);
    }
}
