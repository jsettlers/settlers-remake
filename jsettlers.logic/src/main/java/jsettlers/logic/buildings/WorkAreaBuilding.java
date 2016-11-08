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
package jsettlers.logic.buildings;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.player.Player;

/**
 * A {@link Building} that has a work area that can be changed and drawn to the screen.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class WorkAreaBuilding extends Building {
	private static final long serialVersionUID = -5176169656248971550L;

	private static final EPriority[] SUPPORTED_PRIORITIES_FOR_REQUESTERS = new EPriority[] { EPriority.LOW, EPriority.HIGH, EPriority.STOPPED };
	private static final EPriority[] SUPPORTED_PRIORITIES_FOR_NON_REQUESTERS = new EPriority[] { EPriority.LOW, EPriority.STOPPED };

	private ShortPoint2D workAreaCenter;

	private boolean cannotWork = false;

	protected WorkAreaBuilding(EBuildingType type, Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		super(type, player, position, buildingsGrid);
	}

	@Override
	protected void placedAtEvent(ShortPoint2D pos) {
		this.workAreaCenter = getBuildingType().getDefaultWorkcenter().calculatePoint(pos);
	}

	public final ShortPoint2D getWorkAreaCenter() {
		return workAreaCenter;
	}

	@Override
	public final void setWorkAreaCenter(ShortPoint2D newWorkAreaCenter) {
		int distance = super.pos.getOnGridDistTo(newWorkAreaCenter);

		if (distance < Constants.BUILDINGS_MAX_WORKRADIUS_FACTOR * super.getBuildingType().getWorkRadius()) {
			if (isSelected()) {
				drawWorkAreaCircle(false);
			}

			this.workAreaCenter = newWorkAreaCenter;

			if (isSelected()) {
				drawWorkAreaCircle(true);
			}

			this.cannotWork = false; // reset cannotWork, we need to check it in the new work are first.
		}
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		drawWorkAreaCircle(selected);
	}

	@Override
	public void kill() {
		if (workAreaCenter != null && isSelected()) {
			drawWorkAreaCircle(false);
		}

		super.kill();
	}

	private void drawWorkAreaCircle(boolean draw) {
		super.grid.drawWorkAreaCircle(super.pos, workAreaCenter, super.getBuildingType().getWorkRadius(), draw);
	}

	@Override
	public EPriority[] getSupportedPriorities() {
		if (isConstructionFinished()) {
			if (getBuildingType().getRequestStacks().length == 0) { // has no request stacks
				return SUPPORTED_PRIORITIES_FOR_NON_REQUESTERS;
			} else {
				return SUPPORTED_PRIORITIES_FOR_REQUESTERS;
			}
		} else {
			return super.getSupportedPriorities();
		}
	}

	@Override
	public boolean cannotWork() {
		return cannotWork;
	}

	public void setCannotWork(boolean cannotWork) {
		this.cannotWork = cannotWork;
	}
}
