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
	private final LinkedList<MapDataDelta> undoDeltas = new LinkedList<MapDataDelta>();

	/**
	 * Redo stack
	 */
	private final LinkedList<MapDataDelta> redoDeltas = new LinkedList<MapDataDelta>();

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
			window.enableAction("redo", true);
		}
		if (undoDeltas.isEmpty()) {
			window.enableAction("undo", false);
			window.enableAction("save", false);
		}
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
			window.enableAction("undo", true);
			window.enableAction("save", true);
		}
		if (redoDeltas.isEmpty()) {
			window.enableAction("redo", false);
		}
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
		window.enableAction("undo", true);
		window.enableAction("redo", false);

		window.enableAction("save", true);
		changedSinceLastSave = true;
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
