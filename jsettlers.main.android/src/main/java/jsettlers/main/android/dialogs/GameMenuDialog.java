package jsettlers.main.android.dialogs;

import static jsettlers.main.android.GameService.ACTION_PAUSE;
import static jsettlers.main.android.GameService.ACTION_QUIT;
import static jsettlers.main.android.GameService.ACTION_QUIT_CANCELLED;
import static jsettlers.main.android.GameService.ACTION_UNPAUSE;

import jsettlers.main.android.R;
import jsettlers.main.android.menus.GameMenu;
import jsettlers.main.android.providers.GameMenuProvider;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
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

/**
 * Created by tompr on 16/11/2016.
 */

public class GameMenuDialog extends DialogFragment {
    private GameMenu gameMenu;
    private Button pauseButton;
    private Button quitButton;
    private Button saveButton;

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
        intentFilter.addAction(ACTION_QUIT);
        intentFilter.addAction(ACTION_QUIT_CANCELLED);
        intentFilter.addAction(ACTION_PAUSE);
        intentFilter.addAction(ACTION_UNPAUSE);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_game_menu, null);
        pauseButton = (Button) view.findViewById(R.id.button_pause);
        quitButton = (Button) view.findViewById(R.id.button_quit);
        saveButton = (Button) view.findViewById(R.id.button_save);

        setPauseButtonText();
        setQuitButtonText();

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

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameMenu.canQuitConfirm()) {
                    gameMenu.quitConfirm();
                } else {
                    gameMenu.quit();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameMenu.save();
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

    private void setQuitButtonText() {
        if (gameMenu.canQuitConfirm()) {
            quitButton.setText(R.string.game_menu_quit_confirm);
        } else {
            quitButton.setText(R.string.game_menu_quit);
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
            switch (intent.getAction()) {
                case ACTION_QUIT:
                    setQuitButtonText();
                    break;
                case ACTION_QUIT_CANCELLED:
                    setQuitButtonText();
                    break;
                case ACTION_PAUSE:
                    setPauseButtonText();
                    break;
                case ACTION_UNPAUSE:
                    setPauseButtonText();
                    break;
            }
        }
    };
}
