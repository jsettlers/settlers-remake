package jsettlers.logic.map.newGrid.partition.manager;

import java.util.Iterator;
import java.util.PriorityQueue;

import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.newGrid.partition.PositionableDatastructure;
import jsettlers.logic.map.newGrid.partition.PositionableDatastructure.IAcceptor;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import synchronic.timer.INetworkTimerable;
import synchronic.timer.NetworkTimer;

/**
 * This is a manager for a partition. It stores offers, requests and jobless to build up jobs and give them to the jobless.
 * 
 * @author Andreas Eberle
 * 
 */
public class PartitionManager implements INetworkTimerable {
	private PositionableDatastructure<Offer> offers;
	private PriorityQueue<Request> requests;
	private PositionableDatastructure<IManageableBearer> jobless;

	public PartitionManager() {
		this.offers = new PositionableDatastructure<PartitionManager.Offer>();
		this.requests = new PriorityQueue<PartitionManager.Request>();
		this.jobless = new PositionableDatastructure<IManageableBearer>();

		NetworkTimer.schedule(this, (short) 1);
	}

	public boolean addOffer(ISPosition2D position, EMaterialType materialType) {
		Offer existingOffer = offers.getObjectAt(position);
		if (existingOffer != null) {
			if (existingOffer.materialType == materialType) {
				existingOffer.amount++;
				return true;
			} else {
				return false;
			}
		} else {
			offers.set(position, new Offer(position, materialType, (byte) 1));
			return true;
		}
	}

	public void request(ISPosition2D position, EMaterialType materialType, byte priority, IMaterialRequester requester) {
		requests.offer(new Request(position, materialType, priority, requester));
	}

	public IManageableBearer removeJobless(ISPosition2D position) {
		return jobless.removeObjectAt(position);
	}

	public void addJobless(IManageableBearer manageable) {
		this.jobless.set(manageable.getPos(), manageable);
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
		Offer removedOffer = offers.removeObjectAt(position);
		if (removedOffer != null) {
			newManager.offers.set(position, removedOffer); // the new manager can not have any offers at that position, because he just occupied it
		}

		java.util.Iterator<Request> requestIter = requests.iterator();
		while (requestIter.hasNext()) {
			if (requestIter.next().position.equals(position)) {
				requestIter.remove();
			}
		}
	}

	/**
	 * 
	 * @param position
	 *            position to be removed from this manager and added to the given manager
	 * @param newManager
	 *            new manager of the given position <br>
	 *            NOTE: the new manager MUST NOT be null!
	 */
	public void removePositionTo(IMapArea area, PartitionManager newManager) {
		Iterator<Offer> offerIter = offers.iterator();

		while (offerIter.hasNext()) {
			Offer currOffer = offerIter.next();
			if (area.contains(currOffer.position)) {
				// the new manager can not have any offers at that position, because he just occupied it
				newManager.offers.set(currOffer.position, currOffer);
			}
		}

		Iterator<Request> requestIter = requests.iterator();
		while (requestIter.hasNext()) {
			if (area.contains(requestIter.next().position)) {
				requestIter.remove();
			}
		}
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

	}

	private class Request implements Comparable<Request> {
		final ISPosition2D position;
		final EMaterialType materialType;
		byte priority = 1;
		final IMaterialRequester requester;

		public Request(ISPosition2D position, EMaterialType materialType, byte priority, IMaterialRequester requester) {
			this.position = position;
			this.materialType = materialType;
			this.priority = priority;
			this.requester = requester;
		}

		@Override
		public int compareTo(Request other) {
			return this.priority - other.priority;
		}

		public void decreasePriority() {
			if (priority > Byte.MIN_VALUE)
				priority--;
		}
	}

	private class MaterialTypeAcceptor implements IAcceptor<Offer> {
		EMaterialType materialType = null;

		@Override
		public final boolean isAccepted(Offer offer) {
			return this.materialType == offer.materialType;
		}
	}

	private class AllAcceptor<T> implements IAcceptor<T> {
		@Override
		public final boolean isAccepted(T object) {
			return true;
		}
	}

	private final MaterialTypeAcceptor materialTypeAcceptor = new MaterialTypeAcceptor();
	private final AllAcceptor<IManageableBearer> manageableAcceptor = new AllAcceptor<IManageableBearer>();

	@Override
	public void timerEvent() {
		if (!requests.isEmpty()) {
			Request request = requests.poll();

			materialTypeAcceptor.materialType = request.materialType;
			Offer offer = offers.getObjectNextTo(request.position, materialTypeAcceptor);

			if (offer == null) {
				reofferRequest(request);
			} else {
				IManageableBearer manageable = jobless.getObjectNextTo(offer.position, manageableAcceptor);

				if (manageable != null) {
					offer.amount--;
					if (offer.amount <= 0) {
						offers.removeObjectAt(offer.position);
					}
					manageable.executeJob(offer.position, request.position, offer.materialType);
				} else {
					reofferRequest(request);
				}
			}
		}

	}

	private void reofferRequest(Request request) {
		request.decreasePriority();
		requests.offer(request);
	}
}
