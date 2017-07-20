/*
 * Copyright (c) 2017
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
 */

package jsettlers.graphics.utils;

import static java8.util.stream.StreamSupport.stream;

import java.util.ArrayList;
import java.util.List;

public abstract class UiStateProvider<T extends UiStateProvider> implements UIUpdater.IUpdateReceiver {

	public interface IUiStateListener<T extends UiStateProvider> {
		void update(T uiStateProvider);
	}

	private final ArrayList<IUiStateListener<T>> listeners = new ArrayList<>();

	public void addListener(IUiStateListener<T> listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void addListeners(List<IUiStateListener<T>> listeners) {
		synchronized (this.listeners) {
			this.listeners.addAll(listeners);
		}
	}

	public void removeListener(IUiStateListener<T> listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	public void notifyLiseners() {
		synchronized (listeners) {
			// noinspection unchecked
			stream(listeners).forEach(listeners -> listeners.update((T) this));
		}
	}
}
