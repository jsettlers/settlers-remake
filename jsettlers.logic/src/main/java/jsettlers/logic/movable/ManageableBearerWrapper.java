package jsettlers.logic.movable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IBarrack;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IMaterialOffer;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IMaterialRequest;
import jsettlers.logic.map.grid.partition.manager.objects.WorkerCreationRequest;
import jsettlers.logic.movable.components.BearerComponent;
import jsettlers.logic.movable.components.MovableComponent;

/**
 * Created by jt-1 on 4/5/2017.
 */

public final class ManageableBearerWrapper implements IManageableBearer {
    private Entity entity;
    public ManageableBearerWrapper(Entity entity) {
        this.entity = entity;
    }

    @Override
    public ShortPoint2D getPos() {
        return entity.get(MovableComponent.class).getPos();
    }

    @Override
    public boolean becomeWorker(IWorkerRequester requester, WorkerCreationRequest request) {
        return entity.get(BearerComponent.class).becomeWorker(requester, request);
    }

    @Override
    public boolean becomeWorker(IWorkerRequester requester, WorkerCreationRequest request, IMaterialOffer offer) {
        return entity.get(BearerComponent.class).becomeWorker(requester, request, offer);
    }

    @Override
    public boolean becomeSoldier(IBarrack barrack) {
        return entity.get(BearerComponent.class).becomeSoldier(barrack);
    }

    @Override
    public void deliver(EMaterialType materialType, IMaterialOffer offer, IMaterialRequest request) {
        entity.get(BearerComponent.class).deliver(materialType, offer, request);
    }
}
