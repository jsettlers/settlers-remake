package jsettlers.main.android.core;

import jsettlers.main.android.core.controls.ControlsAdapter;
import jsettlers.main.android.core.controls.GameMenu;

/**
 * Created by tompr on 21/01/2017.
 */

public interface GameManager {
	ControlsAdapter getControlsAdapter();

	GameMenu getGameMenu();

	boolean isGameInProgress();
}
