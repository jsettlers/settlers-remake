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

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import jsettlers.main.android.R;
import jsettlers.main.android.core.utils.StringUtil;

public class EditTextDialog extends DialogFragment {
	private static final String ARG_REQUEST_CODE = "request_code";
	private static final String ARG_TITLE_RES_ID = "title_res_id";
	private static final String ARG_HINT_RES_ID = "hint_res_id";
	private static final String ARG_TEXT = "text";

	private EditText mEditText;

	public interface Listener {
		void saveEditTextDialog(int requestCode, String text);
	}

	public static EditTextDialog create(int requestCode, int titleResId, int hintResId, CharSequence text) {
		EditTextDialog dialog = new EditTextDialog();

		Bundle bundle = new Bundle();
		bundle.putInt(ARG_REQUEST_CODE, requestCode);
		bundle.putInt(ARG_TITLE_RES_ID, titleResId);
		bundle.putInt(ARG_HINT_RES_ID, hintResId);
		bundle.putCharSequence(ARG_TEXT, text);
		dialog.setArguments(bundle);

		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_text, null);
		mEditText = (EditText) view.findViewById(R.id.edit_text);

		final int requestCode = getArguments().getInt(ARG_REQUEST_CODE);
		int titleResId = getArguments().getInt(ARG_TITLE_RES_ID);
		int hintResId = getArguments().getInt(ARG_HINT_RES_ID);
		CharSequence text = getArguments().getCharSequence(ARG_TEXT);

		mEditText.setHint(hintResId);
		mEditText.setText(text);
		mEditText.setSelection(mEditText.getText().length());

		return new AlertDialog.Builder(getActivity()).setView(view)
				.setTitle(titleResId)
				.setPositiveButton("OK", (dialog, id) -> {
					String inputText = mEditText.getText().toString();

					if (!StringUtil.isNullOrWhiteSpace(inputText)) {
						Listener listener = (Listener) getParentFragment();

						if (listener == null) {
							listener = (Listener) getActivity();
						}

						if (listener == null) {
							throw new ClassCastException("EditTextDialog parent does not implement listener.");
						}

						listener.saveEditTextDialog(requestCode, inputText);
					}
				})
				.setNegativeButton("Cancel", null)
				.create();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		// Workaround to avoid memory leak
		mEditText.setCursorVisible(false);
	}
}
