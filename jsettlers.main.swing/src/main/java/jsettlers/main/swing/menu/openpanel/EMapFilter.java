/*******************************************************************************
 * Copyright (c) 2016
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
package jsettlers.main.swing.menu.openpanel;

import jsettlers.graphics.localization.Labels;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.original.OriginalMapLoader;
import jsettlers.logic.map.loading.newmap.FreshMapLoader;

/**
 * Map filter for OpenPanel
 * 
 * @author Andreas Butti
 */
public enum EMapFilter {

	/**
	 * No filtering
	 */
	ALL {
		@Override
		public boolean filter(MapLoader loader) {
			return true;
		}
	},

	/**
	 * JSettler Maps
	 */
	JSETTLER {
		@Override
		public boolean filter(MapLoader loader) {
			return loader instanceof FreshMapLoader;
		}
	},

	/**
	 * Mapps imported from Original settler
	 */
	SETTLER_IMPORTED {
		@Override
		public boolean filter(MapLoader loader) {
			return loader instanceof OriginalMapLoader;
		}
	};

	/**
	 * Translated name
	 */
	private final String name;

	/**
	 * Constructor
	 */
	EMapFilter() {
		this.name = Labels.getString("mapfilter." + name());
	}

	/**
	 * Filter a single map entry
	 * 
	 * @param loader
	 *            Map to filter
	 * @return true if it should be displayed
	 */
	public abstract boolean filter(MapLoader loader);

	/**
	 * @return Translated name
	 */
	public String getName() {
		return name;
	}
}
