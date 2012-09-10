package jsettlers.logic.map.newGrid.partition.manager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import jsettlers.common.SerializableLinkedList;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.queue.SlotQueue;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.map.newGrid.partition.manager.datastructures.PositionableList;
import jsettlers.logic.map.newGrid.partition.manager.datastructures.SimpleSlotQueue;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer.IWorkerRequester;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IBarrack;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IDiggerRequester;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IMaterialRequester;
import jsettlers.logic.map.newGrid.partition.manager.objects.BricklayerRequest;
import jsettlers.logic.map.newGrid.partition.manager.objects.DiggerRequest;
import jsettlers.logic.map.newGrid.partition.manager.objects.MaterialOffer;
import jsettlers.logic.map.newGrid.partition.manager.objects.MaterialRequest;
import jsettlers.logic.map.newGrid.partition.manager.objects.MaterialTypeAcceptor;
import jsettlers.logic.map.newGrid.partition.manager.objects.MovableTypeAcceptor;
import jsettlers.logic.map.newGrid.partition.manager.objects.OfferMap;
import jsettlers.logic.map.newGrid.partition.manager.objects.ProductionRequest;
import jsettlers.logic.map.newGrid.partition.manager.objects.SoilderCreationRequest;
import jsettlers.logic.map.newGrid.partition.manager.objects.WorkerCreationRequest;
import jsettlers.logic.map.newGrid.partition.manager.objects.WorkerRequest;
import jsettlers.logic.timer.ITimerable;
import jsettlers.logic.timer.PartitionManagerTimer;

/**
 * This is a manager for a partition. It stores offers, requests and jobless to build up jobs and give them to the jobless.
 * 
 * @author Andreas Eberle
 * 
 */
public class PartitionManager implements ITimerable, Serializable, IWorkerRequester {
	private static final long serialVersionUID = 1L;

	private static final int BRICKLAYER_DIGGER_MAX_CONCURRENT_REQUESTS = 1;

	private final MaterialTypeAcceptor materialTypeAcceptor = new MaterialTypeAcceptor();
	private final MovableTypeAcceptor movableTypeAcceptor = new MovableTypeAcceptor();

	private final OfferMap materialOffers = new OfferMap();
	private final SlotQueue<EMaterialType, MaterialRequest> materialRequests = new SlotQueue<EMaterialType, MaterialRequest>(EMaterialType.values(),
			new int[EMaterialType.values().length]);
	private final PositionableList<IManageableBearer> joblessBearer = new PositionableList<IManageableBearer>();

	private final SerializableLinkedList<WorkerRequest> workerRequests = new SerializableLinkedList<WorkerRequest>();
	private final PositionableList<IManageableWorker> joblessWorkers = new PositionableList<IManageableWorker>();

	private final SerializableLinkedList<DiggerRequest> diggerRequests = new SerializableLinkedList<DiggerRequest>();
	private final PositionableList<IManageableDigger> joblessDiggers = new PositionableList<IManageableDigger>();

	private final SerializableLinkedList<BricklayerRequest> bricklayerRequests = new SerializableLinkedList<BricklayerRequest>();
	private final PositionableList<IManageableBricklayer> joblessBricklayers = new PositionableList<IManageableBricklayer>();

	private final SimpleSlotQueue<EMovableType, WorkerCreationRequest> workerCreationRequests = new SimpleSlotQueue<EMovableType, WorkerCreationRequest>(
			EMovableType.values());
	private final SerializableLinkedList<SoilderCreationRequest> soilderCreationRequests = new SerializableLinkedList<SoilderCreationRequest>();

