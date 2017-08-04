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

package jsettlers.graphics.map.controls.original.panel.content.updaters;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.position.ShortPoint2D;

public class UiLocationDependingContentUpdater<T> extends UiContentUpdater<T> {

	public interface IUiLocationDependingContentProvider<T> {
		T getData(IGraphicsGrid grid, ShortPoint2D position);
	}

	private final IUiLocationDependingContentProvider<T> freshDataProvider;

	private IGraphicsGrid grid;
	private ShortPoint2D position;

	public UiLocationDependingContentUpdater(IUiLocationDependingContentProvider<T> freshDataProvider) {
		this.freshDataProvider = freshDataProvider;
	}

	public void updatePosition(IGraphicsGrid grid, ShortPoint2D position) {
		this.grid = grid;
		this.position = position;
		updateUi();
	}

	@Override
	protected T getUpdatedData() {
		if (grid != null && position != null) {
			return freshDataProvider.getData(grid, position);
		} else {
			return null;
		}
	}

	public ShortPoint2D getPosition() {
		return position;
	}
}
