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
package jsettlers.mapcreator.mapvalidator.result.fix;

import jsettlers.mapcreator.control.UndoRedoHandler;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.mapvalidator.MapValidator;

/**
 * Class contains all data to execute the fixes
 * 
 * @author Andreas Butti
 *
 */
public class FixData {

	/**
	 * The Map
	 */
	private final MapData map;

	/**
	 * Undo / Redo stack
	 */
	private final UndoRedoHandler undoRedo;

	/**
	 * Validates the map for errors
	 */
	private final MapValidator validator;

	/**
	 * Constructor
	 * 
	 * @param map
	 *            The Map
	 * @param undoRedo
	 *            Undo / Redo stack
	 * @param validator
	 *            Validates the map for errors
	 */
	public FixData(MapData map, UndoRedoHandler undoRedo, MapValidator validator) {
		this.map = map;
		this.undoRedo = undoRedo;
		this.validator = validator;
	}

	/**
	 * @return The map
	 */
	public MapData getMap() {
		return map;
	}

	/**
	 * @return Undo / Redo stack
	 */
	public UndoRedoHandler getUndoRedo() {
		return undoRedo;
	}

	/**
	 * @return Validates the map for errors
	 */
	public MapValidator getValidator() {
		return validator;
	}

}
