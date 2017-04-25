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
import java.io.FileFilter;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import jsettlers.main.android.core.resources.scanner.ResourceLocationScanner;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by tingl on 27/05/2016.
 */
public class DirectoryPickerDialog extends DialogFragment {

	private ArrayAdapter<File> directoryAdapter;
	private Stack<File> directoryStack;

	public interface Listener {
		void onDirectorySelected();
	}

	public static DirectoryPickerDialog newInstance() {
		DirectoryPickerDialog fragment = new DirectoryPickerDialog();
		// fragment.setCancelable(false);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		directoryStack = new Stack<>();
		directoryStack.push(Environment.getExternalStorageDirectory());
		directoryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
		updateList();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ListView listView = new ListView(getActivity());
		listView.setAdapter(directoryAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if (position == 0) {
					directoryStack.pop();
				} else {
					// String format = directoryStack.size() == 1 ? "%s%s" : "%s/%s";
					// directoryStack.push(String.format(format, directoryStack.peek(), directoryAdapter.getItem(position)));
					directoryStack.push(directoryAdapter.getItem(position));
				}
				updateList();
				setButtonState();
			}
		});

		return new AlertDialog.Builder(getActivity())
				.setTitle("Locate SND and GFX folders")
				.setView(listView)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new ResourceLocationScanner(getActivity()).setExternalDirectory(directoryStack.peek().getAbsolutePath());
						Listener listener = (Listener) getParentFragment();
						listener.onDirectorySelected();
					}
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
		List<File> paths = Collections.singletonList(directoryStack.peek());
		boolean hasGameFiles = ResourceLocationScanner.hasImagesOnPath(paths);
		Button button = dialog.getButton(Dialog.BUTTON_POSITIVE);
		button.setEnabled(hasGameFiles);
	}

	private void updateList() {
		directoryAdapter.clear();
		new DirectoryScanner(directoryStack.peek(), directoryAdapter).start();
	}

	private class DirectoryScanner extends Thread {
		private ArrayAdapter<File> directories;
		private File base;
		private Handler handler;

		public DirectoryScanner(File base, ArrayAdapter<File> directories) {
			super("DirectoryScanner");
			this.base = base;
			this.directories = directories;
			handler = new Handler();
		}

		@Override
		public void run() {
			File dir = base;
			final File[] files = dir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			});
			handler.post(new Runnable() {
				@Override
				public void run() {
					directories.add(directoryStack.peek().getParentFile());

					if (files != null && files.length > 0) {
						for (File f : files) {
							directories.add(f);
						}
					} else {
						// TODO: Display an empty direcotry message.
					}
				}
			});
		}
	}
}
