package jsettlers.main.android.ui.fragments.mainmenu.mapsetup;

import jsettlers.main.android.menus.mainmenu.NewMultiPlayerSetupMenu;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.views.NewMultiPlayerSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerSetupFragment extends MapSetupFragment<NewMultiPlayerSetupMenu> implements NewMultiPlayerSetupView {
    private static final String TAG_JOINING_PROGRESS_DIALOG = "joingingprogress";

    @Override
    protected NewMultiPlayerSetupMenu createMenu(GameStarter gameStarter, String mapId) {
        return new NewMultiPlayerSetupMenu(this, gameStarter, mapId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRemoving()) {
            getMenu().dispose();
        }
    }


    /**
     * NewMultiPlayerSetupView implementation
     */
    @Override
    public void setJoiningProgress(String stateString, int progressPercentage) {
//        JoiningGameProgressDialog joiningProgressDialog = (JoiningGameProgressDialog) getChildFragmentManager().findFragmentByTag(TAG_JOINING_PROGRESS_DIALOG);
//        if (joiningProgressDialog == null) {
//            JoiningGameProgressDialog.create(stateString, progressPercentage).show(getChildFragmentManager(), TAG_JOINING_PROGRESS_DIALOG);
//        } else {
//            joiningProgressDialog.setProgress(stateString, progressPercentage);
//        }
    }
}
