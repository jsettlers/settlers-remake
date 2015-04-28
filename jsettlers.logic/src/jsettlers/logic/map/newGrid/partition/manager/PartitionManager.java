/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.map.newGrid.partition.manager;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import jsettlers.algorithms.queue.SlotQueue;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
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
import jsettlers.logic.map.newGrid.partition.manager.materials.MaterialsManager;
import jsettlers.logic.map.newGrid.partition.manager.materials.interfaces.IJoblessSupplier;
import jsettlers.logic.map.newGrid.partition.manager.materials.interfaces.IManagerBearer;
import jsettlers.logic.map.newGrid.partition.manager.materials.offers.MaterialOffer;
import jsettlers.logic.map.newGrid.partition.manager.materials.offers.OffersList;
import jsettlers.logic.map.newGrid.partition.manager.materials.requests.MaterialRequestObject;
import jsettlers.logic.map.newGrid.partition.manager.objects.BricklayerRequest;
import jsettlers.logic.map.newGrid.partition.manager.objects.DiggerRequest;
import jsettlers.logic.map.newGrid.partition.manager.objects.MovableTypeAcceptor;
import jsettlers.logic.map.newGrid.partition.manager.objects.ProductionRequest;
import jsettlers.logic.map.newGrid.partition.manager.objects.SoilderCreationRequest;
import jsettlers.logic.map.newGrid.partition.manager.objects.WorkerCreationRequest;
import jsettlers.logic.map.newGrid.partition.manager.objects.WorkerRequest;
import jsettlers.logic.map.newGrid.partition.manager.settings.PartitionManagerSettings;
import jsettlers.logic.timer.IScheduledTimerable;
import jsettlers.logic.timer.RescheduleTimer;

/**
 * This is a manager for a partition. It stores offers, requests and jobless to build up jobs and give them to the jobless.
 * 
 * @author Andreas Eberle
 * 
 */
public class PartitionManager implements IScheduledTimerable, Serializable, IWorkerRequester {
	private static final int SCHEDULING_PERIOD = 25;
	private static final long serialVersionUID = 1L;
	private static final int BRICKLAYER_DIGGER_MAX_CONCURRENT_REQUESTS = 1;

	private final PartitionManagerSettings settings = new PartitionManagerSettings();

	private final MovableTypeAcceptor movableTypeAcceptor = new MovableTypeAcceptor();
	private final PositionableList<IManageableBearer> joblessBearer = new PositionableList<IManageableBearer>();
	private final OffersList materialOffers = new OffersList();

	private final MaterialsManager materialsManager = new MaterialsManager(new IJoblessSupplier() {
		private static final long serialVersionUID = -113397265091126902L;

		@Override
		public IManagerBearer removeJoblessCloseTo(ShortPoint2D position) {
			return joblessBearer.removeObjectNextTo(position);
		}

		@Override
		public boolean isEmpty() {
			return joblessBearer.isEmpty();
		}
	}, materialOffers, settings);

	private final LinkedList<WorkerRequest> workerRequests = new LinkedList<WorkerRequest>();
	private final PositionableList<IManageableWorker> joblessWorkers = new PositionableList<IManageableWorker>();

	private final LinkedList<DiggerRequest> diggerRequests = new LinkedList<DiggerRequest>();
	private final PositionableList<IManageableDigger> joblessDiggers = new PositionableList<IManageableDigger>();

	private final LinkedList<BricklayerRequest> bricklayerRequests = new LinkedList<BricklayerRequest>();
	private final PositionableList<IManageableBricklayer> joblessBricklayers = new PositionableList<IManageableBricklayer>();

	private final SimpleSlotQueue<EMovableType, WorkerCreationRequest> workerCreationRequests = new SimpleSlotQueue<EMovableType, WorkerCreationRequest>(
			EMovableType.values);
	private final LinkedList<SoilderCreationRequest> soilderCreationRequests = new LinkedList<SoilderCreationRequest>();