	private final SlotQueue<EMaterialType, ProductionRequest> toolProductionRequests = new SlotQueue<EMaterialType, ProductionRequest>(
			new EMaterialType[] { EMaterialType.HAMMER, EMaterialType.BLADE, EMaterialType.AXE, EMaterialType.SAW, EMaterialType.PICK,
					EMaterialType.FISHINGROD, EMaterialType.SCYTHE }, new int[] { 50, 50, 30, 30, 30, 30, 30 });
	private final SlotQueue<EMaterialType, ProductionRequest> weaponProductionRequests = new SlotQueue<EMaterialType, ProductionRequest>(
			EMaterialType.values(), new int[EMaterialType.values().length]);

	private final SimpleSlotQueue<EMaterialType, WorkerCreationRequest> toolRequestingWorkerRequests = new SimpleSlotQueue<EMaterialType, WorkerCreationRequest>(
			new EMaterialType[] { EMaterialType.HAMMER, EMaterialType.BLADE, EMaterialType.AXE, EMaterialType.SAW, EMaterialType.PICK,
					EMaterialType.FISHINGROD, EMaterialType.SCYTHE });

	public PartitionManager() {
		schedule();
	}

	private void schedule() {
		PartitionManagerTimer.add(this);
	}

	public void stopManager() {
		PartitionManagerTimer.remove(this);
	}

	public boolean addOffer(ShortPoint2D position, EMaterialType materialType) {
		MaterialOffer existingOffer = materialOffers.getObjectAt(position);
		if (existingOffer != null) {
			if (existingOffer.materialType == materialType) {
				existingOffer.amount++;
				return true;
			} else {
				return false;
			}
		} else {
			materialOffers.set(position, new MaterialOffer(position, materialType, (byte) 1));
			return true;
		}
	}

	public void request(IMaterialRequester requester, EMaterialType materialType, byte priority) {
		materialRequests.add(materialType, new MaterialRequest(requester, materialType, priority));
	}

	public void requestDiggers(IDiggerRequester requester, byte amount) {
		diggerRequests.offer(new DiggerRequest(requester, amount));
	}

	public void requestBricklayer(Building building, ShortPoint2D bricklayerTargetPos, EDirection direction) {
		bricklayerRequests.offer(new BricklayerRequest(building, bricklayerTargetPos, direction));
	}

	public void requestBuildingWorker(EMovableType workerType, WorkerBuilding workerBuilding) {
		workerRequests.offer(new WorkerRequest(workerType, workerBuilding));
	}

	public void requestSoilderable(IBarrack barrack) {
		soilderCreationRequests.offer(new SoilderCreationRequest(barrack));
	}

	public IManageableBearer removeJobless(ShortPoint2D position) {
		return joblessBearer.removeObjectAt(position);
	}

	public void addJobless(IManageableBearer bearer) {
		// TODO @Andreas try to find him a new job first
		this.joblessBearer.insert(bearer);
	}

	public void removeJobless(IManageableBearer bearer) {
		this.joblessBearer.remove(bearer);
	}

	public void addJobless(IManageableDigger digger) {
		joblessDiggers.insert(digger);
	}

	public void removeJobless(IManageableDigger digger) {
		joblessDiggers.remove(digger);
	}

	public void addJobless(IManageableBricklayer bricklayer) {
		joblessBricklayers.insert(bricklayer);
	}

	public void removeJobless(IManageableBricklayer bricklayer) {
		joblessBricklayers.remove(bricklayer);
	}

	public void addJobless(IManageableWorker worker) {
		joblessWorkers.insert(worker);
	}

	public void removeJobless(IManageableWorker worker) {
		joblessWorkers.remove(worker);
	}

