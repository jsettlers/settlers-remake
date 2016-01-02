package jsettlers.mapcreator.mapvalidator.tasks;

import java.util.Formatter;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.mapvalidator.result.ValidationList;
import jsettlers.mapcreator.mapvalidator.result.fix.IFix;

/**
 * Base class for validation tasks
 * 
 * @author Andreas Butti
 */
public abstract class AbstractValidationTask {

	/**
	 * Map to check
	 */
	protected MapData data;

	/**
	 * List with the errors
	 */
	private ValidationList list;

	/**
	 * Player data array
	 */
	protected byte[][] players;

	/**
	 * Border array
	 */
	protected boolean[][] borders;

	/**
	 * Constructor
	 */
	public AbstractValidationTask() {
	}

	/**
	 * @param players
	 *            Array with player data REFERENCE, DO NOT clone!
	 */
	public void setPlayers(byte[][] players) {
		this.players = players;
	}

	/**
	 * @param borders
	 *            Array with border data REFERENCE, DO NOT clone!
	 */
	public void setBorders(boolean[][] borders) {
		this.borders = borders;
	}

	/**
	 * @param data
	 *            Map to check
	 */
	public void setData(MapData data) {
		this.data = data;
	}

	/**
	 * @param list
	 *            List with the errors
	 */
	public void setList(ValidationList list) {
		this.list = list;
	}

	/**
	 * Execute the task
	 */
	public abstract void doTest();

	/**
	 * Add a header Text
	 * 
	 * @param textId
	 *            Text id (for translation)
	 * @param fix
	 *            Fix, if any
	 */
	protected void addHeader(String textId, IFix fix) {
		list.addHeader(EditorLabels.getLabel("validation." + textId), fix);
	}

	/**
	 * Add an error message to the list
	 * 
	 * @param textId
	 *            text ID to use
	 * @param pos
	 *            Position
	 * @param parameter
	 *            Parameter to replace in the text (optional)
	 */
	protected void addErrorMessage(String textId, ShortPoint2D pos, Object... parameter) {
		String translatedText = EditorLabels.getLabel("validation." + textId);
		if (parameter.length > 0) {
			translatedText = new Formatter().format(translatedText, parameter).toString();
		}

		list.addError(translatedText, pos, textId);
	}
}