	private final SlotQueue<EMaterialType, ProductionRequest> toolProductionRequests = new SlotQueue<EMaterialType, ProductionRequest>(
			new EMaterialType[] { EMaterialType.HAMMER, EMaterialType.BLADE, EMaterialType.AXE, EMaterialType.SAW, EMaterialType.PICK,
					EMaterialType.FISHINGROD, EMaterialType.SCYTHE }, new int[] { 50, 50, 30, 30, 30, 30, 30 });
	private final SlotQueue<EMaterialType, ProductionRequest> weaponProductionRequests = new SlotQueue<EMaterialType, ProductionRequest>(
			EMaterialType.values, new int[EMaterialType.NUMBER_OF_MATERIALS]);

	private final SimpleSlotQueue<EMaterialType, WorkerCreationRequest> toolRequestingWorkerRequests = new SimpleSlotQueue<EMaterialType, WorkerCreationRequest>(
			new EMaterialType[] { EMaterialType.HAMMER, EMaterialType.BLADE, EMaterialType.AXE, EMaterialType.SAW, EMaterialType.PICK,
					EMaterialType.FISHINGROD, EMaterialType.SCYTHE });

	private boolean stopped = true;

	public void startManager() {
		stopped = false;
		RescheduleTimer.add(this, SCHEDULING_PERIOD);
	}

	public void stopManager() {
		stopped = true;
	}

	public boolean isStopped() {
		return stopped;
	}

	public void addOffer(ShortPoint2D position, EMaterialType materialType) {
		materialOffers.addOffer(position, materialType);
	}

