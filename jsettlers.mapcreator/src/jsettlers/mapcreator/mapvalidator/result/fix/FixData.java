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
