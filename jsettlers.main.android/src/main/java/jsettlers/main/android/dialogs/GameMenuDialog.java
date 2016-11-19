package jsettlers.main.android.dialogs;

import jsettlers.main.android.menus.GameMenu;
import jsettlers.main.android.R;
import jsettlers.main.android.providers.GameMenuProvider;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

/**
 * Created by tompr on 16/11/2016.
 */

public class GameMenuDialog extends DialogFragment {
    GameMenu gameMenu;

    public static GameMenuDialog newInstance() {
        GameMenuDialog dialog = new GameMenuDialog();
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameMenuProvider gameMenuProvider = (GameMenuProvider)getParentFragment();
        gameMenu = gameMenuProvider.getGameMenu();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_game_menu, null);

        final Button pauseButton = (Button) view.findViewById(R.id.button_pause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameMenu.isPaused()) {
                    gameMenu.unPause();
                    pauseButton.setText(R.string.game_menu_pause);
                } else {
                    gameMenu.pause();
                    pauseButton.setText(R.string.game_menu_resume);
                }
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
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (gameMenu.isPaused()) {
            gameMenu.unPause();
        }
    }
}
