package jsettlers.main.android.ui.fragments.mainmenu;

import jsettlers.common.menu.IMultiplayerListener;
import jsettlers.common.menu.IStartingGame;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

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
        LinearLayout linearLayout = new LinearLayout(getActivity());
        CheckBox checkBox = new CheckBox(getActivity());

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            gameStarter.getJoinPhaseMultiplayerConnector().setReady(b);
        });

        linearLayout.addView(checkBox);
        return linearLayout;
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
