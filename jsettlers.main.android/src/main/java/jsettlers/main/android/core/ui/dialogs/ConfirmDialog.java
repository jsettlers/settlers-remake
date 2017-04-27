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

import jsettlers.main.android.R;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by tompr on 23/11/2016.
 */
public class ConfirmDialog extends DialogFragment {
	private static final String ARG_REQUEST_CODE = "requestcode";
	private static final String ARG_TITLE = "title";
	private static final String ARG_MESSAGE = "message";
	private static final String ARG_CONFIRM_BUTTON_TEXT = "confirmbuttontext";
	private static final String ARG_CANCEL_BUTTON_TEXT = "cancelbuttontext";

	private int requestCode;

	public interface ConfirmListener {
		void onConfirm(int requestCode);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestCode = getArguments().getInt(ARG_REQUEST_CODE);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		int titleResId = bundle.getInt(ARG_TITLE);
		int messageResId = bundle.getInt(ARG_MESSAGE);
		int confirmButtonTextResId = bundle.getInt(ARG_CONFIRM_BUTTON_TEXT, R.string.ok);
		int cancelButtonTextResId = bundle.getInt(ARG_CANCEL_BUTTON_TEXT, R.string.cancel);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		if (titleResId != 0) {
			builder.setTitle(titleResId);
		}

		if (messageResId != 0) {
			builder.setMessage(messageResId);
		}

		builder.setPositiveButton(confirmButtonTextResId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				ConfirmListener confirmListener = null;

				if (getParentFragment() instanceof ConfirmListener) {
					confirmListener = (ConfirmListener) getParentFragment();
				}

				if (confirmListener != null) {
					confirmListener.onConfirm(requestCode);
				}
			}
		});

		builder.setNegativeButton(cancelButtonTextResId, null);

		return builder.create();
	}

	public static class Builder {
		private final Bundle bundle;

		public Builder(int requestCode) {
			bundle = new Bundle();
			bundle.putInt(ARG_REQUEST_CODE, requestCode);
		}

		public Builder setTitle(int titleResId) {
			bundle.putInt(ARG_TITLE, titleResId);
			return this;
		}

		public Builder setMessage(int messageResId) {
			bundle.putInt(ARG_TITLE, messageResId);
			return this;
		}

		public Builder setConfirmButtonText(int confirmButtonTextResId) {
			bundle.putInt(ARG_CONFIRM_BUTTON_TEXT, confirmButtonTextResId);
			return this;
		}

		public Builder setCancelButtonText(int cancelButtonTextResId) {
			bundle.putInt(ARG_CANCEL_BUTTON_TEXT, cancelButtonTextResId);
			return this;
		}

		public ConfirmDialog create() {
			ConfirmDialog confirmDialog = new ConfirmDialog();
			confirmDialog.setArguments(bundle);
			return confirmDialog;
		}
	}
}