	/**
	 * @param x
	 *            x coordinate of the position to be removed from this manager and added to the given manager
	 * @param y
	 *            y coordinate of the position to be removed from this manager and added to the given manager
	 * @param newManager
	 *            new manager of the given position <br>
	 *            NOTE: the new manager MUST NOT be null!
	 * @param newHasSamePlayer
	 */
	public void removePositionTo(final short x, final short y, PartitionManager newManager, boolean newHasSamePlayer) {
		ShortPoint2D position = new ShortPoint2D(x, y);

		MaterialOffer removedOffer = materialOffers.removeObjectAt(position);
		if (removedOffer != null) {// the new manager can not have any offers at that position, because he just occupied it
			newManager.materialOffers.set(position, removedOffer);
		}

		// TODO: use newHasSamePlayer
		materialRequests.moveItemsForPosition(position, newManager.materialRequests);

		toolProductionRequests.moveItemsForPosition(position, newManager.toolProductionRequests);
		weaponProductionRequests.moveItemsForPosition(position, newManager.weaponProductionRequests);
		workerCreationRequests.moveItemsForPosition(position, newManager.workerCreationRequests);

		if (newHasSamePlayer) {
			IManageableBearer bearer = joblessBearer.removeObjectAt(position);
			if (bearer != null)
				newManager.addJobless(bearer);
			IManageableBricklayer bricklayer = joblessBricklayers.removeObjectAt(position);
			if (bricklayer != null)
				newManager.addJobless(bricklayer);
			IManageableDigger digger = joblessDiggers.removeObjectAt(position);
			if (digger != null)
				newManager.addJobless(digger);
			IManageableWorker worker = joblessWorkers.removeObjectAt(position);
			if (worker != null)
				newManager.addJobless(worker);
		}

		removePositionTo(position, this.bricklayerRequests, newManager.bricklayerRequests, newHasSamePlayer);
		removePositionTo(position, this.diggerRequests, newManager.diggerRequests, newHasSamePlayer);
		removePositionTo(position, this.workerRequests, newManager.workerRequests, newHasSamePlayer);
	}

	private <T extends ILocatable> void removePositionTo(ShortPoint2D pos, LinkedList<T> fromList, LinkedList<T> toList, boolean newHasSamePlayer) {
		Iterator<T> iter = fromList.iterator();
		while (iter.hasNext()) {
			T curr = iter.next();
			if (curr.getPos().equals(pos)) {
				iter.remove();
				if (newHasSamePlayer) {
					toList.offer(curr);
				}
			}
		}
	}

	public void mergeInto(PartitionManager newManager) {
		newManager.bricklayerRequests.addAll(this.bricklayerRequests);
		newManager.diggerRequests.addAll(this.diggerRequests);
		newManager.joblessBearer.addAll(this.joblessBearer);
		newManager.joblessBricklayers.addAll(this.joblessBricklayers);
		newManager.joblessDiggers.addAll(this.joblessDiggers);
		newManager.joblessWorkers.addAll(this.joblessWorkers);
		newManager.materialOffers.addAll(this.materialOffers);
		newManager.materialRequests.addAll(this.materialRequests);
		newManager.soilderCreationRequests.addAll(this.soilderCreationRequests);
		newManager.workerCreationRequests.addAll(this.workerCreationRequests);
		newManager.workerRequests.addAll(this.workerRequests);
		newManager.toolProductionRequests.addAll(toolProductionRequests);
		newManager.weaponProductionRequests.addAll(weaponProductionRequests);

		newManager.toolRequestingWorkerRequests.merge(this.toolRequestingWorkerRequests);
	}

	@Override
	public final void timerEvent() {
		handleMaterialRequest();

		handleWorkerCreationRequest();
		handleSoldierCreationRequest();

		handleDiggerRequest();
		handleBricklayerRequest();

		handleWorkerRequest();
	}

	private void handleWorkerRequest() {
		WorkerRequest workerRequest = workerRequests.poll();
		if (workerRequest != null) {
			movableTypeAcceptor.movableType = workerRequest.movableType;
			IManageableWorker worker = joblessWorkers.removeObjectNextTo(workerRequest.getPos(), movableTypeAcceptor);

			if (worker != null) {
				worker.setWorkerJob(workerRequest.building);
			} else {
				if (!workerRequest.creationRequested) {
					workerRequest.creationRequested = true;
					createNewTooluser(workerRequest.movableType, workerRequest.getPos());
				}
				workerRequests.offerLast(workerRequest);
			}
		}
	}

