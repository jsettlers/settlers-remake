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
package jsettlers.logic.map.grid.partition.manager;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import jsettlers.common.map.partition.IPartitionSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.map.grid.partition.data.IMaterialCounts;
import jsettlers.logic.map.grid.partition.manager.datastructures.PositionableList;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableBearer.IWorkerRequester;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IBarrack;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IDiggerRequester;
import jsettlers.logic.map.grid.partition.manager.materials.MaterialsManager;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IJoblessSupplier;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IManagerBearer;
import jsettlers.logic.map.grid.partition.manager.materials.offers.IOffersCountListener;
import jsettlers.logic.map.grid.partition.manager.materials.offers.MaterialOffer;
import jsettlers.logic.map.grid.partition.manager.materials.offers.OffersList;
import jsettlers.logic.map.grid.partition.manager.materials.requests.MaterialRequestObject;
import jsettlers.logic.map.grid.partition.manager.objects.BricklayerRequest;
import jsettlers.logic.map.grid.partition.manager.objects.DiggerRequest;
import jsettlers.logic.map.grid.partition.manager.objects.SoilderCreationRequest;
import jsettlers.logic.map.grid.partition.manager.objects.WorkerCreationRequest;
import jsettlers.logic.map.grid.partition.manager.objects.WorkerRequest;
import jsettlers.logic.map.grid.partition.manager.settings.PartitionManagerSettings;
import jsettlers.logic.timer.IScheduledTimerable;
import jsettlers.logic.timer.RescheduleTimer;

/**
 * This is a manager for a partition. It stores offers, requests and jobless to build up jobs and give them to the jobless.
 * 
 * @author Andreas Eberle
 * 
 */
public class PartitionManager implements IScheduledTimerable, Serializable, IWorkerRequester {
	private static final long serialVersionUID = 3759772044136966735L;

	private static final int SCHEDULING_PERIOD = 25;

	private static final byte priorityForTool[] = new byte[EMaterialType.NUMBER_OF_MATERIALS];

	static { // Tools with higher priorities are produced first by auto production.
		priorityForTool[EMaterialType.AXE.ordinal] = 10;
		priorityForTool[EMaterialType.SAW.ordinal] = 10;
		priorityForTool[EMaterialType.PICK.ordinal] = 10;
		priorityForTool[EMaterialType.SCYTHE.ordinal] = 5;
		priorityForTool[EMaterialType.FISHINGROD.ordinal] = 5;
		priorityForTool[EMaterialType.HAMMER.ordinal] = 1;
		priorityForTool[EMaterialType.BLADE.ordinal] = 1;
	}

	private final PartitionManagerSettings settings = new PartitionManagerSettings();

	private final MovableTypeAcceptor movableTypeAcceptor = new MovableTypeAcceptor();
	private final PositionableList<IManageableBearer> joblessBearer = new PositionableList<IManageableBearer>();
	private final OffersList materialOffers;

	private final MaterialsManager materialsManager;

	private final LinkedList<WorkerRequest> workerRequests = new LinkedList<WorkerRequest>();
	private final PositionableList<IManageableWorker> joblessWorkers = new PositionableList<IManageableWorker>();

	private final LinkedList<DiggerRequest> diggerRequests = new LinkedList<DiggerRequest>();
	private final PositionableList<IManageableDigger> joblessDiggers = new PositionableList<IManageableDigger>();

	private final LinkedList<BricklayerRequest> bricklayerRequests = new LinkedList<BricklayerRequest>();
	private final PositionableList<IManageableBricklayer> joblessBricklayers = new PositionableList<IManageableBricklayer>();

	private final LinkedList<WorkerCreationRequest> workerCreationRequests = new LinkedList<WorkerCreationRequest>();
	private final LinkedList<SoilderCreationRequest> soilderCreationRequests = new LinkedList<SoilderCreationRequest>();

	private boolean stopped = true;

