package jsettlers.logic.map.newGrid.partition.manager;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.map.newGrid.partition.manager.datastructures.PositionableHashMap;
import jsettlers.logic.map.newGrid.partition.manager.datastructures.PositionableHashMap.IAcceptor;
import jsettlers.logic.map.newGrid.partition.manager.datastructures.PositionableList;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;
import synchronic.timer.INetworkTimerable;
import synchronic.timer.NetworkTimer;

/**
 * This is a manager for a partition. It stores offers, requests and jobless to build up jobs and give them to the jobless.
 * 
 * @author Andreas Eberle
 * 
 */
public class PartitionManager implements INetworkTimerable {
	private PositionableHashMap<Offer> materialOffers = new PositionableHashMap<PartitionManager.Offer>();
	private PriorityQueue<Request<EMaterialType>> materialRequests = new PriorityQueue<PartitionManager.Request<EMaterialType>>();
	private PositionableList<IManageableBearer> joblessBearer = new PositionableList<IManageableBearer>();

	private PriorityQueue<Request<EMovableType>> workerRequests = new PriorityQueue<PartitionManager.Request<EMovableType>>();
	private PositionableList<IManageableWorker> joblessWorkers = new PositionableList<IManageableWorker>();

	private LinkedList<DiggerRequest> diggerRequests = new LinkedList<PartitionManager.DiggerRequest>();
	private PositionableList<IManageableDigger> joblessDiggers = new PositionableList<IManageableDigger>();

	private LinkedList<BricklayerRequest> bricklayerRequests = new LinkedList<PartitionManager.BricklayerRequest>();
	private PositionableList<IManageableBricklayer> joblessBricklayers = new PositionableList<IManageableBricklayer>();

	private LinkedList<WorkerCreationRequest> workerCreationRequests = new LinkedList<PartitionManager.WorkerCreationRequest>();

	public PartitionManager() {
		NetworkTimer.schedule(this, (short) 2);
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

	public IManageableBearer removeJobless(ISPosition2D position) {
		return joblessBearer.removeObjectAt(position);
	}

	public void addJobless(IManageableBearer manageable) {
		this.joblessBearer.insert(manageable);
	}

	public void addJobless(IManageableDigger digger) {
		joblessDiggers.insert(digger);
	}

	/**
	 * 
	 * @param position
	 *            position to be removed from this manager and added to the given manager
	 * @param newManager
	 *            new manager of the given position <br>
	 *            NOTE: the new manager MUST NOT be null!
	 */
	public void removePositionTo(ISPosition2D position, PartitionManager newManager) {
		Offer removedOffer = materialOffers.removeObjectAt(position);
		if (removedOffer != null) {
			newManager.materialOffers.set(position, removedOffer); // the new manager can not have any offers at that position, because he just
																	// occupied it
		}

		java.util.Iterator<Request<EMaterialType>> requestIter = materialRequests.iterator();
		while (requestIter.hasNext()) {
			if (requestIter.next().position.equals(position)) {
				requestIter.remove();
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

	private final MaterialTypeAcceptor materialTypeAcceptor = new MaterialTypeAcceptor();

	@Override
	public void timerEvent() {
		handleMaterialRequest();

		handleWorkerCreationRequest();

		handleDiggerRequest();
	}

	private void handleWorkerCreationRequest() {
		WorkerCreationRequest workerRequest = workerCreationRequests.poll();
		if (workerRequest != null) {
			EMaterialType tool = workerRequest.movableType.getTool();
			if (tool != null) {
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

	private void handleDiggerRequest() {
		DiggerRequest request = diggerRequests.poll();
		if (request != null) {
			IManageableDigger digger = joblessDiggers.removeObjectNextTo(request.getPosition());
			if (digger != null) {
				digger.setDiggerJob(request.buildingArea, request.heightAvg);
				request.amount--;
				request.creationRequested--;
			} else {
				if (request.amount > request.creationRequested) {
					request.creationRequested++;
					createNewTooluser(EMovableType.DIGGER, request.getPosition());
				} else {
					diggerRequests.addLast(request);
				}
			}

			if (request.amount > 0) {
				diggerRequests.addLast(request);
			}
		}
	}

	private void createNewTooluser(EMovableType movableType, ISPosition2D position) {
		workerCreationRequests.addLast(new WorkerCreationRequest(movableType, position));
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

	private void reduceOfferAmount(Offer offer) {
		offer.amount--;
		if (offer.amount <= 0) {
			materialOffers.removeObjectAt(offer.position);
		}
	}

	private void reofferRequest(Request<EMaterialType> request) {
		request.decreasePriority();
		materialRequests.add(request);
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
		byte priority = 1;

		public Request(ISPosition2D position, T requested, byte priority) {
			this.position = position;
			this.requested = requested;
			this.priority = priority;
		}

		@Override
		public int compareTo(Request<T> other) {
			return other.priority - this.priority;
		}

		public void decreasePriority() {
			if (priority > Byte.MIN_VALUE)
				priority--;
		}
	}

	private class DiggerRequest {
		private final FreeMapArea buildingArea;
		private final byte heightAvg;
		private byte amount;
		private byte creationRequested = 0;

		public DiggerRequest(FreeMapArea buildingArea, byte heightAvg, byte amount) {
			this.buildingArea = buildingArea;
			this.heightAvg = heightAvg;
			this.amount = amount;
		}

		public ISPosition2D getPosition() {
			return buildingArea.get(0);
		}
	}

	private class BricklayerRequest {
		private final Building building;
		private final ShortPoint2D bricklayerTargetPos;
		private final EDirection direction;

		public BricklayerRequest(Building building, ShortPoint2D bricklayerTargetPos, EDirection direction) {
			this.building = building;
			this.bricklayerTargetPos = bricklayerTargetPos;
			this.direction = direction;
		}

		public final ISPosition2D getPosition() {
			return building.getPos();
		}
	}

	private class WorkerCreationRequest {
		private final EMovableType movableType;
		private final ISPosition2D position;

		public WorkerCreationRequest(EMovableType movableType, ISPosition2D position) {
			this.movableType = movableType;
			this.position = position;
		}
	}

	private class MaterialTypeAcceptor implements IAcceptor<Offer> {
		EMaterialType materialType = null;

		@Override
		public final boolean isAccepted(Offer offer) {
			return this.materialType == offer.materialType;
		}
	}

}
