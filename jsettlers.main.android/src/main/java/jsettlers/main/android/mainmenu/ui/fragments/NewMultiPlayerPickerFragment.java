package jsettlers.main.android.mainmenu.ui.fragments;

import jsettlers.main.android.R;
import jsettlers.main.android.mainmenu.presenters.MapPickerPresenter;
import jsettlers.main.android.mainmenu.presenters.NewMultiPlayerPickerPresenter;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.mainmenu.ui.dialogs.JoiningGameProgressDialog;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.views.NewMultiPlayerPickerView;

import android.support.v4.app.Fragment;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerPickerFragment extends MapPickerFragment implements NewMultiPlayerPickerView {
    private static final String TAG_JOINING_PROGRESS_DIALOG = "joingingprogress";

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

    /**
     * NewMultiPlayerPickerView implementation
     */
    @Override
    public void setJoiningProgress(String stateString, int progressPercentage) {
        JoiningGameProgressDialog joiningProgressDialog = (JoiningGameProgressDialog) getChildFragmentManager().findFragmentByTag(TAG_JOINING_PROGRESS_DIALOG);
        if (joiningProgressDialog == null) {
            JoiningGameProgressDialog.create(stateString, progressPercentage).show(getChildFragmentManager(), TAG_JOINING_PROGRESS_DIALOG);
        } else {
            joiningProgressDialog.setProgress(stateString, progressPercentage);
        }
    }

    @Override
    public void dismissJoiningProgress() {
        JoiningGameProgressDialog joiningProgressDialog = (JoiningGameProgressDialog) getChildFragmentManager().findFragmentByTag(TAG_JOINING_PROGRESS_DIALOG);
        if (joiningProgressDialog != null) {
            joiningProgressDialog.dismiss();
        }
    }
}
