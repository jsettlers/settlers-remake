package jsettlers.main.android.ui.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IJoiningGameListener;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;

/**
 * Created by tompr on 22/01/2017.
 */

public class JoiningGameProgressDialog extends DialogFragment implements IJoiningGameListener {
    private GameStarter gameStarter;
    private MainMenuNavigator navigator;

    public static DialogFragment create() {
        return new JoiningGameProgressDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameStarter = (GameStarter) getActivity().getApplication();
        navigator = (MainMenuNavigator) getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new ProgressDialog.Builder(getActivity())
                .setTitle("join progress")
                .create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (gameStarter.getJoiningGame() == null) {
            dismiss();
        } else {
            gameStarter.getJoiningGame().setListener(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (gameStarter.getJoiningGame() != null) {
            gameStarter.getJoiningGame().setListener(null);
        }
    }

    /**
     * IJoiningGameListener imeplementation
     */
    @Override
    public void joinProgressChanged(EProgressState state, float progress) {

    }

    @Override
    public void gameJoined(IJoinPhaseMultiplayerGameConnector connector) {
        getActivity().runOnUiThread(() -> {
            gameStarter.setJoinPhaseMultiPlayerConnector(connector);
            dismiss();
            navigator.showJoinMultiPlayerSetup();
        });
    }

    private void showProgress() {

    }
}
