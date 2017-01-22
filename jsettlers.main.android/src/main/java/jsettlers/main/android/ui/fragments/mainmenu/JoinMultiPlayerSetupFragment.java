package jsettlers.main.android.ui.fragments.mainmenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import jsettlers.common.menu.IMultiplayerListener;
import jsettlers.common.menu.IStartingGame;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.navigation.GameNavigator;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;

/**
 * Created by tompr on 22/01/2017.
 */

public class JoinMultiPlayerSetupFragment extends Fragment implements IMultiplayerListener {
    private GameStarter gameStarter;
    private MainMenuNavigator navigator;

    public static Fragment create() {
        return new JoinMultiPlayerSetupFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameStarter = (GameStarter) getActivity().getApplication();
        navigator = (MainMenuNavigator) getActivity();

        if (gameStarter.getJoinPhaseMultiplayerConnector() == null) {
            getActivity().getSupportFragmentManager().popBackStack();
        } else {
            gameStarter.getJoinPhaseMultiplayerConnector().setMultiplayerListener(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new LinearLayout(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (gameStarter.getJoinPhaseMultiplayerConnector() != null) {
            gameStarter.getJoinPhaseMultiplayerConnector().setMultiplayerListener(this);
        }
    }

    /**
     * IMultiplayerListener implementation
     */
    @Override
    public void gameIsStarting(IStartingGame game) {
        gameStarter.setStartingGame(game);
        navigator.showGame();
    }

    @Override
    public void gameAborted() {

    }
}
