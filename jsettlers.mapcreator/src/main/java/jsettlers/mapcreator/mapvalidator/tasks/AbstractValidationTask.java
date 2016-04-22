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
package jsettlers.mapcreator.mapvalidator.tasks;

import java.util.Formatter;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.mapvalidator.result.ValidationList;
import jsettlers.mapcreator.mapvalidator.result.fix.AbstractFix;

/**
 * Base class for validation tasks, have to be inserted in the validation list in #ValidatorRunnable
 * 
 * @author Andreas Butti
 */
public abstract class AbstractValidationTask {

	/**
	 * Map to check
	 */
	protected MapData data;

	/**
	 * Map header
	 */
	protected MapFileHeader header;

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
	 * @param header
	 *            Map header
	 */
	public void setHeader(MapFileHeader header) {
		this.header = header;
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
	protected void addHeader(String textId, AbstractFix fix) {
		list.addHeader(EditorLabels.getLabel("validation." + textId), fix);
	}

	/**
	 * Add a warning message to the list
	 * 
	 * @param textId
	 *            text ID to use
	 * @param pos
	 *            Position
	 * @param parameter
	 *            Parameter to replace in the text (optional)
	 */
	protected void addWarningMessage(String textId, ShortPoint2D pos, Object... parameter) {
		addErrorWarningMessage(null, textId, false, pos, parameter);
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
		addErrorWarningMessage(null, textId, true, pos, parameter);
	}

	/**
	 * Add a warning message to the list
	 * 
	 * @param additionalErrorData
	 *            Used for special cases... Can be anything, needs a special implementation in the sidebar also
	 * @param textId
	 *            text ID to use
	 * @param pos
	 *            Position
	 * @param parameter
	 *            Parameter to replace in the text (optional)
	 */
	protected void addWarningMessage(Object additionalErrorData, String textId, ShortPoint2D pos, Object... parameter) {
		addErrorWarningMessage(additionalErrorData, textId, false, pos, parameter);
	}

	/**
	 * Add an error message to the list
	 * 
	 * @param additionalErrorData
	 *            Used for special cases... Can be anything, needs a special implementation in the sidebar also
	 * @param textId
	 *            text ID to use
	 * @param pos
	 *            Position
	 * @param parameter
	 *            Parameter to replace in the text (optional)
	 */
	protected void addErrorMessage(Object additionalErrorData, String textId, ShortPoint2D pos, Object... parameter) {
		addErrorWarningMessage(additionalErrorData, textId, true, pos, parameter);
	}

	/**
	 * Add an error or warning message to the list
	 * 
	 * @param additionalErrorData
	 *            Used for special cases... Can be anything, needs a special implementation in the sidebar also
	 * @param textId
	 *            text ID to use
	 * @param error
	 *            true for error, false for warning
	 * @param pos
	 *            Position
	 * @param parameter
	 *            Parameter to replace in the text (optional)
	 */
	private void addErrorWarningMessage(Object additionalErrorData, String textId, boolean error, ShortPoint2D pos, Object... parameter) {
		String translatedText = EditorLabels.getLabel("validation." + textId);
		if (parameter.length > 0) {
			translatedText = new Formatter().format(translatedText, parameter).toString();
		}

		list.addError(additionalErrorData, translatedText, error, pos, textId);
	}
}
