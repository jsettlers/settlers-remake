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
package jsettlers.logic.movable.strategies;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IBarrack;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IMaterialOffer;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IMaterialRequest;
import jsettlers.logic.map.grid.partition.manager.materials.offers.EOfferPriority;
import jsettlers.logic.map.grid.partition.manager.objects.WorkerCreationRequest;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;

/**
 * Strategy for bearers.
 *
 * @author Andreas Eberle
 *
 */
public final class BearerMovableStrategy extends MovableStrategy implements IManageableBearer {
	private static final long serialVersionUID = -734268451796522451L;

	private EBearerState state = EBearerState.JOBLESS;

	private IMaterialOffer   offer;
	private IMaterialRequest request;
	private EMaterialType    materialType;

	private IBarrack              barrack;
	private IWorkerRequester      workerRequester;
	private WorkerCreationRequest workerCreationRequest;

	public BearerMovableStrategy(Movable movable) {
		super(movable);
		reportJobless();
	}

	public final void reportJobless() {
		super.getGrid().addJobless(this);
	}

	@Override
	protected void action() {
		switch (state) {
		case JOBLESS:
			break;

		case INIT_CONVERT_WITH_TOOL_JOB:
		case INIT_CARRY_JOB:
			state = EBearerState.GOING_TO_OFFER;

			if (!movable.getPosition().equals(offer.getPosition())) { // if we are not at the offers position, go to it.
				if (!super.goToPos(offer.getPosition())) {
					handleJobFailed(true);
				}
				break;
			}
		case GOING_TO_OFFER:
			if (movable.getPosition().equals(offer.getPosition())) {
				state = EBearerState.TAKING;
				if (!super.take(materialType, true)) {
					handleJobFailed(true);
				}
			} else {
				handleJobFailed(true);
			}
			break;

		case TAKING:
			if (workerCreationRequest != null) { // we handle a convert with tool job
				state = EBearerState.DEAD_OBJECT;
				super.setMaterial(EMaterialType.NO_MATERIAL);
				movable.convertTo(workerCreationRequest.requestedMovableType());
			} else {
				offer = null;
				state = EBearerState.GOING_TO_REQUEST;
				if (!movable.getPosition().equals(request.getPosition()) && !super.goToPos(request.getPosition())) {
					handleJobFailed(true);
				}
			}
			break;

		case GOING_TO_REQUEST:
			if (movable.getPosition().equals(request.getPosition())) {
				state = EBearerState.DROPPING;
				super.drop(materialType);
			} else {
				handleJobFailed(true);
			}
			break;

		case DROPPING:
			request = null;
			materialType = null;
			state = EBearerState.JOBLESS;
			reportJobless();
			break;

		case INIT_CONVERT_JOB:
			state = EBearerState.DEAD_OBJECT;
			movable.convertTo(workerCreationRequest.requestedMovableType());
			break;

		case INIT_BECOME_SOLDIER_JOB:
			super.goToPos(barrack.getDoor());
			state = EBearerState.GOING_TO_BARRACK;
			break;

		case GOING_TO_BARRACK:
			EMovableType movableType = barrack.popWeaponForBearer();
			if (movableType == null) { // weapon got missing, make this bearer jobless again
				this.barrack = null;
				this.state = EBearerState.JOBLESS;
				reportJobless();
			} else {
				this.state = EBearerState.DEAD_OBJECT;
				movable.convertTo(movableType);
				super.goToPos(barrack.getSoldierTargetPosition());
				movable.getPlayer().getEndgameStatistic().incrementAmountOfProducedSoldiers();
			}
			break;

		case DEAD_OBJECT:
			assert false : "we should never get here!";
		}
	}

	protected void tookMaterial() {
		if (offer != null) {
			offer.offerTaken();
		}
	}

	@Override
	public boolean droppingMaterial() {
		if (request != null) {
			if (request.isActive() && request.getPosition().equals(movable.getPosition())) {
				request.deliveryFulfilled();
				request = null;
				return false;
			} else {
				request.deliveryAborted();
				request = null;
			}
		}
		return true; // offer the material
	}

