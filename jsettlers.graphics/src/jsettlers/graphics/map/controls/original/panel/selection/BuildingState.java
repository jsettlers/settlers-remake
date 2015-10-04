/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.graphics.map.controls.original.panel.selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.buildings.IBuildingOccupyer;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.movable.IMovable;

/**
 * This class saves the state parts of the building that is displayed by the gui, to detect changes
 * 
 * @author michael
 */
public class BuildingState {

	private final EPriority priority;
	private final EPriority[] supportedPriorities;
	private final ArrayList<StackState> stackStates = new ArrayList<>();
	private boolean construction;

	/**
	 * An array: soldier class -> available places.
	 */
	private final Hashtable<ESoldierClass, ArrayList<OccupierState>> occupierStates;
	private final BitSet stockStates;
	private int[] tradingCounts;
	private boolean isSeaTrading;

	public static class StackState {
		public final EMaterialType type;
		public final int count;
		public final boolean offering;

		public StackState(IBuildingMaterial mat) {
			type = mat.getMaterialType();
			count = mat.getMaterialCount();
			offering = mat.isOffering();
		}

		public boolean isStillInState(IBuildingMaterial mat) {
			return mat.getMaterialType() == type && mat.getMaterialCount() == count && mat.isOffering() == offering;
		}

	}

	public static class OccupierState {
		private final IMovable movable;
		private final boolean comming;

		private OccupierState(IMovable movable) {
			this.movable = movable;
			comming = false;
		}

		private OccupierState(boolean comming) {
			this.comming = comming;
			movable = null;
		}

		public boolean isComming() {
			return comming;
		}

		public boolean isMissing() {
			return movable == null && !isComming();
		}

		public IMovable getMovable() {
			return movable;
		}
	}

	/**
	 * Saves the current state of the building
	 * 
	 * @param building
	 *            the building
	 */
	public BuildingState(IBuilding building) {
		priority = building.getPriority();
		supportedPriorities = building.getSupportedPriorities();
		construction = building.getStateProgress() < 1;
		occupierStates = computeOccupierStates(building);
		stockStates = computeStockStates(building);
		tradingCounts = computeTradingCounts(building);
		for (IBuildingMaterial mat : building.getMaterials()) {
			stackStates.add(new StackState(mat));
		}
		isSeaTrading = building instanceof IBuilding.ITrading && ((IBuilding.ITrading) building).isSeaTrading();
	}

	private int[] computeTradingCounts(IBuilding building) {
		if (building instanceof IBuilding.ITrading) {
			IBuilding.ITrading trading = (IBuilding.ITrading) building;
			int[] counts = new int[EMaterialType.NUMBER_OF_MATERIALS];
			for (EMaterialType m : EMaterialType.DROPPABLE_MATERIALS) {
				counts[m.ordinal] = trading.getRequestedTradingFor(m);
			}
			return counts;
		} else {
			return null;
		}
	}

	private BitSet computeStockStates(IBuilding building) {
		if (building instanceof IBuilding.IStock && !construction) {
			BitSet set = new BitSet();
			IBuilding.IStock stock = (IBuilding.IStock) building;
			for (EMaterialType m : EMaterialType.DROPPABLE_MATERIALS) {
				set.set(m.ordinal, stock.acceptsMaterial(m));
			}
			// TODO: Store the is global flag.
			return set;
		} else {
			return null;
		}
	}

	private Hashtable<ESoldierClass, ArrayList<OccupierState>> computeOccupierStates(IBuilding building) {
		Hashtable<ESoldierClass, ArrayList<OccupierState>> occupierStates = null;
		if (building instanceof IBuilding.IOccupyed && !construction) {
			IBuilding.IOccupyed occupyed = (IBuilding.IOccupyed) building;
			occupierStates = new Hashtable<ESoldierClass, ArrayList<OccupierState>>();
			for (ESoldierClass soldierClass : ESoldierClass.values) {
				occupierStates.put(soldierClass, new ArrayList<OccupierState>());
			}

			for (IBuildingOccupyer o : occupyed.getOccupyers()) {
				ESoldierClass soldierClass = o.getPlace().getSoldierClass();
				OccupierState state = new OccupierState(o.getMovable());
				occupierStates.get(soldierClass).add(state);
			}

			for (ESoldierClass soldierClass : ESoldierClass.values) {
				ArrayList<OccupierState> list = occupierStates.get(soldierClass);
				int comming = occupyed.getCurrentlyCommingSoldiers(soldierClass);
				while (list.size() < comming) {
					list.add(new OccupierState(true));
				}
				int requested = occupyed.getMaximumRequestedSoldiers(soldierClass);
				while (list.size() < requested) {
					list.add(new OccupierState(false));
				}
			}
		}
		return occupierStates;
	}

	public EPriority[] getSupportedPriorities() {
		return supportedPriorities;
	}

	public boolean isConstruction() {
		return construction;
	}

	public ArrayList<StackState> getStackStates() {
		return stackStates;
	}

	public boolean stockAcceptsMaterial(EMaterialType material) {
		return stockStates != null && stockStates.get(material.ordinal);
	}

	public boolean isStillInState(IBuilding building) {
		return building.getPriority() == priority
				&& Arrays.equals(supportedPriorities,
						building.getSupportedPriorities())
				&& construction == (building.getStateProgress() < 1)
				&& hasSameStacks(building)
				&& hasSameOccupiers(building)
				&& hasSameStock(building)
				&& hasSameTrading(building);
	}

	private boolean hasSameTrading(IBuilding building) {
		return isEqual(computeTradingCounts(building), tradingCounts);
	}

	private boolean hasSameStock(IBuilding building) {
		return isEqual(computeStockStates(building), stockStates);
	}

	private boolean hasSameOccupiers(IBuilding building) {
		return isEqual(computeOccupierStates(building), occupierStates);
	}

	private static boolean isEqual(Object o1, Object o2) {
		return o1 == o2 || (o1 != null && o1.equals(o2));
	}

	private boolean hasSameStacks(IBuilding building) {
		List<IBuildingMaterial> materials = building.getMaterials();
		if (materials.size() != stackStates.size()) {
			return false;
		}
		int i = 0;
		for (IBuildingMaterial mat : materials) {
			if (stackStates.get(i++).isStillInState(mat)) {
				return false;
			}
		}
		return true;
	}

	public List<OccupierState> getOccupiers(ESoldierClass soldierClass) {
		return Collections.unmodifiableList(occupierStates.get(soldierClass));
	}

	public int getTradingCount(EMaterialType material) {
		return tradingCounts == null ? 0 : tradingCounts[material.ordinal];
	}

	public boolean isOccupied() {
		return occupierStates != null && !construction;
	}

	public boolean isStock() {
		return stockStates != null && !construction;
	}

	public boolean isTrading() {
		return tradingCounts != null && !construction;
	}

	public boolean isSeaTrading() {
		return isSeaTrading;
	}
}
