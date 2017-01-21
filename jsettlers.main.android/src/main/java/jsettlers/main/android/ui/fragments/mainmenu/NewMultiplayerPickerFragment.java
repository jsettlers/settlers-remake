package jsettlers.main.android.ui.fragments.mainmenu;

import android.support.v4.app.Fragment;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.main.android.R;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerPickerFragment extends MapPickerFragment {
    public static Fragment newInstance() {
        return new NewMultiPlayerPickerFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.new_multi_player_game);
    }

    @Override
    protected ChangingList<? extends IMapDefinition> getMaps() {
        return getGameStarter().getStartScreenConnector().getMultiplayerMaps();
    }

    @Override
    protected void mapSelected(IMapDefinition map) {
        getNavigator().showNewMultiPlayerSetup(map);
    }
}
