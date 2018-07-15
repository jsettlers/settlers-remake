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

package jsettlers.main.android.mainmenu.home;

import java.io.File;
import java.io.IOException;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import jsettlers.common.resources.SettlersFolderChecker;
import jsettlers.main.android.R;

@EFragment
public class DirectoryPickerDialog extends DialogFragment {

	@Bean
	DirectoryAdapter directoryAdapter;

	public interface Listener {
		void onDirectorySelected(File resourceDirectory);
	}

	public static DirectoryPickerDialog newInstance() {
		return new DirectoryPickerDialog_();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		View view = layoutInflater.inflate(R.layout.dialog_directory_picker, null);

		ProgressBar progressBar = view.findViewById(R.id.progressBar);

		ListView listView = view.findViewById(R.id.listView);
		listView.setAdapter(directoryAdapter);
		listView.setOnItemClickListener((arg0, arg1, position, arg3) -> {
			directoryAdapter.positionSelected(position);
			setButtonState();
		});

		AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.resource_selection_dialog_title)
				.setView(view)
				.setPositiveButton(R.string.ok, null)
				.create();

		alertDialog.setOnShowListener(dialog -> {
			Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

			button.setOnClickListener(v -> {
				setCancelable(false);
				listView.setEnabled(false);
				button.setEnabled(false);
				progressBar.setVisibility(View.VISIBLE);
				((Listener) getParentFragment()).onDirectorySelected(directoryAdapter.getCurrentDirectory());
			});
		});

		return alertDialog;
	}

	@Override
	public void onStart() {
		super.onStart();
		setButtonState();
	}

	private void setButtonState() {
		AlertDialog dialog = (AlertDialog) getDialog();
		boolean hasGameFiles = SettlersFolderChecker.checkSettlersFolder(directoryAdapter.getCurrentDirectory()).isValidSettlersFolder();
		Button button = dialog.getButton(Dialog.BUTTON_POSITIVE);
		button.setEnabled(hasGameFiles);
	}

	@EBean
	static class DirectoryAdapter extends ArrayAdapter<String> {
		private final File baseDirectory;
		private File currentDirectory;

		public DirectoryAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_1);
			this.baseDirectory = Environment.getExternalStorageDirectory();
			this.currentDirectory = this.baseDirectory;

			updateList();
		}

		void positionSelected(int position) {
			String item = getItem(position);
			if (item != null) {
				try {
					File newDirectory = new File(currentDirectory, item).getCanonicalFile();
					if (newDirectory.exists()) {
						currentDirectory = newDirectory;
						updateList();
					}
				} catch (IOException e) {
					// ignore this exception
				}
			}
		}

		@Override
		@UiThread
		public void clear() {
			super.clear();
		}

		@Background
		void updateList() {
			clear();
			addFiles(currentDirectory.listFiles(File::isDirectory));
		}

		@UiThread
		void addFiles(File[] files) {
			if (!baseDirectory.equals(currentDirectory)) {
				add("..");
			}

			if (files != null && files.length > 0) {
				for (File file : files) {
					add(file.getName());
				}
			} else {
				add(getContext().getResources().getString(R.string.empty_directory));
			}
		}

		File getCurrentDirectory() {
			return currentDirectory;
		}
	}
}
