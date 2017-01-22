package jsettlers.main.android.ui.fragments.mainmenu.mappicker;

import android.support.v4.app.Fragment;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.main.android.R;

/**
 * Created by tompr on 19/01/2017.
 */

public class NewSinglePlayerPickerFragment extends MapPickerFragment {
    public static Fragment newInstance() {
        return new NewSinglePlayerPickerFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.new_single_player_game);
    }

    @Override
    protected ChangingList<? extends IMapDefinition> getMaps() {
        return getGameStarter().getStartScreen().getSingleplayerMaps();
    }

    @Override
    protected void mapSelected(IMapDefinition map) {
        getNavigator().showNewSinglePlayerSetup(map);
    }
}
