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
 * @author homoroselaps
 */
public final class ManageableBearerWrapper implements IManageableBearer {
	private static final long serialVersionUID = 2252932151684965586L;

	private final Entity entity;

	public ManageableBearerWrapper(Entity entity) {
		this.entity = entity;
	}

	@Override
	public ShortPoint2D getPos() {
		return entity.getComponent(MovableComponent.class).getPos();
	}

	@Override
	public boolean becomeWorker(IWorkerRequester requester, WorkerCreationRequest request) {
		return entity.getComponent(BearerComponent.class).becomeWorker(requester, request);
	}

	@Override
	public boolean becomeWorker(IWorkerRequester requester, WorkerCreationRequest request, IMaterialOffer offer) {
		return entity.getComponent(BearerComponent.class).becomeWorker(requester, request, offer);
	}

	@Override
	public boolean becomeSoldier(IBarrack barrack) {
		return entity.getComponent(BearerComponent.class).becomeSoldier(barrack);
	}

	@Override
	public void deliver(EMaterialType materialType, IMaterialOffer offer, IMaterialRequest request) {
		entity.getComponent(BearerComponent.class).deliver(materialType, offer, request);
	}
}
