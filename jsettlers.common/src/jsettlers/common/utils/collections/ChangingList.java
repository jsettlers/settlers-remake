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

import java.util.Collections;
import java.util.List;

/**
 * This class implements the {@link IChangingList} interface and represents a list that can change and will inform it's listener of this change.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
public class ChangingList<T> {

	private List<? extends T> items;
	private IChangingListListener<T> listener;

	public ChangingList() {
		this(Collections.<T> emptyList());
	}

	public ChangingList(List<? extends T> items) {
		setList(items);
	}

	public synchronized void setListener(IChangingListListener<T> listener) {
		this.listener = listener;
	}

	public List<? extends T> getItems() {
		return items;
	}

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
