package jsettlers.graphics.map.selection;

import java.util.Iterator;
import java.util.NoSuchElementException;

import jsettlers.common.selectable.ISelectable;

/**
 * This is an empty selection that contains nothing.
 * 
 * @author michael
 */
public class EmptySelection implements ISelectionSet {

	@Override
	public boolean contains(Object selected) {
		return false;
	}

	@Override
	public Iterator<ISelectable> iterator() {
		return new Iterator<ISelectable>() {

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public ISelectable next() {
				throw new NoSuchElementException();
			}

			@Override
			public boolean hasNext() {
				return false;
			}
		};
	}

	@Override
	public int getSize() {
		return 0;
	}

}
