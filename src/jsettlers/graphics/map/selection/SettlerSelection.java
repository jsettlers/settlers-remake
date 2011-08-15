package jsettlers.graphics.map.selection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.selectable.ISelectable;

/**
 * This is a selection of any type of settlers.
 * <p>
 * TODO: change it to/add special selections for soilders, pioneers, ...?
 * 
 * @author michael
 */
public class SettlerSelection implements ISelectionSet {
	private int[] workerCount = new int[EMovableType.values().length];
	private int[] bearerMaterials = new int[EMaterialType.values().length];
	private final List<ISelectable> selectables = new ArrayList<ISelectable>();

	/**
	 * Creates a new selection of movables.
	 * 
	 * @param movables
	 *            The movables the selection should contain.
	 */
	public SettlerSelection(List<? extends IMovable> movables) {
		for (IMovable movable : movables) {
			// if (!(movable instanceof ISelectable)) {
			// throw new
			// IllegalArgumentException("Only selectable movables may be added to the selection.");
			// }
			this.selectables.add(movable);

			EMovableType type = movable.getMovableType();
			this.workerCount[type.ordinal()]++;

			if (type == EMovableType.BEARER) {
				this.bearerMaterials[movable.getMaterial().ordinal()]++;
			}
		}
	}

	@Override
	public boolean contains(Object selected) {
		return this.selectables.contains(selected);
	}

	@Override
	public Iterator<ISelectable> iterator() {
		return this.selectables.iterator();
	}

	/**
	 * gets the number of movables with that type.
	 * 
	 * @param type
	 *            The type
	 * @return The number of movables with that type.
	 */
	public int getMovableCount(EMovableType type) {
		return this.workerCount[type.ordinal()];
	}

	/**
	 * Counts how many of the selected bearers port a given material.
	 * 
	 * @param type
	 *            The type
	 * @return The number of settlers having that material assigned.
	 */
	public int getMaterialCount(EMaterialType type) {
		return this.bearerMaterials[type.ordinal()];
	}

	@Override
	public int getSize() {
		return selectables.size();
	}

}
