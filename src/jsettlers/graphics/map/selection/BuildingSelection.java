package jsettlers.graphics.map.selection;

import java.util.Iterator;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.selectable.ISelectable;

/**
 * This is a building selection.
 * 
 * @author michael
 */
public class BuildingSelection implements ISelectionSet {

	private final IBuilding building;

	/**
	 * Creates a new selection for the building.
	 * 
	 * @param building
	 *            The selected building.
	 */
	public BuildingSelection(IBuilding building) {
		this.building = building;
	}

	/**
	 * Gets the building contained by this selection.
	 * 
	 * @return The selected building.
	 */
	public IBuilding getSelectedBuilding() {
		return this.building;
	}

	@Override
	public boolean contains(Object selected) {
		return selected == this.building;
	}

	@Override
	public Iterator<ISelectable> iterator() {
		return new OneBuildingIterator(this.building);
	}

	@Override
	public int getSize() {
		return 1;
	}

}
