package jsettlers.logic.movable;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IConstructableBuilding;
import jsettlers.logic.movable.components.BricklayerComponent;

public final class ManageableBricklayerWrapper implements IManageableBricklayer {
    private static final long serialVersionUID = 2252932351648921543L;

    private final Entity entity;

    public ManageableBricklayerWrapper(Entity entity) { this.entity = entity; }

    @Override
    public ShortPoint2D getPosition() {
        return entity.movableComponent().getPosition();
    }

    @Override
    public boolean setBricklayerJob(IConstructableBuilding constructionSite, ShortPoint2D bricklayerTargetPos, EDirection direction) {
        return entity.getComponent(BricklayerComponent.class).assignBricklayerJob(constructionSite, bricklayerTargetPos, direction);
    }
}
