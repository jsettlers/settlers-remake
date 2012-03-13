package jsettlers.common.selectable;

/**
 * This interface defines something that can be selected and therefore has an selection status.
 * 
 * @author michael
 */
public interface ISelectable {
	/**
	 * Returns whether this object is currently selected.
	 * <p>
	 * This information is not used by the interface to show the object in the UI, you also have to set a {@link SelectionSet} in the interface
	 * connector.
	 * 
	 * @return The selection state of the item.
	 */
	boolean isSelected();

	/**
	 * Sets the selection flag of the given item.
	 * 
	 * @param selected
	 *            The selection status.
	 */
	void setSelected(boolean selected);

	/**
	 * Lets this selectable stop or start its work.
	 * 
	 * @param stop
	 *            if true this selectable should stop working<br>
	 *            if false, it should stop working.
	 */
	void stopOrStartWorking(boolean stop);

	/**
	 * get the selection type.
	 * 
	 * @return {@link ESelectionType}
	 */
	ESelectionType getSelectionType();
}
