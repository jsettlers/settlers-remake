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

package jsettlers.main.android.mainmenu.ui.dialogs;

import java.io.File;
import java.io.IOException;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

import jsettlers.common.resources.SettlersFolderChecker;
import jsettlers.main.android.R;
import jsettlers.main.android.core.resources.scanner.AndroidResourcesLoader;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

@EFragment
public class DirectoryPickerDialog extends DialogFragment {

	@Bean
	DirectoryAdapter directoryAdapter;
	@Bean
	AndroidResourcesLoader androidResourcesLoader;

	public interface Listener {
		void onDirectorySelected();
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
		ListView listView = new ListView(getActivity());
		listView.setAdapter(directoryAdapter);
		listView.setOnItemClickListener((arg0, arg1, position, arg3) -> {
			directoryAdapter.positionSelected(position);
			setButtonState();
		});

		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.resource_selection_dialog_title)
				.setView(listView)
				.setPositiveButton(R.string.ok, (dialog, which) -> {
					androidResourcesLoader.setResourcesDirectory(directoryAdapter.getCurrentDirectory().getAbsolutePath());
					((Listener) getParentFragment()).onDirectorySelected();
				})
				.create();
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
