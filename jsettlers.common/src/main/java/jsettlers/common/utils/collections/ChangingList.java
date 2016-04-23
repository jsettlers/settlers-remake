/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.common.utils.collections;

import java.util.LinkedList;
import java.util.List;

/**
 * This class implements the {@link IChangingList} interface and represents a list that can change and will inform it's listener of this change.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
public class ChangingList<T> {

	private List<T> items;
	private IChangingListListener<? super T> listener;

	public ChangingList() {
		this.items = new LinkedList<>();
	}

	public ChangingList(List<T> items) {
		this.items = items;
	}

	public void setListener(IChangingListListener<? super T> listener) {
		this.listener = listener;
	}

	public void removeListener(IChangingListListener<? super T> listener) {
		if (this.listener == listener) {
			this.listener = null;
		}
	}

	public List<T> getItems() {
		return items;
	}

	public void stop() {
		listener = null;
		items = new LinkedList<>();
	}

	public void setList(List<T> items) {
		if (items == null) {
			throw new NullPointerException();
		}
		this.items = items;
		notifyListener();
	}

	private synchronized void notifyListener() {
		if (listener != null) {
			listener.listChanged(this);
		}
	}

	public void clear() {
		if (items != null) {
			items.clear();
			notifyListener();
		}
	}

	public void add(T item) {
		items.add(item);
		notifyListener();
	}

	public void remove(T item) {
		items.remove(item);
		notifyListener();
	}

	@Override
	public String toString() {
		return items == null ? "[]" : items.toString();
	}
}