	public PartitionManager(IOffersCountListener offersCountListener) {
		materialOffers = new OffersList(offersCountListener);
		materialsManager = new MaterialsManager(new IJoblessSupplier() {
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
	}

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
		materialOffers.addOffer(position, materialType, false);
	}

	public void addOffer(ShortPoint2D position, EMaterialType materialType, boolean isStockOffer) {
		materialOffers.addOffer(position, materialType, isStockOffer);
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

		removePositionTo(position, this.workerCreationRequests, newManager.workerCreationRequests, newHasSamePlayer);
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
	}

	@Override
	public final int timerEvent() {
		if (stopped) {
			return -1; // unschedule
		}

		materialsManager.distributeJobs();

		handleDiggerRequest();
		handleBricklayerRequest();

		handleWorkerRequest();

		handleWorkerCreationRequests();
		handleSoldierCreationRequest();

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
					createNewToolUser(workerRequest);
				}
				workerRequests.offerLast(workerRequest);
			}
		}
	}

	private void createNewToolUser(WorkerCreationRequest workerCreationRequest) {
		workerCreationRequests.offer(workerCreationRequest);
	}

	private void handleWorkerCreationRequests() {
		for (Iterator<WorkerCreationRequest> iterator = workerCreationRequests.iterator(); iterator.hasNext() && !joblessBearer.isEmpty();) {
			WorkerCreationRequest workerCreationRequest = iterator.next();
			if (!workerCreationRequest.isRequestAlive() || tryToCreateWorker(workerCreationRequest)) {
				iterator.remove();
			}
		}
	}

	private boolean tryToCreateWorker(WorkerCreationRequest workerCreationRequest) {
		EMovableType movableType = workerCreationRequest.requestedMovableType();
		EMaterialType tool = movableType.getTool();

		if (tool != EMaterialType.NO_MATERIAL) { // try to create a worker with a tool
			MaterialOffer offer = this.materialOffers.removeOfferCloseTo(tool, workerCreationRequest.getPos(), false);

			if (offer != null) {
				IManageableBearer manageableBearer = joblessBearer.removeObjectNextTo(offer.getPos());
				if (manageableBearer != null) {
					manageableBearer.becomeWorker(this, workerCreationRequest, offer.getPos());
					return true;

				} else { // no free movable found => return material and add the creation request to the end of the queue
					materialOffers.addOffer(offer.getPos(), tool, offer.isStockOffer());
					return false;
				}

			} else { // no tool found => cannot create worker
				workerCreationRequest.setToolProductionRequired(true);
				return false;
			}

		} else { // create worker without a tool
			IManageableBearer manageableBearer = joblessBearer.removeObjectNextTo(workerCreationRequest.getPos());
			if (manageableBearer != null) {
				manageableBearer.becomeWorker(this, workerCreationRequest);
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void workerCreationRequestFailed(WorkerCreationRequest failedRequest) {
		workerCreationRequests.offer(failedRequest);
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

		if (request.isRequestAlive()) {
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
					createNewToolUser(request);
					request.creationRequested++;
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
		if (bricklayerRequest != null && bricklayerRequest.isRequestAlive()) {
			IManageableBricklayer bricklayer = joblessBricklayers.removeObjectNextTo(bricklayerRequest.getPos());
			if (bricklayer != null) {
				if (!bricklayer.setBricklayerJob(bricklayerRequest.building, bricklayerRequest.bricklayerTargetPos, bricklayerRequest.direction)) {
					bricklayerRequests.add(bricklayerRequest);
				}

			} else if (!bricklayerRequest.isCreationRequested()) { // if the creation hasn't been requested yet => request it.
				createNewToolUser(bricklayerRequest);
				bricklayerRequest.setCreationRequested();
				bricklayerRequests.offerLast(bricklayerRequest);

			} else { // no bricklayer available and creation already requested => nothing to do.
				bricklayerRequests.offerLast(bricklayerRequest);
			}
		}
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

	public void makeStockOffersNormal(ShortPoint2D position, EMaterialType materialType) {
		this.materialOffers.makeStockOffersNormal(position, materialType);
	}

	public final EMaterialType popToolProduction(ShortPoint2D closeTo) {
		byte bestPrio = 0;
		EMaterialType bestTool = null;

		for (WorkerCreationRequest request : workerCreationRequests) { // go through all requests and select the best one
			if (!request.isRequestAlive() || !request.isToolProductionRequired())
				continue; // skip inactive requests and requests not needing a tool production

			request.setToolProductionRequired(false);

			EMaterialType tool = request.requestedMovableType().getTool();
			byte prio = priorityForTool[tool.ordinal];

			if (prio > bestPrio) {
				bestPrio = prio;
				bestTool = tool;
			}
		}

		return bestTool;
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

	public IPartitionSettings getPartitionSettings() {
		return settings;
	}

	public IMaterialCounts getMaterialCounts() {
		return materialOffers;
	}

	public void setMaterialDistributionSettings(EMaterialType materialType, float[] probabilities) {
		settings.getDistributionSettings(materialType).setProbabilities(probabilities);
	}

	public void setMaterialPrioritiesSettings(EMaterialType[] materialTypeForPriority) {
		settings.setMaterialTypesForPriorities(materialTypeForPriority);
	}

	public void setMaterialAcceptedInStock(EMaterialType materialType, boolean acceptedInStock) {
		settings.setMaterialAcceptedInStock(materialType, acceptedInStock);
	}

}
