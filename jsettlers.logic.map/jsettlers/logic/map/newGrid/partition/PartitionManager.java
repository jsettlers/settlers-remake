package jsettlers.logic.map.newGrid.partition;

import java.util.Iterator;
import java.util.PriorityQueue;

import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ISPosition2D;
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
	private PositionableDatastructure<IManagable> jobless;

	protected PartitionManager() {
		this.offers = new PositionableDatastructure<PartitionManager.Offer>();
		this.requests = new PriorityQueue<PartitionManager.Request>();
		this.jobless = new PositionableDatastructure<IManagable>();

		NetworkTimer.schedule(this, (short) 1);
	}

	public boolean addOffer(ISPosition2D position, EMaterialType materialType) {
		Offer exisitingOffer = offers.getObjectAt(position);
		if (exisitingOffer != null) {
			if (exisitingOffer.materialType == materialType) {
				exisitingOffer.amount++;
				return true;
			} else {
				return false;
			}
		} else {
			offers.set(position, new Offer(position, materialType, (byte) 1));
			return true;
		}
	}

	public void request(ISPosition2D position, EMaterialType materialType, byte priority) {
		requests.offer(new Request(position, materialType, priority));
	}

	public IManagable removeJobless(ISPosition2D position) {
		return jobless.removeObjectAt(position);
	}

	public void addJobless(ISPosition2D position, IManagable managable) {
		this.jobless.set(position, managable);
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
		public Offer(ISPosition2D position, EMaterialType materialType, byte amount) {
			this.position = position;
			this.materialType = materialType;
			this.amount = amount;
		}

		private ISPosition2D position;
		private EMaterialType materialType;
		private byte amount = 0;
	}

	private class Request implements Comparable<Request> {
		public Request(ISPosition2D position, EMaterialType materialType, byte priority) {
			this.position = position;
			this.materialType = materialType;
			this.priority = priority;
		}

		private ISPosition2D position;
		private EMaterialType materialType;
		private byte priority = 1;

		@Override
		public int compareTo(Request other) {
			return this.priority - other.priority;
		}
	}

	@Override
	public void timerEvent() {
		// TODO implement job creation and propagation to jobless
	}
}