	private void handleWorkerCreationRequest() {
		checkExistingToolRequestingWorkerCreationRequests();

		checkWorkerCreationRequests();
	}

	private void checkWorkerCreationRequests() {
		EMovableType[] movableTypes = workerCreationRequests.getSlotTypes();
		for (int slotIdx = 0; slotIdx < movableTypes.length; slotIdx++) {
			WorkerCreationRequest workerRequest = workerCreationRequests.popFront(slotIdx);

			if (workerRequest != null) {
				EMaterialType tool = workerRequest.movableType.getTool();

				if (tool != EMaterialType.NO_MATERIAL) {

					if (toolRequestingWorkerRequests.getSlotSize(workerRequest.movableType.getTool()) <= 3) {
						this.materialTypeAcceptor.materialType = tool;
						MaterialOffer offer = this.materialOffers.getObjectNextTo(workerRequest.position, this.materialTypeAcceptor);

						if (offer != null) {
							IManageableBearer manageableBearer = joblessBearer.removeObjectNextTo(workerRequest.position);
							if (manageableBearer != null) {
								manageableBearer.becomeWorker(this, workerRequest.movableType, offer.position);
								reduceOfferAmount(offer);
							} else {
								workerCreationRequests.pushLast(slotIdx, workerRequest);
							}

						} else {
							toolProductionRequests.add(tool, new ProductionRequest(tool, workerRequest.position));
							toolRequestingWorkerRequests.pushLast(tool, workerRequest);
						}

					} else {// don't create a worker with this tool if there are already workers requesting this type of tool
						workerCreationRequests.pushLast(slotIdx, workerRequest);
					}

				} else {
					IManageableBearer manageableBearer = joblessBearer.removeObjectNextTo(workerRequest.position);
					if (manageableBearer != null) {
						manageableBearer.becomeWorker(this, workerRequest.movableType);
					} else {
						workerCreationRequests.pushLast(slotIdx, workerRequest);
					}
				}
			}
		}
	}

	private void checkExistingToolRequestingWorkerCreationRequests() {
		EMaterialType[] slotTypes = toolRequestingWorkerRequests.getSlotTypes();
		for (int slot = 0; slot < slotTypes.length; slot++) {
			// check the existing tool requesting WorkerCreationRequests
			WorkerCreationRequest request = toolRequestingWorkerRequests.popFront(slot);
			if (request != null) {
				this.materialTypeAcceptor.materialType = slotTypes[slot];
				MaterialOffer offer = this.materialOffers.getObjectNextTo(request.position, this.materialTypeAcceptor);

				if (offer != null) {
					IManageableBearer manageableBearer = joblessBearer.removeObjectNextTo(request.position);
					if (manageableBearer != null) {
						manageableBearer.becomeWorker(this, request.movableType, offer.position);
						reduceOfferAmount(offer);
					} else { // no bearer found, so add the request back to the queue.
						toolRequestingWorkerRequests.pushLast(slot, request);
					}
				} else { // no offer found, so add the request back to the queue.
					toolRequestingWorkerRequests.pushLast(slot, request);
				}
			}
		}
	}

	@Override
	public void workerCreationRequestFailed(EMovableType type, ShortPoint2D position) {
		workerCreationRequests.pushLast(type, new WorkerCreationRequest(type, position));
	}

	private void handleSoldierCreationRequest() {
		SoilderCreationRequest soilderRequest = soilderCreationRequests.poll();
		if (soilderRequest != null) {
			IManageableBearer manageableBearer = joblessBearer.removeObjectNextTo(soilderRequest.getPos());
			if (manageableBearer != null) {
				manageableBearer.becomeSoldier(soilderRequest.getBarrack());
			} else {
				soilderCreationRequests.addLast(soilderRequest);
			}
		}
	}

