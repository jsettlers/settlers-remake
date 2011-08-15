package jsettlers.graphics.map.selection;

import jsettlers.common.selectable.ISelectable;

/**
 * This is a selection set that describes a group of things that may be
 * selected.
 * 
 * @author michael
 */
public interface ISelectionSet extends Iterable<ISelectable> {

	/**
	 * Checks whether the given object is selected by this set. TODO: change
	 * type of selected to Selectable?
	 * 
	 * @param selected
	 *            The object to be checked.
	 * @return true if and only if it is selected.
	 */
	boolean contains(Object selected);

	/**
	 * Gives the number of selected elements.
	 * 
	 * @return number of elements selected by this SelectionSet
	 */
	int getSize();
}
