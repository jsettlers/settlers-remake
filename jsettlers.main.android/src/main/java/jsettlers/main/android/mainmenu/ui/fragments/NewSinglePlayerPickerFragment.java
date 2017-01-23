package jsettlers.main.android.mainmenu.ui.fragments;

import android.support.v4.app.Fragment;

import jsettlers.main.android.R;
import jsettlers.main.android.mainmenu.presenters.MapPickerPresenter;
import jsettlers.main.android.mainmenu.presenters.NewSinglePlayerPickerPresenter;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;

/**
 * Created by tompr on 19/01/2017.
 */

public class NewSinglePlayerPickerFragment extends MapPickerFragment {
    public static Fragment newInstance() {
        return new NewSinglePlayerPickerFragment();
    }

    @Override
    protected MapPickerPresenter getPresenter() {
        GameStarter gameStarter = (GameStarter) getActivity().getApplication();
        MainMenuNavigator navigator = (MainMenuNavigator) getActivity();

        return new NewSinglePlayerPickerPresenter(this, gameStarter, navigator);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.new_single_player_game);
    }
}
