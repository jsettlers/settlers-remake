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
package jsettlers.mapcreator.control;

import java.util.LinkedList;

import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.data.MapDataDelta;
import jsettlers.mapcreator.main.window.EditorFrame;

/**
 * Handle Undo / Redo actions
 * 
 * @author Andreas Butti
 *
 */
public class UndoRedoHandler {

	/**
	 * Max undo counts, The data is not compressed, limit max undo count to not run out of memory
	 */
	private static final int MAX_UNDO = 100;

	/**
	 * Undo stack
	 */
	private final LinkedList<MapDataDelta> undoDeltas = new LinkedList<>();

	/**
	 * Redo stack
	 */
	private final LinkedList<MapDataDelta> redoDeltas = new LinkedList<>();

	/**
	 * Flag to indicate changes since last save
	 */
	private boolean changedSinceLastSave = true;

	/**
	 * Window displayed
	 */
	private final EditorFrame window;

	/**
	 * Map data
	 */
	private final MapData data;

	/**
	 * Constructor
	 * 
	 * @param data
	 *            Map data
	 * @param window
	 *            Window displayed
	 */
	public UndoRedoHandler(EditorFrame window, MapData data) {
		this.window = window;
		this.data = data;
	}

	/**
	 * Undo the last action, if possible
	 */
	public void undo() {
		if (!undoDeltas.isEmpty()) {
			MapDataDelta delta = undoDeltas.pollLast();

			MapDataDelta inverse = data.apply(delta);

			redoDeltas.addLast(inverse);
		}
		updateMenuAndToolbar();

		changedSinceLastSave = true;
	}

	/**
	 * Redo the last action, if possible
	 */
	public void redo() {
		if (!redoDeltas.isEmpty()) {
			MapDataDelta delta = redoDeltas.pollLast();

			MapDataDelta inverse = data.apply(delta);

			undoDeltas.addLast(inverse);
		}

		updateMenuAndToolbar();

		changedSinceLastSave = true;
	}

	/**
	 * Ends a use step of a tool: creates a diff to the last step.
	 */
	public void endUseStep() {
		MapDataDelta delta = data.getUndoDelta();
		data.resetUndoDelta();

		if (undoDeltas.size() >= MAX_UNDO) {
			undoDeltas.removeFirst();
		}
		undoDeltas.add(delta);
		redoDeltas.clear();

		updateMenuAndToolbar();

		changedSinceLastSave = true;
	}

	/**
	 * Activate / deactivate menu / toolbar
	 */
	public void updateMenuAndToolbar() {
		window.enableAction("undo", !undoDeltas.isEmpty());

		// allow save always, can be later changed if all is really good tested
		window.enableAction("save", true);

		window.enableAction("redo", !redoDeltas.isEmpty());
	}

	/**
	 * Check if changed since last save
	 * 
	 * @return true if changed
	 */
	public boolean isChangedSinceLastSave() {
		return changedSinceLastSave;
	}

	/**
	 * Set the saved flag
	 */
	public void setSaved() {
		changedSinceLastSave = false;
	}
}
