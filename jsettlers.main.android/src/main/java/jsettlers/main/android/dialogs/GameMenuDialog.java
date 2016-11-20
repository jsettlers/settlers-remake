package jsettlers.main.android.dialogs;

import jsettlers.main.android.menus.GameMenu;
import jsettlers.main.android.R;
import jsettlers.main.android.providers.GameMenuProvider;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import static jsettlers.main.android.GameService.ACTION_PAUSE;
import static jsettlers.main.android.GameService.ACTION_UNPAUSE;

/**
 * Created by tompr on 16/11/2016.
 */

public class GameMenuDialog extends DialogFragment {
    private GameMenu gameMenu;
    private Button pauseButton;

    private LocalBroadcastManager localBroadcastManager;

    public static GameMenuDialog newInstance() {
        GameMenuDialog dialog = new GameMenuDialog();
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameMenuProvider gameMenuProvider = (GameMenuProvider)getParentFragment();
        gameMenu = gameMenuProvider.getGameMenu();
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (gameMenu == null) {
            dismiss();
            return new Dialog(getActivity());
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PAUSE);
        intentFilter.addAction(ACTION_UNPAUSE);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_game_menu, null);
        pauseButton = (Button) view.findViewById(R.id.button_pause);
        setPauseButtonText();

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameMenu.isPaused()) {
                    gameMenu.unPause();
                } else {
                    gameMenu.pause();
                }
                setPauseButtonText();
            }
        });

        view.findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameMenu.save();
            }
        });

        view.findViewById(R.id.button_quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameMenu.quit();
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.game_menu_title)
                .setView(view)
                .create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);

        if (gameMenu != null && gameMenu.isPaused() && isRemoving()) {
            gameMenu.unPause();
        }
    }

    private void setPauseButtonText() {
        if (gameMenu.isPaused()) {
            pauseButton.setText(R.string.game_menu_unpause);
        } else {
            pauseButton.setText(R.string.game_menu_pause);
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setPauseButtonText();
        }
    };
}
