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
package jsettlers.logic.buildings.military;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.ESoldierType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.buildings.WorkAreaBuilding;
import jsettlers.logic.buildings.stack.IRequestStack;
import jsettlers.logic.buildings.stack.IRequestStackListener;
import jsettlers.logic.buildings.stack.RequestStack;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IBarrack;
import jsettlers.logic.player.Player;

/**
 * This is the barrack building. It requests weapons and bearers to make them to soldiers.
 *
 * @author Andreas Eberle
 */
public final class Barrack extends WorkAreaBuilding implements IBarrack, IRequestStackListener {
	private static final long serialVersionUID = -6541972855836598068L;

	public Barrack(Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		super(EBuildingType.BARRACK, player, position, buildingsGrid);
		setOccupied(true);
	}

	@Override
	protected EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

	@Override
	public EMovableType popWeaponForBearer() {
		for (IRequestStack stack : super.getStacks()) {
			EMaterialType materialType = stack.getMaterialType();
			if (materialType == EMaterialType.BOW || materialType == EMaterialType.SWORD || materialType == EMaterialType.SPEAR) {
				if (stack.pop()) {
					return getSoldierType(materialType);
				}
			}
		}

		return null;
	}

	private EMovableType getSoldierType(EMaterialType materialType) {
		return getPlayer().getMannaInformation().getSoldierMovableFor(getSoldierTypeForMaterialType(materialType));
	}

	private ESoldierType getSoldierTypeForMaterialType(EMaterialType materialType) {
		switch (materialType) {
			case SWORD:
				return ESoldierType.SWORDSMAN;
			case BOW:
				return ESoldierType.BOWMAN;
			case SPEAR:
				return ESoldierType.PIKEMAN;
			default:
				throw new IllegalArgumentException("MaterialType: " + materialType + " is not a tool of a soldier.");
		}
	}

	@Override
	public void bearerRequestFailed() {
		super.grid.requestSoldierable(this);
	}

	@Override
	protected int constructionFinishedEvent() {
		for (IRequestStack curr : super.getStacks()) {
			curr.setListener(this);
		}
		return -1;
	}

	@Override
	protected int subTimerEvent() {
		assert false : "This should never be called.";
		return -1;
	}

	@Override
	public ShortPoint2D getSoldierTargetPosition() {
		return super.getWorkAreaCenter();
	}

	@Override
	public void materialDelivered(RequestStack stack) {
		super.grid.requestSoldierable(this);
	}
}
