package jsettlers.main.android.gameplay.ui.dialogs;

import jsettlers.main.android.R;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by tompr on 23/11/2016.
 */

public class PausedDialog extends DialogFragment {
    public interface Listener {
        void onUnPause();
    }

    public static PausedDialog newInstance() {
        return new PausedDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.game_menu_paused)
                .setPositiveButton(R.string.game_menu_unpause, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getDialog().cancel();
                    }
                })
                .setCancelable(true)
                .create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Listener listener = (Listener) getParentFragment();
        listener.onUnPause();
    }
}
