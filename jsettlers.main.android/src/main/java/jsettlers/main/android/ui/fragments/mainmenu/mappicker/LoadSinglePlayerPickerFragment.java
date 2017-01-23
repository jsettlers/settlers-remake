package jsettlers.main.android.ui.fragments.mainmenu.mappicker;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IStartingGame;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.main.android.R;
import jsettlers.main.android.presenters.LoadSinglePlayerPickerPresenter;
import jsettlers.main.android.presenters.MapPickerPresenter;
import jsettlers.main.android.presenters.NewMultiPlayerPickerPresenter;
import jsettlers.main.android.providers.GameStarter;
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
    protected MapPickerPresenter getPresenter() {
        GameStarter gameStarter = (GameStarter) getActivity().getApplication();
        MainMenuNavigator navigator = (MainMenuNavigator) getActivity();

        return new LoadSinglePlayerPickerPresenter(this, gameStarter, navigator);
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
    protected boolean showMapDates() {
        return true;
    }
}
