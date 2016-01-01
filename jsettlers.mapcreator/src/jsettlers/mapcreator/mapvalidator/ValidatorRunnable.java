package jsettlers.mapcreator.mapvalidator;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.mapvalidator.result.ValidationList;
import jsettlers.mapcreator.mapvalidator.tasks.AbstractValidationTask;
import jsettlers.mapcreator.mapvalidator.tasks.ValidateAll;

/**
 * The validation runnable running in the thread queue
 * 
 * @author Andreas Butti
 */
public class ValidatorRunnable implements Runnable {

	/**
	 * List with the errors
	 */
	private ValidationList list = new ValidationList();

	/**
	 * Listener for validation result
	 */
	private ValidationResultListener resultListener;

	/**
	 * Validation tasks
	 */
	private List<AbstractValidationTask> tasks = new ArrayList<>();

	/**
	 * Map to check
	 */
	private final MapData data;

	/**
	 * Constructor
	 * 
	 * @param resultListener
	 *            Listener for validation result
	 * @param data
	 *            Map to check
	 */
	public ValidatorRunnable(ValidationResultListener resultListener, MapData data) {
		this.resultListener = resultListener;
		this.data = data;

		registerTask(new ValidateAll());
	}

	/**
	 * Register a task to be executed
	 * 
	 * @param task
	 *            Task
	 */
	private void registerTask(AbstractValidationTask task) {
		tasks.add(task);
		task.setData(data);
		task.setList(list);
	}

	@Override
	public void run() {
		list.addHeader("REAL");

		for (AbstractValidationTask t : tasks) {
			t.doTest();
		}

		list.prepareToDisplay();
		fireResult();
	}

	/**
	 * Fire the result to the listener
	 */
	private void fireResult() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				resultListener.validationFinished(list);
			}
		});
	}

}
