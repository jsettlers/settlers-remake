package jsettlers.graphics.startscreen.interfaces;

import java.util.List;

public interface IChangingList<T> {
	/**
	 * Sets a listener to notify when the list was changed.
	 * 
	 * @param listener
	 */
	public void setListener(IChangingListListener<T> listener);

	/**
	 * Gets the current state of the list.
	 * 
	 * @return A list that is not modified after returning it and contains all
	 *         current items of the list.
	 */
	public List<T> getItems();

	/**
	 * Called when the list is not needed any more.
	 */
	public void stop();
}
