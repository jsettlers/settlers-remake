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
package jsettlers.logic.movable.strategies.specialists;

import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.movable.EGoInDirectionMode;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public final class PioneerStrategy extends MovableStrategy {
	private static final long serialVersionUID = 1L;

	private static final float ACTION1_DURATION = 1.2f;

	private EPioneerState state = EPioneerState.JOBLESS;
	private ShortPoint2D centerPos;

	public PioneerStrategy(Movable movable) {
		super(movable);
	}

	@Override
	protected void action() {
		switch (state) {
		case JOBLESS:
			return;

		case GOING_TO_POS:
			if (centerPos == null) {
				this.centerPos = movable.getPosition();
			}

			if (canWorkOnPos(movable.getPosition())) {
				super.playAction(EMovableAction.ACTION1, ACTION1_DURATION);
				state = EPioneerState.WORKING_ON_POS;
			} else {
				findWorkablePosition();
			}
			break;

		case WORKING_ON_POS:
			if (canWorkOnPos(movable.getPosition())) {
				executeAction(movable.getPosition());
			}

			findWorkablePosition();
			break;
		}
	}

	private void findWorkablePosition() {
		EDirection closeForeignTileDir = getCloseForeignTile();

		if (closeForeignTileDir != null && super.goInDirection(closeForeignTileDir, EGoInDirectionMode.GO_IF_ALLOWED_AND_FREE)) {
			this.state = EPioneerState.GOING_TO_POS;
			return;
		}
		centerPos = null;

		ShortPoint2D pos = movable.getPosition();
		if (super.preSearchPath(true, pos.x, pos.y, (short) 30, ESearchType.UNENFORCED_FOREIGN_GROUND)) {
			super.followPresearchedPath();
			this.state = EPioneerState.GOING_TO_POS;
		} else {
			this.state = EPioneerState.JOBLESS;
		}
	}

	private EDirection getCloseForeignTile() {
		EDirection[] bestNeighbourDir = new EDirection[1];
		double[] bestNeighbourDistance = new double[] { Double.MAX_VALUE }; // distance from start point

		ShortPoint2D position = movable.getPosition();
		HexGridArea.stream(position.x, position.y, 1, 6)
				.filter((x, y) -> super.isValidPosition(x, y) && canWorkOnPos(x, y))
				.forEach((x, y) -> {
					double distance = ShortPoint2D.getOnGridDist(x - centerPos.x, y - centerPos.y);
					if (distance < bestNeighbourDistance[0]) {
						bestNeighbourDistance[0] = distance;
						bestNeighbourDir[0] = EDirection.getApproxDirection(position.x, position.y, x, y);
					}
				});
		return bestNeighbourDir[0];
	}

	private void executeAction(ShortPoint2D pos) {
		super.getGrid().changePlayerAt(pos, movable.getPlayer());
	}

	private boolean canWorkOnPos(int x, int y) {
		return super.fitsSearchType(x, y, ESearchType.UNENFORCED_FOREIGN_GROUND);
	}

	private boolean canWorkOnPos(ShortPoint2D pos) {
		return super.fitsSearchType(pos, ESearchType.UNENFORCED_FOREIGN_GROUND);
	}

	@Override
	protected void moveToPathSet(ShortPoint2D oldPosition, ShortPoint2D oldTargetPos, ShortPoint2D targetPos) {
		this.state = EPioneerState.GOING_TO_POS;
		centerPos = null;
	}

	@Override
	protected boolean canBeControlledByPlayer() {
		return true;
	}

	@Override
	protected void stopOrStartWorking(boolean stop) {
		if (stop) {
			state = EPioneerState.JOBLESS;
		} else {
			state = EPioneerState.GOING_TO_POS;
		}
	}

	@Override
	protected void pathAborted(ShortPoint2D pathTarget) {
		state = EPioneerState.JOBLESS;
	}

	/**
	 * Internal state of a {@link PioneerStrategy}.
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	private enum EPioneerState {
		JOBLESS,
		GOING_TO_POS,
		WORKING_ON_POS
	}

}
