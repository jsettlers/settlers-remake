package jsettlers.newmain.datatypes;

import java.util.List;

import jsettlers.graphics.startscreen.interfaces.IChangingList;
import jsettlers.graphics.startscreen.interfaces.IChangingListListener;

/**
 * This class implements the {@link IChangingList} interface and represents a list that can change and will inform it's listener of this change.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
public class ChangingList<T> implements IChangingList<T> {

	private List<T> items;
	private IChangingListListener<T> listener;

	public ChangingList() {
	}

	public ChangingList(List<T> items) {
		this.items = items;
	}

	@Override
	public synchronized void setListener(IChangingListListener<T> listener) {
		this.listener = listener;
	}

	@Override
	public List<T> getItems() {
		return items;
	}

	@Override
	public void stop() {
		listener = null;
		items = null;
	}

	public void setList(List<T> items) {
		this.items = items;
		informListener();
	}

	private synchronized void informListener() {
		if (listener != null) {
			listener.listChanged(this);
		}
	}

}
