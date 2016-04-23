/*******************************************************************************
 * Copyright (c) 2015 - 2016
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
package jsettlers.mapcreator.main.window;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import jsettlers.logic.map.loading.MapLoader;

/**
 * Panel to open one of the last used maps
 * 
 * @author Andreas Butti
 *
 */
public class LastUsedPanel extends OpenPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param doubleclickListener
	 *            Gets called when an entry is double clicked, can be <code>null</code>
	 */
	public LastUsedPanel(ActionListener doubleclickListener) {
		super(doubleclickListener);
	}

	/**
	 * Order the maps
	 */
	@Override
	protected void sortMaps() {
		LastUsedHandler lastUsed = new LastUsedHandler();

		List<MapLoader> mapsCopy = maps;
		maps = new ArrayList<>();

		for (String id : lastUsed.getLastUsed()) {
			for (MapLoader m : mapsCopy) {
				if (m.getMapId() != null && m.getMapId().equals(id)) {
					maps.add(m);
					break;
				}
			}
		}
	}

	/**
	 * @return true if there is at least one file
	 */
	public boolean hasFiles() {
		return !maps.isEmpty();
	}
}
