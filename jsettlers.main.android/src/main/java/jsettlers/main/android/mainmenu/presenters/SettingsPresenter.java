package jsettlers.main.android.mainmenu.presenters;

import jsettlers.main.android.core.AndroidPreferences;
import jsettlers.main.android.mainmenu.views.SettingsView;

/**
 * Created by tompr on 03/03/2017.
 */

public class SettingsPresenter {

	private final SettingsView view;
	private final AndroidPreferences androidPreferences;

	public SettingsPresenter(SettingsView view, AndroidPreferences androidPreferences) {
		this.view = view;
		this.androidPreferences = androidPreferences;
	}

	public void bindView() {
		view.setPlayerName(androidPreferences.getPlayerName());
	}

	public void playerNameEdited(String playerName) {
		androidPreferences.setPlayerName(playerName);
		view.setPlayerName(playerName);
	}
}
