/*
 * Copyright (c) 2017
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
 */

package jsettlers.main.android.mainmenu.factories;

import static java8.util.stream.StreamSupport.stream;

import java.util.List;

import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.main.android.core.AndroidPreferences;
import jsettlers.main.android.core.GameManager;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.core.resources.scanner.ResourcesLocationManager;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.presenters.MainMenuPresenter;
import jsettlers.main.android.mainmenu.presenters.SettingsPresenter;
import jsettlers.main.android.mainmenu.presenters.picker.JoinMultiPlayerPickerPresenter;
import jsettlers.main.android.mainmenu.presenters.picker.LoadSinglePlayerPickerPresenter;
import jsettlers.main.android.mainmenu.presenters.picker.NewMultiPlayerPickerPresenter;
import jsettlers.main.android.mainmenu.presenters.picker.NewSinglePlayerPickerPresenter;
import jsettlers.main.android.mainmenu.presenters.setup.JoinMultiPlayerSetupPresenter;
import jsettlers.main.android.mainmenu.presenters.setup.JoinMultiPlayerSetupPresenterImpl;
import jsettlers.main.android.mainmenu.presenters.setup.JoinMultiPlayerSetupPresenterPop;
import jsettlers.main.android.mainmenu.presenters.setup.NewMultiPlayerSetupPresenter;
import jsettlers.main.android.mainmenu.presenters.setup.NewMultiPlayerSetupPresenterImpl;
import jsettlers.main.android.mainmenu.presenters.setup.NewMultiPlayerSetupPresenterPop;
import jsettlers.main.android.mainmenu.presenters.setup.NewSinglePlayerSetupPresenter;
import jsettlers.main.android.mainmenu.views.JoinMultiPlayerPickerView;
import jsettlers.main.android.mainmenu.views.JoinMultiPlayerSetupView;
import jsettlers.main.android.mainmenu.views.LoadSinglePlayerPickerView;
import jsettlers.main.android.mainmenu.views.MainMenuView;
import jsettlers.main.android.mainmenu.views.MapPickerView;
import jsettlers.main.android.mainmenu.views.NewMultiPlayerPickerView;
import jsettlers.main.android.mainmenu.views.NewMultiPlayerSetupView;
import jsettlers.main.android.mainmenu.views.NewSinglePlayerSetupView;
import jsettlers.main.android.mainmenu.views.SettingsView;

import android.app.Activity;
import android.content.Context;

/**
 * Created by tompr on 03/02/2017.
 */
public class PresenterFactory {

	public static MainMenuPresenter createMainMenuPresenter(Activity activity, MainMenuView view) {
		MainMenuNavigator navigator = (MainMenuNavigator) activity;
		GameManager gameManager = (GameManager) activity.getApplication();

		return new MainMenuPresenter(view, navigator, gameManager, new ResourcesLocationManager(activity));
	}

	public static SettingsPresenter createSettingsPresenter(Context context, SettingsView view) {
		return new SettingsPresenter(view, new AndroidPreferences(context));
	}

	/**
	 * Picker screen presenters
	 */
	public static NewSinglePlayerPickerPresenter createNewSinglePlayerPickerPresenter(Activity activity, MapPickerView view) {
		MainMenuNavigator navigator = (MainMenuNavigator) activity;
		GameStarter gameStarter = (GameStarter) activity.getApplication();
		ChangingList<MapLoader> changingMaps = gameStarter.getMapList().getFreshMaps();

		return new NewSinglePlayerPickerPresenter(view, navigator, gameStarter, changingMaps);
	}

	public static LoadSinglePlayerPickerPresenter createLoadSinglePlayerPickerPresenter(Activity activity, LoadSinglePlayerPickerView view) {
		MainMenuNavigator navigator = (MainMenuNavigator) activity;
		GameStarter gameStarter = (GameStarter) activity.getApplication();
		ChangingList<? extends MapLoader> changingMaps = gameStarter.getMapList().getSavedMaps();

		return new LoadSinglePlayerPickerPresenter(view, navigator, gameStarter, changingMaps);
	}