	private void handleJobFailed(boolean reportAsJobless) {
		switch (state) {
		case INIT_CARRY_JOB:
		case GOING_TO_OFFER:
		case TAKING:
			if (super.movable.getMaterial() == EMaterialType.NO_MATERIAL) {
				reoffer();
			}
			if (workerCreationRequest != null) {
				workerRequester.workerCreationRequestFailed(workerCreationRequest);
			}
		case GOING_TO_REQUEST:
			if (request != null) {
				request.deliveryAborted();
			}
			break;

		case DROPPING:
			if (request != null) {
				boolean offerMaterial = droppingMaterial();
				super.setMaterial(EMaterialType.NO_MATERIAL);
				super.getGrid().dropMaterial(movable.getPosition(), materialType, offerMaterial, false);
			}
			break;

		case INIT_BECOME_SOLDIER_JOB:
		case GOING_TO_BARRACK:
			barrack.bearerRequestFailed();
			break;

		case INIT_CONVERT_WITH_TOOL_JOB:
			reoffer();
		case INIT_CONVERT_JOB:
			workerRequester.workerCreationRequestFailed(workerCreationRequest);
			break;

		case DEAD_OBJECT:
			break;
		case JOBLESS:
			break;
		default:
			break;
		}

		EMaterialType carriedMaterial = super.setMaterial(EMaterialType.NO_MATERIAL);
		if (carriedMaterial != EMaterialType.NO_MATERIAL) {
			super.getGrid().dropMaterial(movable.getPosition(), materialType, true, false);
		}

		offer = null;
		request = null;
		materialType = null;
		workerCreationRequest = null;
		workerRequester = null;
		state = EBearerState.JOBLESS;

		if (reportAsJobless) {
			reportJobless();
		}
	}

	private void reoffer() {
		offer.distributionAborted();
	}

	@Override
	protected boolean checkPathStepPreconditions(ShortPoint2D pathTarget, int step) {
		if (request != null && !request.isActive()) {
			return false;
		}

		if (offer != null) {
			EOfferPriority minimumAcceptedPriority = request != null ? request.getMinimumAcceptedOfferPriority() : EOfferPriority.LOWEST;
			if (!offer.isStillValid(minimumAcceptedPriority)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void deliver(EMaterialType materialType, IMaterialOffer offer, IMaterialRequest request) {
		if (state == EBearerState.JOBLESS) {
			this.offer = offer;
			this.request = request;
			this.materialType = materialType;
			this.state = EBearerState.INIT_CARRY_JOB;

			offer.distributionAccepted();
			request.deliveryAccepted();
		}
	}

	@Override
	public boolean becomeWorker(IWorkerRequester requester, WorkerCreationRequest workerCreationRequest) {
		if (state == EBearerState.JOBLESS) {
			this.workerRequester = requester;
			this.workerCreationRequest = workerCreationRequest;
			this.state = EBearerState.INIT_CONVERT_JOB;
			this.offer = null;
			this.materialType = null;

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean becomeWorker(IWorkerRequester requester, WorkerCreationRequest workerCreationRequest, IMaterialOffer offer) {
		if (state == EBearerState.JOBLESS) {
			this.workerRequester = requester;
			this.workerCreationRequest = workerCreationRequest;
			this.offer = offer;
			this.state = EBearerState.INIT_CONVERT_WITH_TOOL_JOB;
			this.materialType = workerCreationRequest.requestedMovableType().getTool();

			offer.distributionAccepted();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean becomeSoldier(IBarrack barrack) {
		if (state == EBearerState.JOBLESS) {
			this.barrack = barrack;
			this.state = EBearerState.INIT_BECOME_SOLDIER_JOB;

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void strategyKilledEvent(ShortPoint2D pathTarget) {
		if (state == EBearerState.JOBLESS) {
			super.getGrid().removeJobless(this);
		} else {
			handleJobFailed(false);
		}
		state = EBearerState.DEAD_OBJECT;
	}

	@Override
	protected void pathAborted(ShortPoint2D pathTarget) {
		if (state != EBearerState.JOBLESS) {
			handleJobFailed(true);
		}
	}

	/**
	 * This enum defines the internal states of a bearer.
	 *
	 * @author Andreas Eberle
	 *
	 */
	private enum EBearerState {
		JOBLESS,

		INIT_CARRY_JOB,
		GOING_TO_REQUEST,
		GOING_TO_OFFER,
		TAKING,
		DROPPING,

		INIT_CONVERT_JOB,
		INIT_CONVERT_WITH_TOOL_JOB,

		DEAD_OBJECT,

		INIT_BECOME_SOLDIER_JOB,
		GOING_TO_BARRACK,
	}

}
