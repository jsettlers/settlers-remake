package jsettlers.main.android.ui.fragments.mainmenu;

import android.support.v4.app.Fragment;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;

/**
 * Created by tompr on 19/01/2017.
 */

public class LoadSinglePlayerPickerFragment extends MapPickerFragment {
    public static Fragment newInstance() {
        return new LoadSinglePlayerPickerFragment();
    }

    @Override
    protected ChangingList<? extends IMapDefinition> getMaps() {
        return getGameStarter().getStartScreenConnector().getStoredSingleplayerGames();
    }

    @Override
    protected void mapSelected(IMapDefinition map) {
        super.mapSelected(map);
        getGameStarter().loadSinglePlayerGame();
    }
}
