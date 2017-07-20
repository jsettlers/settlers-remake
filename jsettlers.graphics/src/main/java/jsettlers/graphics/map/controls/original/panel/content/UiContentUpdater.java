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

package jsettlers.graphics.map.controls.original.panel.content;

import static java8.util.stream.StreamSupport.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.position.ShortPoint2D;

public final class UiContentUpdater<T> {
	private static final int UPDATER_INTERVAL = 1000;

	public interface IUiStateProvider<T> {
		T getData(IGraphicsGrid grid, ShortPoint2D position);
	}

	public interface IUiStateListener<T> {
		void update(T uiStateProvider);
	}

	private static Timer timer;
	private TimerTask started;

	private final IUiStateProvider<T> freshDataProvider;

	private T currentData;
	private IGraphicsGrid grid;
	private ShortPoint2D position;

	public UiContentUpdater(IUiStateProvider<T> freshDataProvider) {
		this.freshDataProvider = freshDataProvider;
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

	protected void notifyListeners() {
		synchronized (listeners) {
			// noinspection unchecked
			stream(listeners).forEach(listeners -> listeners.update(currentData));
		}
	}

	public void updatePosition(IGraphicsGrid grid, ShortPoint2D position) {
		this.grid = grid;
		this.position = position;
		updateUi();
	}

	private void updateUi() {
		if (grid == null || position == null) {
			currentData = null;
		} else {
			currentData = freshDataProvider.getData(grid, position);
		}

		notifyListeners();
	}

	public synchronized void start() {
		if (started != null) {
			return;
		}

		started = new TimerTask() {
			@Override
			public void run() {
				updateUi();
			}
		};
		getUITimer().scheduleAtFixedRate(started, 0, UPDATER_INTERVAL);
	}

	/**
	 * Stops the {@link UiContentUpdater}.
	 */
	public synchronized void stop() {
		if (started == null) {
			return;
		}

		started.cancel();
		started = null;
	}

	private static synchronized Timer getUITimer() {
		if (timer == null) {
			timer = new Timer();
		}
		return timer;
	}
}
