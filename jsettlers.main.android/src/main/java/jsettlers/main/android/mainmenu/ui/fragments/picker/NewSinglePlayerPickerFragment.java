package jsettlers.main.android.mainmenu.ui.fragments.picker;

import android.support.v4.app.Fragment;

import jsettlers.main.android.R;
import jsettlers.main.android.mainmenu.factories.PresenterFactory;
import jsettlers.main.android.mainmenu.presenters.picker.MapPickerPresenter;

/**
 * Created by tompr on 19/01/2017.
 */

public class NewSinglePlayerPickerFragment extends MapPickerFragment {
    public static Fragment newInstance() {
        return new NewSinglePlayerPickerFragment();
    }

    @Override
    protected MapPickerPresenter getPresenter() {
        return PresenterFactory.createNewSinglePlayerPickerPresenter(getActivity(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.new_single_player_game);
    }
}
