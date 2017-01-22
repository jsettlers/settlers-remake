package jsettlers.main.android.presenters;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;
import jsettlers.main.android.views.MapPickerView;

/**
 * Created by tompr on 22/01/2017.
 */

public class NewSinglePlayerPickerPresenter extends MapPickerPresenter {

    private final GameStarter gameStarter;
    private final MainMenuNavigator navigator;

    public NewSinglePlayerPickerPresenter(MapPickerView view, GameStarter gameStarter, MainMenuNavigator navigator) {
        super(view, gameStarter, navigator, gameStarter.getStartScreen().getSingleplayerMaps());
        this.gameStarter = gameStarter;
        this.navigator = navigator;
    }

    @Override
    public void itemSelected(IMapDefinition mapDefinition) {
        gameStarter.setMapDefinition(mapDefinition);
        navigator.showNewSinglePlayerSetup(mapDefinition);
    }
}
