package jsettlers.newmain.datatypes;

import java.util.Collections;
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

	private List<? extends T> items;
	private IChangingListListener<T> listener;

	public ChangingList() {
		this(Collections.<T>emptyList());
	}

	public ChangingList(List<? extends T> items) {
		setList(items);
	}

	@Override
	public synchronized void setListener(IChangingListListener<T> listener) {
		this.listener = listener;
	}

	@Override
	public List<? extends T> getItems() {
		return items;
	}

	@Override
	public void stop() {
		listener = null;
		items = Collections.emptyList();
	}

	public void setList(List<? extends T> items) {
		if (items == null) {
			throw new NullPointerException();
		}
		this.items = items;
		informListener();
	}

	private synchronized void informListener() {
		if (listener != null) {
			listener.listChanged(this);
		}
	}

}
