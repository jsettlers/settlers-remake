package jsettlers.logic.map.newGrid.partition.manager;

import java.util.Iterator;
import java.util.LinkedList;

import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.spawn.Barrack;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.map.newGrid.partition.manager.datastructures.PositionableHashMap;
import jsettlers.logic.map.newGrid.partition.manager.datastructures.PositionableHashMap.IAcceptor;
import jsettlers.logic.map.newGrid.partition.manager.datastructures.PositionableList;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;
import synchronic.timer.INetworkTimerable;
import synchronic.timer.NetworkTimer;

/**
 * This is a manager for a partition. It stores offers, requests and jobless to build up jobs and give them to the jobless.
 * 
 * @author Andreas Eberle
 * 
 */
public class PartitionManager implements INetworkTimerable {
	private final MaterialTypeAcceptor materialTypeAcceptor = new MaterialTypeAcceptor();
	private final MovableTypeAcceptor movableTypeAcceptor = new MovableTypeAcceptor();

	private final PositionableHashMap<Offer> materialOffers = new PositionableHashMap<PartitionManager.Offer>();
	private final LinkedList<Request<EMaterialType>> materialRequests = new LinkedList<PartitionManager.Request<EMaterialType>>();
	private final PositionableList<IManageableBearer> joblessBearer = new PositionableList<IManageableBearer>();

	private final LinkedList<WorkerRequest> workerRequests = new LinkedList<WorkerRequest>();
	private final PositionableList<IManageableWorker> joblessWorkers = new PositionableList<IManageableWorker>();

	private final LinkedList<DiggerRequest> diggerRequests = new LinkedList<PartitionManager.DiggerRequest>();
	private final PositionableList<IManageableDigger> joblessDiggers = new PositionableList<IManageableDigger>();

	private final LinkedList<BricklayerRequest> bricklayerRequests = new LinkedList<PartitionManager.BricklayerRequest>();
	private final PositionableList<IManageableBricklayer> joblessBricklayers = new PositionableList<IManageableBricklayer>();

	private final LinkedList<WorkerCreationRequest> workerCreationRequests = new LinkedList<PartitionManager.WorkerCreationRequest>();
	private final LinkedList<SoilderCreationRequest> soilderCreationRequests = new LinkedList<PartitionManager.SoilderCreationRequest>();

	public PartitionManager() {
		NetworkTimer.schedule(this, (short) 2);
	}

	public void stop() {
		NetworkTimer.remove(this);
	}

	public boolean addOffer(ISPosition2D position, EMaterialType materialType) {
		Offer existingOffer = materialOffers.getObjectAt(position);
		if (existingOffer != null) {
			if (existingOffer.materialType == materialType) {
				existingOffer.amount++;
				return true;
			} else {
				return false;
			}
		} else {
			materialOffers.set(position, new Offer(position, materialType, (byte) 1));
			return true;
		}
	}

	public void request(ISPosition2D position, EMaterialType materialType, byte priority) {
		materialRequests.offer(new Request<EMaterialType>(position, materialType, priority));
	}

	public void requestDiggers(FreeMapArea buildingArea, byte heightAvg, byte amount) {
		diggerRequests.offer(new DiggerRequest(buildingArea, heightAvg, amount));
	}

	public void requestBricklayer(Building building, ShortPoint2D bricklayerTargetPos, EDirection direction) {
		bricklayerRequests.offer(new BricklayerRequest(building, bricklayerTargetPos, direction));
	}

	public void requestBuildingWorker(EMovableType workerType, WorkerBuilding workerBuilding) {
		workerRequests.offer(new WorkerRequest(workerType, workerBuilding));
	}

	public void requestSoilderable(ISPosition2D weaponPosition, Barrack barrack) {
		soilderCreationRequests.offer(new SoilderCreationRequest(weaponPosition, barrack));
	}

	public IManageableBearer removeJobless(ISPosition2D position) {
		return joblessBearer.removeObjectAt(position);
	}

	public void addJobless(IManageableBearer manageable) {
		this.joblessBearer.insert(manageable);
	}

	public void addJobless(IManageableDigger digger) {
		joblessDiggers.insert(digger);
	}

	public void addJobless(IManageableBricklayer bricklayer) {
		joblessBricklayers.insert(bricklayer);
	}

	public void addJobless(IManageableWorker worker) {
		joblessWorkers.insert(worker);
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

		Offer removedOffer = materialOffers.removeObjectAt(position);
		if (removedOffer != null) {// the new manager can not have any offers at that position, because he just occupied it
			newManager.materialOffers.set(position, removedOffer);
		}

		java.util.Iterator<Request<EMaterialType>> requestIter = materialRequests.iterator();
		while (requestIter.hasNext()) {
			Request<EMaterialType> currRequest = requestIter.next();
			if (currRequest.position.equals(position)) {
				requestIter.remove();
				if (newHasSamePlayer) {
					newManager.materialRequests.offer(currRequest);
				} else {
					System.out.println("merge but has not same player");
				}
			}
		}

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
		removePositionTo(position, this.workerCreationRequests, newManager.workerCreationRequests, newHasSamePlayer);
		removePositionTo(position, this.workerRequests, newManager.workerRequests, newHasSamePlayer);
	}

