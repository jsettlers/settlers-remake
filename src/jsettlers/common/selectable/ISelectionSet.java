package jsettlers.common.selectable;

import jsettlers.common.movable.EMovableType;

/**
 * Interface for sets of selectables.<br>
 * A class of this interface can only be of one {@link ESelectionType}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ISelectionSet extends Iterable<ISelectable> {

	/**
	 * Checks whether the given object is selected by this set.
	 * 
	 * @param selected
	 *            The selectable to be checked.
	 * @return true if and only if it is selected.
	 */
	boolean contains(ISelectable selectable);

	/**
	 * Gives the number of selected elements.
	 * 
	 * @return number of elements selected by this SelectionSet
	 */
	int getSize();

	/**
	 * 
	 * @return {@link ESelectionType} of this selection set
	 */
	ESelectionType getSelectionType();

	/**
	 * counts the movables in the set of the given type.
	 * 
	 * @param type
	 * @return
	 */
	int getMovableCount(EMovableType type);

	/**
	 * gives the selected at given index.
	 * 
	 * @param idx
	 * @return
	 */
	ISelectable get(int idx);
}
