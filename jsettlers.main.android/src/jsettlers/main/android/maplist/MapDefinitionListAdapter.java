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
package jsettlers.main.android.maplist;

import java.util.Comparator;
import java.util.List;

import jsettlers.common.utils.collections.ChangingList;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import android.view.LayoutInflater;

public class MapDefinitionListAdapter<T extends IMapDefinition> extends MapListAdapter<T> {

	private final List<? extends T> maps;

	public MapDefinitionListAdapter(LayoutInflater inflater, ChangingList<? extends T> changingList) {
		super(inflater, changingList);
		this.maps = changingList.getItems();
	}

	@Override
	public String getTitle(T map) {
		String title = map.getMapName();
		return title;
	}

	@Override
	protected short[] getImage(T map) {
		return map.getImage();
	}

	@Override
	protected String getDescriptionString(T map) {
		return "";
	}

	@Override
	public boolean isEmpty() {
		return maps.isEmpty();
	}

	@Override
	protected Comparator<? super IMapDefinition> getDefaultComparator() {
		return IMapDefinition.MAP_NAME_COMPARATOR;
	}
}
