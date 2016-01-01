package jsettlers.mapcreator.mapvalidator.tasks;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.mapvalidator.result.ValidationList;

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
	 * Constructor
	 */
	public AbstractValidationTask() {
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
	 * Add an error message to the list
	 * 
	 * @param message
	 *            Error message
	 * @param pos
	 *            Error position
	 */
	protected void testFailed(String message, ShortPoint2D pos) {
		list.addError(message, pos);
	}

}