	private void handleDiggerRequest() {
		DiggerRequest request = diggerRequests.poll();
		if (request != null && request.requester.isDiggerRequestActive()) {
			IManageableDigger digger = joblessDiggers.removeObjectNextTo(request.getPos());
			if (digger != null) {
				digger.setDiggerJob(request.requester);
				request.amount--;
				request.creationRequested--;
			} else {
				if (request.amount > request.creationRequested) {
					request.creationRequested++;
					createNewToolUserIfLimitNotExceeded(EMovableType.DIGGER, request.getPos());
				}
			}

			if (request.amount > 0) {
				diggerRequests.addLast(request);
			}
		}
	}

	private void handleBricklayerRequest() {
		BricklayerRequest bricklayerRequest = bricklayerRequests.poll();
		if (bricklayerRequest != null && !bricklayerRequest.building.isConstructionFinished()) {
			IManageableBricklayer bricklayer = joblessBricklayers.removeObjectNextTo(bricklayerRequest.getPos());
			if (bricklayer != null) {
				bricklayer.setBricklayerJob(bricklayerRequest.building, bricklayerRequest.bricklayerTargetPos, bricklayerRequest.direction);
			} else {
				createNewToolUserIfLimitNotExceeded(EMovableType.BRICKLAYER, bricklayerRequest.getPos());
				bricklayerRequests.offerLast(bricklayerRequest);
			}
		}
	}

	private void createNewToolUserIfLimitNotExceeded(EMovableType movableType, ShortPoint2D position) {
		if (workerCreationRequests.getSlotSize(movableType) <= BRICKLAYER_DIGGER_MAX_CONCURRENT_REQUESTS) {
			createNewTooluser(movableType, position);
		}
	}

	private void handleMaterialRequest() {
		MaterialRequest request = materialRequests.pop(materialOffers);
		if (request != null) {

			materialTypeAcceptor.materialType = request.requested;
			MaterialOffer offer = materialOffers.getObjectNextTo(request.getPos(), materialTypeAcceptor);

			if (offer == null) {
				reofferRequest(request);
			} else {
				IManageableBearer manageable = joblessBearer.removeObjectNextTo(offer.position);

				if (manageable != null) {
					reduceOfferAmount(offer);
					manageable.executeJob(offer.position, request.requester, offer.materialType);
				} else {
					reofferRequest(request);
				}
			}
		}
	}

	private void createNewTooluser(EMovableType movableType, ShortPoint2D position) {
		workerCreationRequests.pushLast(movableType, new WorkerCreationRequest(movableType, position));
	}

	private void reduceOfferAmount(MaterialOffer offer) {
		offer.amount--;
		if (offer.amount <= 0) {
			materialOffers.removeObjectAt(offer.position);
		}
	}

	private void reofferRequest(MaterialRequest request) {
		// TODO: decrease priority, do something else, ...
		// request.decreasePriority();
		materialRequests.add(request.requested, request);
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		schedule();
	}

	public void releaseRequestsAt(ShortPoint2D position, EMaterialType materialType) {
		materialRequests.removeOfType(materialType, position);
	}

	/**
	 * removes an offer of the given materialType if it exists.
	 * 
	 * @param pos
	 *            position of the offer
	 * @param materialType
	 *            {@link EMaterialType} to be checked.
	 */
	public final void removeOfferAt(ShortPoint2D pos, EMaterialType materialType) {
		MaterialOffer offer = this.materialOffers.getObjectAt(pos);
		if (offer != null && offer.materialType == materialType) {
			reduceOfferAmount(offer);
		}
	}

	public final EMaterialType popToolProduction(ShortPoint2D closeTo) {
		ProductionRequest request = toolProductionRequests.pop(closeTo);
		if (request != null) {
			return request.type;
		} else {
			return null;
		}
	}

	@Override
	public void kill() {
		throw new UnsupportedOperationException("CAN'T KILL PARTITION MANAGER!! THIS REALLY SHOULD NOT HAPPEN!");
	}

}