	public static NewMultiPlayerPickerPresenter createNewMultiPlayerPickerPresenter(Activity activity, NewMultiPlayerPickerView view) {
		MainMenuNavigator navigator = (MainMenuNavigator) activity;
		GameStarter gameStarter = (GameStarter) activity.getApplication();
		ChangingList<MapLoader> changingMaps = gameStarter.getMapList().getFreshMaps();

		return new NewMultiPlayerPickerPresenter(view, navigator, gameStarter, new AndroidPreferences(activity), changingMaps);
	}

	public static JoinMultiPlayerPickerPresenter createJoinMultiPlayerPickerPresenter(Activity activity, JoinMultiPlayerPickerView view) {
		MainMenuNavigator navigator = (MainMenuNavigator) activity;
		GameStarter gameStarter = (GameStarter) activity.getApplication();

		return new JoinMultiPlayerPickerPresenter(view, navigator, gameStarter);
	}

	/**
	 * Setup screen presenters
	 */
	public static NewSinglePlayerSetupPresenter createNewSinglePlayerSetupPresenter(Activity activity, NewSinglePlayerSetupView view, String mapId) {
		MainMenuNavigator navigator = (MainMenuNavigator) activity;
		GameStarter gameStarter = (GameStarter) activity.getApplication();

		List<MapLoader> maps = gameStarter.getMapList().getFreshMaps().getItems();
		MapLoader mapDefinition = stream(maps)
				.filter(x -> mapId.equals(x.getMapId()))
				.findFirst()
				.get();

		return new NewSinglePlayerSetupPresenter(view, navigator, gameStarter, new AndroidPreferences(activity), mapDefinition);
	}

	public static NewMultiPlayerSetupPresenter createNewMultiPlayerSetupPresenter(Activity activity, NewMultiPlayerSetupView view, String mapId) {
		MainMenuNavigator mainMenuNavigator = (MainMenuNavigator) activity;
		GameStarter gameStarter = (GameStarter) activity.getApplication();
		IJoinPhaseMultiplayerGameConnector joinPhaseMultiplayerGameConnector = gameStarter.getJoinPhaseMultiplayerConnector();

		List<MapLoader> maps = gameStarter.getMapList().getFreshMaps().getItems();
		MapLoader mapDefinition = stream(maps)
				.filter(x -> mapId.equals(x.getMapId()))
				.findFirst()
				.get();

		if (joinPhaseMultiplayerGameConnector == null || mapDefinition == null) {
			return new NewMultiPlayerSetupPresenterPop(mainMenuNavigator);
		} else {
			return new NewMultiPlayerSetupPresenterImpl(view, mainMenuNavigator, gameStarter, joinPhaseMultiplayerGameConnector,
					new AndroidPreferences(activity), mapDefinition);
		}
	}

	public static JoinMultiPlayerSetupPresenter createJoinMultiPlayerSetupPresenter(Activity activity, JoinMultiPlayerSetupView view, String mapId) {
		MainMenuNavigator navigator = (MainMenuNavigator) activity;
		GameStarter gameStarter = (GameStarter) activity.getApplication();
		IJoinPhaseMultiplayerGameConnector connector = gameStarter.getJoinPhaseMultiplayerConnector();

		List<MapLoader> maps = gameStarter.getMapList().getFreshMaps().getItems();
		MapLoader mapDefinition = stream(maps)
				.filter(x -> mapId.equals(x.getMapId()))
				.findFirst()
				.get();

		if (connector == null/* || mapDefinition == null */) {
			return new JoinMultiPlayerSetupPresenterPop(navigator);
		} else {
			return new JoinMultiPlayerSetupPresenterImpl(view, navigator, gameStarter, connector, new AndroidPreferences(activity), mapDefinition);
		}
	}
}
