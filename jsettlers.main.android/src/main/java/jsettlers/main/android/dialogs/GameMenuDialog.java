package jsettlers.main.android.dialogs;

import jsettlers.main.android.Menus.GameMenu;
import jsettlers.main.android.R;
import jsettlers.main.android.providers.GameMenuProvider;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

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

        view.findViewById(R.id.button_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameMenu.pause();
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
}
