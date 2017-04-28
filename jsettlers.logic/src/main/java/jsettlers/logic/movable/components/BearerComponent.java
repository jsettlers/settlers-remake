package jsettlers.logic.movable.components;

import jsettlers.common.material.EMaterialType;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IBarrack;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IMaterialOffer;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IMaterialRequest;
import jsettlers.logic.map.grid.partition.manager.objects.WorkerCreationRequest;
import jsettlers.logic.movable.Notification;

/**
 * @author homoroselaps
 */

public class BearerComponent extends MaterialComponent {
    private static final long serialVersionUID = -3315837668805312398L;

    public static class DeliveryJob extends Notification {
        public final EMaterialType materialType;
        public final IMaterialOffer offer;
        public final IMaterialRequest request;

        public DeliveryJob(EMaterialType materialType, IMaterialOffer offer, IMaterialRequest request) {
            this.offer =offer;
            this.request = request;
            this.materialType = materialType;
        }
    }

    public static class BecomeWorkerJob extends Notification {
        public final IManageableBearer.IWorkerRequester requester;
        public final WorkerCreationRequest workerCreationRequest;
        public final IMaterialOffer offer;

        public BecomeWorkerJob(IManageableBearer.IWorkerRequester requester, WorkerCreationRequest workerCreationRequest, IMaterialOffer offer) {
            this.requester = requester;
            this.workerCreationRequest = workerCreationRequest;
            this.offer = offer;
        }
    }

    public static class BecomeSoldierJob extends Notification {
        public final IBarrack barrack;

        public BecomeSoldierJob(IBarrack barrack) {
            this.barrack = barrack;
        }
    }

    public EMaterialType materialType;
    public IMaterialOffer materialOffer;
    public IMaterialRequest deliveryRequest;

    public IManageableBearer.IWorkerRequester workerRequester;
    public WorkerCreationRequest workerCreationRequest;

    public IBarrack barrack;

    private boolean hasDeliveryJob = false;
    private boolean hasBecomeWorkerJob = false;
    private boolean hasBecomeSoldierJob = false;

    public boolean hasJob() {
        return hasBecomeSoldierJob || hasDeliveryJob || hasBecomeWorkerJob;
    }

    public void setBecomeSoldierJob(BecomeSoldierJob job) {
        assert job != null : "No Null";
        resetJob();
        barrack = job.barrack;
        hasBecomeSoldierJob = true;
    }

    public boolean hasBecomeSoldierJob() {
        return hasBecomeSoldierJob;
    }

    public void setDeliveryJob(DeliveryJob job) {
        assert job != null : "No Null";
        resetJob();
        materialType = job.materialType;
        materialOffer = job.offer;
        deliveryRequest = job.request;
        hasDeliveryJob = true;
    }

    public boolean hasDeliveryJob() {
        return hasDeliveryJob;
    }

    public void setBecomeWorkerJob(BecomeWorkerJob job) {
        assert job != null : "No Null";
        resetJob();
        materialOffer = job.offer;
        workerRequester = job.requester;
        workerCreationRequest = job.workerCreationRequest;
        hasBecomeWorkerJob = true;
    }

    public boolean hasBecomeWorkerJob() {
        return hasBecomeWorkerJob;
    }

    public void resetJob() {
        hasDeliveryJob = false;
        hasBecomeSoldierJob = false;
        hasBecomeSoldierJob = false;

        materialOffer = null;
        workerRequester = null;
        workerCreationRequest = null;
        barrack = null;
        deliveryRequest = null;
        materialType = EMaterialType.NO_MATERIAL;
    }

    public void deliver(EMaterialType materialType, IMaterialOffer offer, IMaterialRequest request) {
        entity.raiseNotification(new DeliveryJob(materialType, offer, request));
    }

    public boolean becomeWorker(IManageableBearer.IWorkerRequester requester, WorkerCreationRequest workerCreationRequest) {
        if (!hasJob()) {
            entity.raiseNotification(new BecomeWorkerJob(requester, workerCreationRequest, null));
            return true;
        }
        return false;
    }

    public boolean becomeWorker(IManageableBearer.IWorkerRequester requester, WorkerCreationRequest workerCreationRequest, IMaterialOffer offer) {
        if (!hasJob()) {
            entity.raiseNotification(new BecomeWorkerJob(requester, workerCreationRequest, offer));
            return true;
        }
        return false;
    }

    public boolean becomeSoldier(IBarrack barrack) {
        if (!hasJob()) {
            entity.raiseNotification(new BecomeSoldierJob(barrack));
            return true;
        }
        return false;
    }
}
