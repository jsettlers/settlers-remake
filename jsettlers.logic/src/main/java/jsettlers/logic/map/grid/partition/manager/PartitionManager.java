/*******************************************************************************
 * Copyright (c) 2015 - 2017
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

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.map.grid.partition.manager.settings.MaterialProductionSettings;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.map.grid.partition.data.MaterialCounts;
import jsettlers.logic.map.grid.partition.manager.datastructures.PositionableList;
import jsettlers.logic.map.grid.partition.manager.datastructures.PredicatedPositionableList;
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
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IOfferEmptiedListener;
import jsettlers.logic.map.grid.partition.manager.materials.offers.EOfferPriority;
import jsettlers.logic.map.grid.partition.manager.materials.offers.IOffersCountListener;
import jsettlers.logic.map.grid.partition.manager.materials.offers.MaterialOffer;
import jsettlers.logic.map.grid.partition.manager.materials.offers.OffersList;
import jsettlers.logic.map.grid.partition.manager.materials.requests.MaterialRequestObject;
import jsettlers.logic.map.grid.partition.manager.objects.BricklayerRequest;
import jsettlers.logic.map.grid.partition.manager.objects.DiggerRequest;
import jsettlers.logic.map.grid.partition.manager.objects.SoldierCreationRequest;
import jsettlers.logic.map.grid.partition.manager.objects.WorkerCreationRequest;
import jsettlers.logic.map.grid.partition.manager.objects.WorkerRequest;
import jsettlers.logic.map.grid.partition.manager.settings.PartitionManagerSettings;
import jsettlers.logic.timer.IScheduledTimerable;
import jsettlers.logic.timer.RescheduleTimer;

/**
 * This is a manager for a partition. It stores offers, requests and jobless to build up jobs and give them to the jobless.
 *
 * @author Andreas Eberle
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

	private final PositionableList<IManageableBearer> joblessBearer = new PositionableList<>();
	private final OffersList materialOffers;

	private final MaterialsManager materialsManager;

	private final LinkedList<WorkerRequest> workerRequests = new LinkedList<>();
	private final PredicatedPositionableList<IManageableWorker> joblessWorkers = new PredicatedPositionableList<>();

	private final LinkedList<DiggerRequest> diggerRequests = new LinkedList<>();
	private final PositionableList<IManageableDigger> joblessDiggers = new PositionableList<>();

	private final LinkedList<BricklayerRequest> bricklayerRequests = new LinkedList<>();
	private final PositionableList<IManageableBricklayer> joblessBricklayers = new PositionableList<>();

	private final LinkedList<WorkerCreationRequest> workerCreationRequests = new LinkedList<>();
	private final LinkedList<SoldierCreationRequest> soldierCreationRequests = new LinkedList<>();

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

	public MaterialProductionSettings getMaterialProduction() {
		return settings.getMaterialProductionSettings();
	}

	public void addOffer(ShortPoint2D position, EMaterialType materialType, EOfferPriority offerPriority) {
		materialOffers.addOffer(position, materialType, offerPriority);
	}

	public void addOffer(ShortPoint2D position, EMaterialType materialType, EOfferPriority offerPriority, IOfferEmptiedListener offerListener) {
		materialOffers.addOffer(position, materialType, offerPriority, offerListener);
	}

	public void updateOfferPriority(ShortPoint2D position, EMaterialType materialType, EOfferPriority newPriority) {
		materialOffers.updateOfferPriority(position, materialType, newPriority);
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

	public void requestSoldierable(IBarrack barrack) {
		soldierCreationRequests.offer(new SoldierCreationRequest(barrack));
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
	 * 		x coordinate of the position to be removed from this manager and added to the given manager
	 * @param y
	 * 		y coordinate of the position to be removed from this manager and added to the given manager
	 * @param newManager
	 * 		new manager of the given position <br>
	 * 		NOTE: the new manager MUST NOT be null!
	 * @param newHasSamePlayer
	 * 		Specifies if the new manager has the same player. If so, requests also need to be moved.
	 */
	public void removePositionTo(final int x, final int y, PartitionManager newManager, boolean newHasSamePlayer) {
		ShortPoint2D position = new ShortPoint2D(x, y);

		materialOffers.moveOffersAtPositionTo(position, newManager.materialOffers);

		if (newHasSamePlayer) {
			materialsManager.movePositionTo(position, newManager.materialsManager);

			IManageableBearer bearer = joblessBearer.removeObjectAt(position);
			if (bearer != null) {
				newManager.addJobless(bearer);
			}
			IManageableBricklayer bricklayer = joblessBricklayers.removeObjectAt(position);
			if (bricklayer != null) {
				newManager.addJobless(bricklayer);
			}
			IManageableDigger digger = joblessDiggers.removeObjectAt(position);
			if (digger != null) {
				newManager.addJobless(digger);
			}
			IManageableWorker worker = joblessWorkers.removeObjectAt(position);
			if (worker != null) {
				newManager.addJobless(worker);
			}
		}

		removePositionTo(position, this.workerCreationRequests, newManager.workerCreationRequests, newHasSamePlayer);
		removePositionTo(position, this.bricklayerRequests, newManager.bricklayerRequests, newHasSamePlayer);
		removePositionTo(position, this.diggerRequests, newManager.diggerRequests, newHasSamePlayer);
		removePositionTo(position, this.workerRequests, newManager.workerRequests, newHasSamePlayer);
		removePositionTo(position, this.soldierCreationRequests, newManager.soldierCreationRequests, newHasSamePlayer);
	}

	private <T extends ILocatable> void removePositionTo(ShortPoint2D pos, LinkedList<T> fromList, LinkedList<T> toList, boolean newHasSamePlayer) {
		Iterator<T> iter = fromList.iterator();
		while (iter.hasNext()) {
			T curr = iter.next();
			if (curr.getPosition().equals(pos)) {
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
		newManager.joblessBearer.moveAll(this.joblessBearer);
		newManager.joblessBricklayers.moveAll(this.joblessBricklayers);
		newManager.joblessDiggers.moveAll(this.joblessDiggers);
		newManager.joblessWorkers.moveAll(this.joblessWorkers);
		newManager.materialOffers.moveAll(this.materialOffers);
		this.materialsManager.mergeInto(newManager.materialsManager);
		newManager.soldierCreationRequests.addAll(this.soldierCreationRequests);
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
			IManageableWorker worker = joblessWorkers.removeObjectNextTo(workerRequest.getPosition(), currentWorker -> currentWorker.getMovableType() == workerRequest.movableType);

			if (worker != null && worker.isAlive()) {
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
		for (Iterator<WorkerCreationRequest> iterator = workerCreationRequests.iterator(); iterator.hasNext() && !joblessBearer.isEmpty(); ) {
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
			MaterialOffer offer = materialOffers.getOfferCloseTo(tool, EOfferPriority.LOWEST, workerCreationRequest.getPosition());

			if (offer != null) {
				IManageableBearer manageableBearer = joblessBearer.removeObjectNextTo(offer.getPosition());
				if (manageableBearer != null) {
					return manageableBearer.becomeWorker(this, workerCreationRequest, offer);

				} else { // no free movable found => cannot create worker
					return false;
				}

			} else { // no tool found => cannot create worker
				workerCreationRequest.setToolProductionRequired(true);
				return false;
			}

		} else { // create worker without a tool
			IManageableBearer manageableBearer = joblessBearer.removeObjectNextTo(workerCreationRequest.getPosition());
			if (manageableBearer != null) {
				return manageableBearer.becomeWorker(this, workerCreationRequest);
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
		SoldierCreationRequest soilderRequest = soldierCreationRequests.poll();
		if (soilderRequest != null) {
			IManageableBearer manageableBearer = joblessBearer.removeObjectNextTo(soilderRequest.getPosition());
			if (manageableBearer == null || !manageableBearer.becomeSoldier(soilderRequest.getBarrack())) {
				soldierCreationRequests.addLast(soilderRequest);
			}
		}
	}

	private void handleDiggerRequest() {
		DiggerRequest request = diggerRequests.peek();
		if (request == null) {
			return;
		}

		if (request.isRequestAlive()) {
			IManageableDigger digger = joblessDiggers.removeObjectNextTo(request.getPosition());
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
			IManageableBricklayer bricklayer = joblessBricklayers.removeObjectNextTo(bricklayerRequest.getPosition());
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

	public final EMaterialType popToolProduction(ShortPoint2D closeTo) {
		byte bestPrio = 0;
		EMaterialType bestTool = null;

		for (WorkerCreationRequest request : workerCreationRequests) { // go through all requests and select the best one
			if (!request.isRequestAlive() || !request.isToolProductionRequired()) {
				continue; // skip inactive requests and requests not needing a tool production
			}

			request.setToolProductionRequired(false); // FIXME @andreas-eberle: Investigate if this is correct! And why it doesn't break the system

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
	 * @param position
	 * 		position to look for the offer
	 * @param materialType
	 * 		type of material of the offer
	 * @param offerPriority
	 * 		offerPriority of the offer
	 * @return Returns the offer at the given position of given materialType and offerPriority or <code>null</code>
	 */
	public MaterialOffer getMaterialOfferAt(ShortPoint2D position, EMaterialType materialType, EOfferPriority offerPriority) {
		return this.materialOffers.getOfferObjectAt(position, materialType, offerPriority);
	}

	public PartitionManagerSettings getPartitionSettings() {
		return settings;
	}

	public MaterialCounts getMaterialCounts() {
		return materialOffers.getMaterialCounts();
	}
}
