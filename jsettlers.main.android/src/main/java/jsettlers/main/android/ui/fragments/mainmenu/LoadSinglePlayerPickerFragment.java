package jsettlers.main.android.ui.fragments.mainmenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IStartingGame;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.main.android.R;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;

/**
 * Created by tompr on 19/01/2017.
 */

public class LoadSinglePlayerPickerFragment extends MapPickerFragment {
    private MainMenuNavigator navigator;

    public static Fragment newInstance() {
        return new LoadSinglePlayerPickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigator = (MainMenuNavigator) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.load_single_player_game);
    }

    @Override
    protected ChangingList<? extends IMapDefinition> getMaps() {
        return getGameStarter().getStartScreen().getStoredSingleplayerGames();
    }

    @Override
    protected void mapSelected(IMapDefinition mapDefinition) {
        IStartingGame startingGame = getGameStarter().getStartScreen().loadSingleplayerGame(mapDefinition);
        getGameStarter().setStartingGame(startingGame);
        navigator.showGame();
    }

    @Override
    protected boolean showMapDates() {
        return true;
    }
}
