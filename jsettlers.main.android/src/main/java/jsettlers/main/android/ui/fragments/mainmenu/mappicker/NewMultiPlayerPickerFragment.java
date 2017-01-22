package jsettlers.main.android.ui.fragments.mainmenu.mappicker;

import jsettlers.main.android.R;
import jsettlers.main.android.presenters.MapPickerPresenter;
import jsettlers.main.android.presenters.NewMultiPlayerPickerPresenter;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;

import android.support.v4.app.Fragment;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerPickerFragment extends MapPickerFragment {
    public static Fragment newInstance() {
        return new NewMultiPlayerPickerFragment();
    }

    @Override
    protected MapPickerPresenter getPresenter() {
        GameStarter gameStarter = (GameStarter) getActivity().getApplication();
        MainMenuNavigator navigator = (MainMenuNavigator) getActivity();

        return new NewMultiPlayerPickerPresenter(this, gameStarter, navigator);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.new_multi_player_game);
    }
}
