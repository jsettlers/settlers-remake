package jsettlers.main.android.mainmenu.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import jsettlers.main.android.providers.GameStarter;

/**
 * Created by tompr on 22/01/2017.
 */

public class LoadMultiPlayerPickerFragment extends Fragment {
    private GameStarter gameStarter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameStarter = (GameStarter) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRemoving()) {
            // move to presenter
            gameStarter.closeMultiPlayerConnector();
        }
    }
}
