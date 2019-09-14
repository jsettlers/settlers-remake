package jsettlers.main.android.mainmenu.home;

import java.io.File;
import java.io.IOException;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import jsettlers.common.resources.SettlersFolderChecker;
import jsettlers.main.android.R;

public class DirectoryPickerViewHolder extends RecyclerView.ViewHolder {
	private final MainMenuViewModel viewModel;
	private final ListView listView;
	private final TextView directoriesExplanationTextView;
	private final Button chooseDirectoryButton;
	private final View progressBar;

	public DirectoryPickerViewHolder(View itemView, Fragment viewModelOwner) {
		super(itemView);
		viewModel = ViewModelProviders.of(viewModelOwner).get(MainMenuViewModel.class);

		listView = itemView.findViewById(R.id.listView);
		progressBar = itemView.findViewById(R.id.progressBar);
		chooseDirectoryButton = itemView.findViewById(R.id.button_resources);
		directoriesExplanationTextView = itemView.findViewById(R.id.textView_directoriesExplanation);

		listView.setOnTouchListener((v, event) -> {
			v.getParent().requestDisallowInterceptTouchEvent(true);
			return false;
		});
	}

	public void onExpand() {
		DirectoryAdapter directoryAdapter = new DirectoryAdapter(itemView.getContext());

		listView.setAdapter(directoryAdapter);

		listView.setOnItemClickListener((arg0, arg1, position, arg3) -> {
			directoryAdapter.positionSelected(position);
		});

		listView.setVisibility(View.VISIBLE);
		directoriesExplanationTextView.setVisibility(View.GONE);
		chooseDirectoryButton.setVisibility(View.GONE);
	}

	class DirectoryAdapter extends ArrayAdapter<String> {
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

						boolean isSettlersFolder = SettlersFolderChecker.checkSettlersFolder(currentDirectory).isValidSettlersFolder();

						if (isSettlersFolder) {
							viewModel.resourceDirectoryChosen(currentDirectory);
							progressBar.setVisibility(View.VISIBLE);
							listView.setEnabled(false);
						}

						updateList();
					}
				} catch (IOException e) {
					// ignore this exception
				}
			}
		}

		void updateList() {
			clear();
			addFiles(currentDirectory.listFiles(File::isDirectory));
		}

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
	}
}
