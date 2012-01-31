package jsettlers.input;

/**
 * Interface for TaskExecutor to give commands to the GuiInterface using the executor.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ITaskExecutorGuiInterface {

	/**
	 * refresh the current selection, because it's possible, that movables changed.
	 */
	void refreshSelection();

}
