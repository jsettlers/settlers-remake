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
	 * get the selection type.
	 * 
	 * @return {@link ESelectionType}
	 */
	ESelectionType getSelectionType();
}
