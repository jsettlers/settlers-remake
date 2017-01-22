package jsettlers.main.android.ui.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by tompr on 22/01/2017.
 */

public class NewJoiningGameProgressDialog extends DialogFragment {
    private static final String ARG_STATE = "argstate";
    private static final String ARG_PROGRESS = "argprogress";

    private ProgressDialog progressDialog;

    public static DialogFragment create(String stateString, int progressPercentage) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_STATE, stateString);
        bundle.putInt(ARG_PROGRESS, progressPercentage);

        DialogFragment dialogFragment = new NewJoiningGameProgressDialog();
        dialogFragment.setArguments(bundle);

        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getActivity());

        String state = getArguments().getString(ARG_STATE);
        int progress = getArguments().getInt(ARG_PROGRESS);

        setProgress(state, progress);

        return progressDialog;
    }

    public void setProgress(String state, int progress) {
        progressDialog.setMessage(state);
        progressDialog.setProgress(progress);
    }
}
