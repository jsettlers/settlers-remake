/*******************************************************************************
 * Copyright (c) 2015, 2016
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

import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.EGoInDirectionMode;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class FleeStrategy extends MovableStrategy {
	private static final long serialVersionUID = -7693464085159449304L;

	private int searchesCounter = 0;
	private boolean turnNextTime;

	private int lastCheckedPathStep;
	private byte pathStepCheckedCounter;

	public FleeStrategy(Movable movable) {
		super(movable);
	}

	@Override
	protected void action() {
		ShortPoint2D position = movable.getPosition();
		if (!super.isValidPosition(position)) {
			if (searchesCounter > 120) {
				movable.kill();
				return;
			}

			if (super.preSearchPath(true, position.x, position.y, Constants.MOVABLE_FLEEING_DIJKSTRA_RADIUS, ESearchType.VALID_FREE_POSITION)
					|| super.preSearchPath(false, position.x, position.y, Constants.MOVABLE_FLEEING_MAX_RADIUS, ESearchType.VALID_FREE_POSITION)) {
				lastCheckedPathStep = Integer.MIN_VALUE;
				super.followPresearchedPath();
			} else {
				EDirection currentDirection = movable.getDirection();
				EDirection newDirection;
				if (turnNextTime || MatchConstants.random().nextFloat() < 0.10) {
					turnNextTime = false;
					newDirection = currentDirection.getNeighbor(MatchConstants.random().nextInt(-1, 1));
				} else {
					newDirection = currentDirection;
				}

				ShortPoint2D newPos = newDirection.getNextHexPoint(position);

				if (super.goInDirection(newDirection, EGoInDirectionMode.GO_IF_FREE)) {
					turnNextTime = MatchConstants.random().nextInt(7) == 0;
				} else {
					super.lookInDirection(newDirection);
					turnNextTime = true;
				}
			}

			searchesCounter++;
		} else {
			movable.convertTo(movable.getMovableType());
		}
	}

	@Override
	protected boolean checkPathStepPreconditions(ShortPoint2D pathTarget, int step) {
		if (lastCheckedPathStep == step) {
			pathStepCheckedCounter++;
			searchesCounter++;
		} else {
			pathStepCheckedCounter = 0;
			lastCheckedPathStep = (short) step;
		}

		return !super.isValidPosition(movable.getPosition()) && pathStepCheckedCounter < 5;
	}
}
