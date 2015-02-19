package jsettlers.input;

import jsettlers.graphics.map.UIState;

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

	/**
	 * 
	 * @return Returns the current {@link UIState} that can be used to save it with a savegame.
	 */
	UIState getUIState();

}
