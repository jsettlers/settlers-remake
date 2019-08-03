package jsettlers.logic.movable;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;
import jsettlers.logic.movable.components.BuildingWorkerComponent;
import jsettlers.logic.movable.components.MovableComponent;

public final class ManageableWorkerWrapper implements IManageableWorker {
    private static final long serialVersionUID = 2252932351688961586L;

    private final Entity entity;

    public ManageableWorkerWrapper(Entity entity) { this.entity = entity; }

    @Override
    public EMovableType getMovableType() {
        return entity.movableComponent().getMovableType();
    }

    @Override
    public void setWorkerJob(IWorkerRequestBuilding building) {
        entity.getComponent(BuildingWorkerComponent.class).setWorkerJob(building);
    }

    @Override
    public void buildingDestroyed() {
        entity.getComponent(BuildingWorkerComponent.class).buildingDestroyed();
    }

    @Override
    public boolean isAlive() {
        return entity.isActive();
    }

    @Override
    public ShortPoint2D getPosition() {
        return entity.movableComponent().getPosition();
    }
}
