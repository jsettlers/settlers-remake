package jsettlers.main.android.gameplay.presenters;

import jsettlers.main.android.core.GameManager;
import jsettlers.main.android.core.controls.ControlsAdapter;
import jsettlers.main.android.core.utils.Dispatcher;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;
import jsettlers.main.android.gameplay.navigation.MenuNavigatorProvider;
import jsettlers.main.android.gameplay.ui.views.BuildingsCategoryView;
import jsettlers.main.android.gameplay.ui.views.SettlersSoldiersView;

import android.app.Activity;

/**
 * Created by tompr on 10/03/2017.
 */

public class MenuFactory {
	private final ControlsAdapter controlsAdapter;
	private final MenuNavigator menuNavigator;

	public MenuFactory(Activity activity) {
		this.controlsAdapter = ((GameManager) activity.getApplication()).getControlsAdapter();
		this.menuNavigator = ((MenuNavigatorProvider) activity).getMenuNavigator();
	}

	public BuildingsCategoryMenu buildingsMenu(BuildingsCategoryView view, int buildingsCategory) {
		return new BuildingsCategoryMenu(view, controlsAdapter, menuNavigator, buildingsCategory);
	}

	public SettlersSoldiersMenu settlersSoldiersMenu(SettlersSoldiersView view) {
		return new SettlersSoldiersMenu(view, controlsAdapter, controlsAdapter, controlsAdapter.getInGamePlayer(), new Dispatcher());
	}
}
