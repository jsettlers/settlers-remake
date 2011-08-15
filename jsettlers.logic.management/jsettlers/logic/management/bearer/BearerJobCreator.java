package jsettlers.logic.management.bearer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ILocatable;
import jsettlers.logic.management.MaterialJobPart;
import jsettlers.logic.management.bearer.job.BearerCarryJob;
import jsettlers.logic.management.bearer.job.BearerToWorkerJob;
import jsettlers.logic.management.bearer.job.BearerToWorkerWithMaterialJob;
import synchronic.timer.INetworkTimerable;
import synchronic.timer.NetworkTimer;

/**
 * manager that creates jobs from material and movable requests and material offers.
 * 
 * @author Andreas Eberle
 * 
 */
public class BearerJobCreator implements INetworkTimerable {
	private static final int NUMBER_OF_MATERIALS = EMaterialType.values().length;

	@SuppressWarnings("unchecked")
	private List<MaterialJobPart>[] offers = new List[NUMBER_OF_MATERIALS];
	@SuppressWarnings("unchecked")
	private List<MaterialJobPart>[] materialRequests = new List[NUMBER_OF_MATERIALS];
	private List<EMovableType> movableRequests = Collections.synchronizedList(new LinkedList<EMovableType>());

	private final BearerJobCenter jobCenter;

	public BearerJobCreator(BearerJobCenter jobCenter) {
		this.jobCenter = jobCenter;
		for (int i = 0; i < NUMBER_OF_MATERIALS; i++) {
			offers[i] = Collections.synchronizedList(new LinkedList<MaterialJobPart>());
			materialRequests[i] = Collections.synchronizedList(new LinkedList<MaterialJobPart>());
		}
	}

	public void start() {
		NetworkTimer.schedule(this, (short) 10);
	}

	/**
	 * this method lets the JobCreator add one Material of the stack's type to it's known offers.
	 * 
	 * @param stack
	 *            OfferStack offering a Material
	 */
	public void offer(MaterialJobPart stack) {
		offers[stack.getMaterialType().ordinal()].add(stack);
	}

	public void requestMaterial(MaterialJobPart request) {
		materialRequests[request.getMaterialType().ordinal()].add(request);
	}

	public void requestMovable(EMovableType requestedMovable) {
		movableRequests.add(requestedMovable);
	}

	@Override
	public void timerEvent() {
		try {
			List<MaterialJobPart> currOffers;
			List<MaterialJobPart> currRequests;

			for (int i = 0; i < NUMBER_OF_MATERIALS; i++) {
				currOffers = offers[i];
				currRequests = materialRequests[i];

				if (!currOffers.isEmpty() && !currRequests.isEmpty()) {
					MaterialJobPart offer;
					MaterialJobPart request;

					// choose resources next to each other
					if (currOffers.size() > currRequests.size()) {// go throw the smaller list to save CPU-time
						offer = currOffers.remove(0);
						int nearestIdx = ILocatable.Methods.getNearest(currRequests, offer.getPos());
						request = currRequests.remove(nearestIdx);
					} else {
						request = currRequests.remove(0);
						int nearestIdx = ILocatable.Methods.getNearest(currOffers, request.getPos());
						offer = currOffers.remove(nearestIdx);
					}

					BearerCarryJob job = new BearerCarryJob(offer, request);
					jobCenter.addCarryJob(job);
				}
			}

			if (!movableRequests.isEmpty()) {
				EMovableType movRequest = movableRequests.remove(0);

				if (movRequest.getTool() == EMaterialType.NO_MATERIAL) {
					jobCenter.addToWorkerJob(new BearerToWorkerJob(movRequest));
				} else {
					if (offers[movRequest.getTool().ordinal()].isEmpty()) {
						movableRequests.add(movRequest);
					} else {
						BearerToWorkerJob job = new BearerToWorkerWithMaterialJob(offers[movRequest.getTool().ordinal()].remove(0), movRequest);
						jobCenter.addToWorkerJob(job);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cancel() {
		NetworkTimer.remove(this);
	}

}
