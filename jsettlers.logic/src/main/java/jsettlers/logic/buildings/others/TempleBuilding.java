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
package jsettlers.logic.buildings.others;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.buildings.stack.IRequestStack;
import jsettlers.logic.player.Player;

import java.util.List;

/**
 *
 * 
 * @author Andreas Eberle
 * 
 */
public final class TempleBuilding extends Building {
	private static final long serialVersionUID = 1L;

	private static final int CHECK_DELAY = 1500;
	private static final int CONSUME_DELAY = 30000;

	public TempleBuilding(Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		super(EBuildingType.TEMPLE, player, position, buildingsGrid);
	}

	@Override
	public boolean isOccupied() {
		return true;
	}

	@Override
	protected int subTimerEvent() {
		IRequestStack wineStack = getWineStack();

		if (wineStack.pop()) {
			getPlayer().getMannaInformation().increaseManna();
			return CONSUME_DELAY;
		} else {
			return CHECK_DELAY;
		}
	}

	private IRequestStack getWineStack() {
		List<? extends IRequestStack> stacks = super.getStacks();
		assert stacks.size() == 1;

		IRequestStack wineStack = stacks.get(0);
		assert wineStack.getMaterialType() == EMaterialType.WINE;
		return wineStack;
	}

	@Override
	protected int constructionFinishedEvent() {
		super.grid.getMapObjectsManager().addWineBowl(super.getDoor(), getWineStack());
		return CHECK_DELAY;
	}

	@Override
	protected EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

	@Override
	protected void killedEvent() {
		ShortPoint2D door = super.getDoor();
		super.grid.getMapObjectsManager().removeMapObjectType(door.x, door.y, EMapObjectType.WINE_BOWL);
	}
}
