package jsettlers.graphics.map.selection;

import java.util.Iterator;
import java.util.NoSuchElementException;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.selectable.ISelectable;

public class OneBuildingIterator implements Iterator<ISelectable> {
	private boolean asked = false;
	private final IBuilding building;

	public OneBuildingIterator(IBuilding building) {
		this.building = building;
	}

	@Override
	public boolean hasNext() {
		return !this.asked;
	}

	@Override
	public ISelectable next() {
		if (this.asked) {
			throw new NoSuchElementException();
		} else {
			this.asked = true;
			return this.building;
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
