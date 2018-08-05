/*
 * Copyright (c) 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package jsettlers.main.android.core.ui.dialogs;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import jsettlers.main.android.R;

/**
 * Created by tompr on 23/11/2016.
 */
@EFragment
public class ConfirmDialog extends DialogFragment {
	@FragmentArg("requestCode")
	int requestCode;
	@FragmentArg("titleResourceId")
	int titleResId;
	@FragmentArg("messageResourceId")
	int messageResId;
	@FragmentArg("confirmButtonTextResourceId")
	int confirmButtonTextResId = R.string.ok;
	@FragmentArg("cancelButtonTextResourceId")
	int cancelButtonTextResId = R.string.cancel;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		if (titleResId != 0) {
			builder.setTitle(titleResId);
		}
		if (messageResId != 0) {
			builder.setMessage(messageResId);
		}
		builder.setPositiveButton(confirmButtonTextResId, (dialogInterface, i) -> {
			if (getParentFragment() instanceof ConfirmListener) {
				((ConfirmListener) getParentFragment()).onConfirm(requestCode);
			}
		});
		builder.setNegativeButton(cancelButtonTextResId, null);
		return builder.create();
	}

	public interface ConfirmListener {
		void onConfirm(int requestCode);
	}
}