	private <T extends ILocatable> void removePositionTo(ISPosition2D pos, LinkedList<T> fromList, LinkedList<T> toList, boolean newHasSamePlayer) {
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

	/**
	 * 
	 * @param area
	 *            area to be removed from this manager and added to the given manager
	 * @param newManager
	 *            new manager of the given position <br>
	 *            NOTE: the new manager MUST NOT be null!
	 */
	public void removeAreaTo(IMapArea area, PartitionManager newManager) {
		Iterator<Offer> offerIter = materialOffers.iterator();

		while (offerIter.hasNext()) {
			Offer currOffer = offerIter.next();
			if (area.contains(currOffer.position)) {
				// the new manager can not have any offers at that position, because he just occupied it
				newManager.materialOffers.set(currOffer.position, currOffer);
			}
		}

		Iterator<Request<EMaterialType>> requestIter = materialRequests.iterator();
		while (requestIter.hasNext()) {
			if (area.contains(requestIter.next().position)) {
				requestIter.remove();
			}
		}
	}

	@Override
	public void timerEvent() {
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
		WorkerCreationRequest workerRequest = workerCreationRequests.poll();
		if (workerRequest != null) {
			EMaterialType tool = workerRequest.movableType.getTool();
			if (tool != EMaterialType.NO_MATERIAL) {
				this.materialTypeAcceptor.materialType = tool;
				Offer offer = this.materialOffers.getObjectNextTo(workerRequest.position, this.materialTypeAcceptor);
				if (offer != null) {
					IManageableBearer manageableBearer = joblessBearer.removeObjectNextTo(workerRequest.position);
					if (manageableBearer != null) {
						manageableBearer.becomeWorker(workerRequest.movableType, offer.position);
						reduceOfferAmount(offer);
					} else {
						workerCreationRequests.addLast(workerRequest);
					}
				} else {
					workerCreationRequests.addLast(workerRequest);
				}
			} else {
				IManageableBearer manageableBearer = joblessBearer.removeObjectNextTo(workerRequest.position);
				if (manageableBearer != null) {
					manageableBearer.becomeWorker(workerRequest.movableType);
				} else {
					workerCreationRequests.addLast(workerRequest);
				}
			}
		}
	}

	private void handleSoldierCreationRequest() {
		SoilderCreationRequest soilderRequest = soilderCreationRequests.poll();
		if (soilderRequest != null) {
			IManageableBearer manageableBearer = joblessBearer.removeObjectNextTo(soilderRequest.position);
			if (manageableBearer != null) {
				manageableBearer.becomeSoilder(soilderRequest.getWeaponPosition(), soilderRequest.getBarrack());
			} else {
				soilderCreationRequests.addLast(soilderRequest);
			}
		}
	}

	private void handleDiggerRequest() {
		DiggerRequest request = diggerRequests.poll();
		if (request != null) {
			IManageableDigger digger = joblessDiggers.removeObjectNextTo(request.getPos());
			if (digger != null) {
				digger.setDiggerJob(request.buildingArea, request.heightAvg);
				request.amount--;
				request.creationRequested--;
			} else {
				if (request.amount > request.creationRequested) {
					request.creationRequested++;
					createNewTooluser(EMovableType.DIGGER, request.getPos());
				}
			}

			if (request.amount > 0) {
				diggerRequests.addLast(request);
			}
		}
	}

	private void handleBricklayerRequest() {
		BricklayerRequest bricklayerRequest = bricklayerRequests.poll();
		if (bricklayerRequest != null) {
			IManageableBricklayer bricklayer = joblessBricklayers.removeObjectNextTo(bricklayerRequest.getPos());
			if (bricklayer != null) {
				bricklayer.setBricklayerJob(bricklayerRequest.building, bricklayerRequest.bricklayerTargetPos, bricklayerRequest.direction);
			} else {
				if (!bricklayerRequest.creationRequested) {
					bricklayerRequest.creationRequested = true;
					createNewTooluser(EMovableType.BRICKLAYER, bricklayerRequest.getPos());
				}
				bricklayerRequests.offerLast(bricklayerRequest);
			}
		}
	}

	private void handleMaterialRequest() {
		if (!materialRequests.isEmpty()) {
			Request<EMaterialType> request = materialRequests.poll();

			materialTypeAcceptor.materialType = request.requested;
			Offer offer = materialOffers.getObjectNextTo(request.position, materialTypeAcceptor);

			if (offer == null) {
				reofferRequest(request);
			} else {
				IManageableBearer manageable = joblessBearer.removeObjectNextTo(offer.position);

				if (manageable != null) {
					reduceOfferAmount(offer);
					manageable.executeJob(offer.position, request.position, offer.materialType);
				} else {
					reofferRequest(request);
				}
			}
		}
	}

	private void createNewTooluser(EMovableType movableType, ISPosition2D position) {
		workerCreationRequests.addLast(new WorkerCreationRequest(movableType, position));
	}

	private void reduceOfferAmount(Offer offer) {
		offer.amount--;
		if (offer.amount <= 0) {
			materialOffers.removeObjectAt(offer.position);
		}
	}

	private void reofferRequest(Request<EMaterialType> request) {
		// TODO: decrease priority, do something else, ...
		// request.decreasePriority();
		materialRequests.offerLast(request);
	}

	private class Offer {
		ISPosition2D position;
		EMaterialType materialType;
		byte amount = 0;

		public Offer(ISPosition2D position, EMaterialType materialType, byte amount) {
			this.position = position;
			this.materialType = materialType;
			this.amount = amount;
		}

		@Override
		public String toString() {
			return "Offer: " + position + "   " + materialType + "    " + amount;
		}

	}

	private class Request<T> implements Comparable<Request<T>> {
		final ISPosition2D position;
		final T requested;
		byte priority = 100;

		public Request(ISPosition2D position, T requested, byte priority) {
			this.position = position;
			this.requested = requested;
			this.priority = priority;
		}

		@Override
		public int compareTo(Request<T> other) {
			return other.priority - this.priority;
		}

		@Override
		public String toString() {
			return requested + "   " + position + "    " + priority;
		}
	}

	private class DiggerRequest implements ILocatable {
		final FreeMapArea buildingArea;
		final byte heightAvg;
		byte amount;
		byte creationRequested = 0;

		public DiggerRequest(FreeMapArea buildingArea, byte heightAvg, byte amount) {
			this.buildingArea = buildingArea;
			this.heightAvg = heightAvg;
			this.amount = amount;
		}

		@Override
		public ISPosition2D getPos() {
			return buildingArea.get(0);
		}
	}

	private class BricklayerRequest implements ILocatable {
		boolean creationRequested = false;
		final Building building;
		final ShortPoint2D bricklayerTargetPos;
		final EDirection direction;

		public BricklayerRequest(Building building, ShortPoint2D bricklayerTargetPos, EDirection direction) {
			this.building = building;
			this.bricklayerTargetPos = bricklayerTargetPos;
			this.direction = direction;
		}

		@Override
		public final ISPosition2D getPos() {
			return building.getPos();
		}
	}

	private class WorkerCreationRequest implements ILocatable {
		final EMovableType movableType;
		final ISPosition2D position;

		public WorkerCreationRequest(EMovableType movableType, ISPosition2D position) {
			this.movableType = movableType;
			this.position = position;
		}

		@Override
		public String toString() {
			return movableType + "    " + position;
		}

		@Override
		public ISPosition2D getPos() {
			return position;
		}
	}

	private class SoilderCreationRequest implements ILocatable {
		private final ISPosition2D position;
		private final Barrack barrack;

		public SoilderCreationRequest(ISPosition2D position, Barrack barrack) {
			this.position = position;
			this.barrack = barrack;
		}

		public ISPosition2D getWeaponPosition() {
			return this.position;
		}

		@Override
		public String toString() {
			return "SoilderCreationRequest[" + position + "]";
		}

		@Override
		public ISPosition2D getPos() {
			return position;
		}

		public Barrack getBarrack() {
			return barrack;
		}
	}

	private class MaterialTypeAcceptor implements IAcceptor<Offer> {
		EMaterialType materialType = null;

		@Override
		public final boolean isAccepted(Offer offer) {
			return this.materialType == offer.materialType;
		}
	}

	private class MovableTypeAcceptor implements IAcceptor<IManageableWorker> {
		EMovableType movableType = null;

		@Override
		public final boolean isAccepted(IManageableWorker worker) {
			return this.movableType == worker.getMovableType();
		}
	}

	private class WorkerRequest implements ILocatable {
		final EMovableType movableType;
		final IWorkerRequestBuilding building;
		boolean creationRequested = false;

		public WorkerRequest(EMovableType movableType, IWorkerRequestBuilding building) {
			this.building = building;
			this.movableType = movableType;
		}

		@Override
		public ISPosition2D getPos() {
			return building.getDoor();
		}

		@Override
		public String toString() {
			return movableType + "    " + creationRequested + "     " + building.getBuildingType();
		}
	}

}
