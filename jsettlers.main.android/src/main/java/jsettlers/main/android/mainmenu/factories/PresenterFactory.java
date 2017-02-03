package jsettlers.main.android.mainmenu.factories;

import java8.util.stream.StreamSupport;
import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.graphics.startscreen.SettingsManager;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.presenters.JoinMultiPlayerPickerPresenter;
import jsettlers.main.android.mainmenu.presenters.JoinMultiPlayerSetupPresenter;
import jsettlers.main.android.mainmenu.presenters.JoinMultiPlayerSetupPresenterImpl;
import jsettlers.main.android.mainmenu.presenters.JoinMultiPlayerSetupPresenterPop;
import jsettlers.main.android.mainmenu.presenters.LoadSinglePlayerPickerPresenter;
import jsettlers.main.android.mainmenu.presenters.NewMultiPlayerPickerPresenter;
import jsettlers.main.android.mainmenu.presenters.NewMultiPlayerSetupPresenter;
import jsettlers.main.android.mainmenu.presenters.NewMultiPlayerSetupPresenterImpl;
import jsettlers.main.android.mainmenu.presenters.NewMultiPlayerSetupPresenterPop;
import jsettlers.main.android.mainmenu.presenters.NewSinglePlayerPickerPresenter;
import jsettlers.main.android.mainmenu.presenters.NewSinglePlayerSetupPresenter;
import jsettlers.main.android.mainmenu.views.JoinMultiPlayerPickerView;
import jsettlers.main.android.mainmenu.views.JoinMultiPlayerSetupView;
import jsettlers.main.android.mainmenu.views.MapPickerView;
import jsettlers.main.android.mainmenu.views.NewMultiPlayerPickerView;
import jsettlers.main.android.mainmenu.views.NewMultiPlayerSetupView;
import jsettlers.main.android.mainmenu.views.NewSinglePlayerSetupView;

import android.app.Activity;

/**
 * Created by tompr on 03/02/2017.
 */

public class PresenterFactory {
    /**
     * Picker screen presenters
     */
    public static NewSinglePlayerPickerPresenter createNewSinglePlayerPickerPresenter(Activity activity, MapPickerView view) {
        MainMenuNavigator navigator = (MainMenuNavigator) activity;
        GameStarter gameStarter = (GameStarter) activity.getApplication();
        ChangingList<? extends IMapDefinition> changingMaps = gameStarter.getStartScreen().getSingleplayerMaps();

        return new NewSinglePlayerPickerPresenter(view, navigator, gameStarter, changingMaps);
    }

    public static LoadSinglePlayerPickerPresenter createLoadSinglePlayerPickerPresenter(Activity activity, MapPickerView view) {
        MainMenuNavigator navigator = (MainMenuNavigator) activity;
        GameStarter gameStarter = (GameStarter) activity.getApplication();
        ChangingList<? extends IMapDefinition> changingMaps = gameStarter.getStartScreen().getStoredSingleplayerGames();

        return new LoadSinglePlayerPickerPresenter(view, navigator, gameStarter, changingMaps);
    }

    public static NewMultiPlayerPickerPresenter createNewMultiPlayerPickerPresenter(Activity activity, NewMultiPlayerPickerView view) {
        MainMenuNavigator navigator = (MainMenuNavigator) activity;
        GameStarter gameStarter = (GameStarter) activity.getApplication();
        ChangingList<? extends IMapDefinition> changingMaps = gameStarter.getStartScreen().getMultiplayerMaps();

        return new NewMultiPlayerPickerPresenter(view, navigator, gameStarter, changingMaps);
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

        IMapDefinition mapDefinition = StreamSupport.stream(gameStarter.getStartScreen().getSingleplayerMaps().getItems())
                .filter(x -> mapId.equals(x.getMapId()))
                .findFirst()
                .get();

        return new NewSinglePlayerSetupPresenter(view, navigator, gameStarter, mapDefinition);
    }

    public static NewMultiPlayerSetupPresenter createNewMultiPlayerSetupPresenter(Activity activity, NewMultiPlayerSetupView view, String mapId) {
        MainMenuNavigator mainMenuNavigator = (MainMenuNavigator) activity;
        GameStarter gameStarter = (GameStarter) activity.getApplication();
        IJoinPhaseMultiplayerGameConnector joinPhaseMultiplayerGameConnector = gameStarter.getJoinPhaseMultiplayerConnector();

        IMapDefinition mapDefinition = StreamSupport.stream(gameStarter.getStartScreen().getMultiplayerMaps().getItems())
                .filter(x -> mapId.equals(x.getMapId()))
                .findFirst()
                .get();

        if (joinPhaseMultiplayerGameConnector == null || mapDefinition == null) {
            return new NewMultiPlayerSetupPresenterPop(mainMenuNavigator);
        } else {
            return new NewMultiPlayerSetupPresenterImpl(view, mainMenuNavigator, gameStarter, joinPhaseMultiplayerGameConnector, SettingsManager.getInstance(), mapDefinition);
        }
    }

    public static JoinMultiPlayerSetupPresenter createJoinMultiPlayerSetupPresenter(Activity activity, JoinMultiPlayerSetupView view) {
        MainMenuNavigator navigator = (MainMenuNavigator) activity;
        GameStarter gameStarter = (GameStarter) activity.getApplication();
        IJoinPhaseMultiplayerGameConnector connector = gameStarter.getJoinPhaseMultiplayerConnector();

        if (connector == null/* || mapDefinition == null */) {
            return new JoinMultiPlayerSetupPresenterPop(navigator);
        } else {
            return new JoinMultiPlayerSetupPresenterImpl(view, navigator, gameStarter, connector);
        }
    }
}