	public void request(EMaterialType materialType, MaterialRequestObject requestObject) {
		materialsManager.addRequestObject(materialType, requestObject);
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
	public void removePositionTo(final int x, final int y, PartitionManager newManager, boolean newHasSamePlayer) {
		ShortPoint2D position = new ShortPoint2D(x, y);

		materialOffers.moveOffersAtPositionTo(position, newManager.materialOffers);

		toolProductionRequests.moveItemsForPosition(position, newManager.toolProductionRequests);
		weaponProductionRequests.moveItemsForPosition(position, newManager.weaponProductionRequests);
		workerCreationRequests.moveItemsForPosition(position, newManager.workerCreationRequests);

		if (newHasSamePlayer) {
			materialsManager.movePositionTo(position, newManager.materialsManager);

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

	public final void mergeInto(PartitionManager newManager) {
		newManager.bricklayerRequests.addAll(this.bricklayerRequests);
		newManager.diggerRequests.addAll(this.diggerRequests);
		newManager.joblessBearer.addAll(this.joblessBearer);
		newManager.joblessBricklayers.addAll(this.joblessBricklayers);
		newManager.joblessDiggers.addAll(this.joblessDiggers);
		newManager.joblessWorkers.addAll(this.joblessWorkers);
		newManager.materialOffers.addAll(this.materialOffers);
		this.materialsManager.mergeInto(newManager.materialsManager);
		newManager.soilderCreationRequests.addAll(this.soilderCreationRequests);
		newManager.workerCreationRequests.addAll(this.workerCreationRequests);
		newManager.workerRequests.addAll(this.workerRequests);
		newManager.toolProductionRequests.addAll(toolProductionRequests);
		newManager.weaponProductionRequests.addAll(weaponProductionRequests);

		newManager.toolRequestingWorkerRequests.merge(this.toolRequestingWorkerRequests);
	}

	@Override
	public final int timerEvent() {
		if (stopped) {
			return -1; // unschedule
		}

		materialsManager.distributeJobs();

		handleWorkerCreationRequest();
		handleSoldierCreationRequest();

		handleDiggerRequest();
		handleBricklayerRequest();

		handleWorkerRequest();

		return SCHEDULING_PERIOD;
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

					if (toolRequestingWorkerRequests.getSlotSize(tool) <= 3) {
						MaterialOffer offer = this.materialOffers.removeOfferCloseTo(tool, workerRequest.position);

						if (offer != null) {
							IManageableBearer manageableBearer = joblessBearer.removeObjectNextTo(workerRequest.position);
							if (manageableBearer != null) {
								manageableBearer.becomeWorker(this, workerRequest.movableType, offer.getPos());
							} else {
								workerCreationRequests.pushLast(slotIdx, workerRequest);
								materialOffers.addOffer(offer.getPos(), tool);
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
				EMaterialType toolType = slotTypes[slot];
				if (!this.materialOffers.isEmpty(toolType)) {
					MaterialOffer offer = this.materialOffers.removeOfferCloseTo(toolType, request.position);

					IManageableBearer manageableBearer = joblessBearer.removeObjectNextTo(request.position);
					if (manageableBearer != null) {
						manageableBearer.becomeWorker(this, request.movableType, offer.getPos());
					} else { // no bearer found, so add the request back to the queue.
						toolRequestingWorkerRequests.pushLast(slot, request);
						this.materialOffers.addOffer(offer.getPos(), toolType);
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
		DiggerRequest request = diggerRequests.peek();
		if (request == null) {
			return;
		}

		if (request.requester.isDiggerRequestActive()) {
			IManageableDigger digger = joblessDiggers.removeObjectNextTo(request.getPos());
			if (digger != null) {
				if (digger.setDiggerJob(request.requester)) {
					request.amount--;
					if (request.creationRequested > 0) {
						request.creationRequested--;
					}
				}
			} else {
				if (request.amount > request.creationRequested) {
					if (createNewToolUserIfLimitNotExceeded(EMovableType.DIGGER, request.getPos())) {
						request.creationRequested++;
					}
				}
			}

			if (request.amount <= 0) {
				diggerRequests.poll();
			}
		} else {
			diggerRequests.poll();
		}
	}

	private void handleBricklayerRequest() {
		BricklayerRequest bricklayerRequest = bricklayerRequests.poll();
		if (bricklayerRequest != null && bricklayerRequest.building.isBricklayerRequestActive()) {
			IManageableBricklayer bricklayer = joblessBricklayers.removeObjectNextTo(bricklayerRequest.getPos());
			if (bricklayer != null) {
				if (!bricklayer.setBricklayerJob(bricklayerRequest.building, bricklayerRequest.bricklayerTargetPos, bricklayerRequest.direction)) {
					bricklayerRequests.add(bricklayerRequest);
				}

			} else if (!bricklayerRequest.isCreationRequested()) { // if the creation hasn't been requested yet => request it.
				createNewToolUserIfLimitNotExceeded(EMovableType.BRICKLAYER, bricklayerRequest.getPos());
				bricklayerRequest.creationRequested();
				bricklayerRequests.offerLast(bricklayerRequest);

			} else { // no bricklayer available and creation already requested => nothing to do.
				bricklayerRequests.offerLast(bricklayerRequest);
			}
		}
	}

	/**
	 * Creates a request to create a new tool user of the given type if there are not already at max
	 * {@value #BRICKLAYER_DIGGER_MAX_CONCURRENT_REQUESTS} requests.
	 * 
	 * @param movableType
	 * @param position
	 * @return true if the request has been created<br>
	 *         false if no request has been created due to the limit.
	 */
	private boolean createNewToolUserIfLimitNotExceeded(EMovableType movableType, ShortPoint2D position) {
		if (workerCreationRequests.getSlotSize(movableType) < BRICKLAYER_DIGGER_MAX_CONCURRENT_REQUESTS) {
			createNewTooluser(movableType, position);
			return true;
		} else {
			return false;
		}
	}

	private void createNewTooluser(EMovableType movableType, ShortPoint2D position) {
		workerCreationRequests.pushLast(movableType, new WorkerCreationRequest(movableType, position));
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
		this.materialOffers.removeOfferAt(pos, materialType);
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

	/**
	 * FOR TESTS ONLY!
	 * 
	 * @param pos
	 * @param material
	 * @return
	 */
	public MaterialOffer getMaterialOfferAt(ShortPoint2D pos, EMaterialType material) {
		return this.materialOffers.getOfferObjectAt(pos, material);
	}

	public PartitionManagerSettings getSettings() {
		return settings;
	}
}
