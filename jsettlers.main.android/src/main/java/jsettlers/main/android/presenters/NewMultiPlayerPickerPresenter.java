package jsettlers.main.android.presenters;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;
import jsettlers.main.android.views.MapPickerView;

/**
 * Created by tompr on 22/01/2017.
 */

public class NewMultiPlayerPickerPresenter extends MapPickerPresenter {

    public NewMultiPlayerPickerPresenter(MapPickerView view, GameStarter gameStarter, MainMenuNavigator navigator) {
        super(view, gameStarter, navigator, gameStarter.getStartScreen().getMultiplayerMaps());
    }

    @Override
    public void itemSelected(IMapDefinition mapDefinition) {

    }

    @Override
    public void abort() {
        super.abort();
        //getGameStarter().closeMultiPlayerConnector();
    }
}
