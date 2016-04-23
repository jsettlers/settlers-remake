package jsettlers.main.android.resources.scanner;

import java.io.File;
import java.io.FileFilter;
import java.util.Collections;
import java.util.List;

import jsettlers.main.android.R;
import jsettlers.main.android.fragments.JsettlersFragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * This allows you to chose a directory that contains the GPX and SND files.
 * 
 * @author Michael Zangl
 */
public class FileChoserFragment extends JsettlersFragment {

	private class DirectoryScanner extends Thread {
		private ArrayAdapter<String> directories;
		private String base;
		private Handler handler;

		public DirectoryScanner(String base, ArrayAdapter<String> directories) {
			super("DirectoryScanner");
			this.base = base;
			this.directories = directories;
			handler = new Handler();
		}

		@Override
		public void run() {
			File dir = new File(base);
			final File[] files = dir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			});
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (files != null && files.length > 0) {
						for (File f : files) {
							directories.add(f.getName());
						}
					} else {
						// TODO: Display an empty direcotry message.
					}
				}
			});
		}
	}

	@Override
	public String getName() {
		return "file-choser-" + getDirectory();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.filechoser, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		View okButton = view.findViewById(R.id.filechoser_ok);
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				useThisDirecotry();
			}
		});
		List<File> paths = Collections.singletonList(new File(getDirectory()));
		okButton.setEnabled(ResourceLocationScanner.hasImagesOnPath(paths));

		ListView list = (ListView) view.findViewById(R.id.filechoser_list);
		ArrayAdapter<String> directories = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
		list.setAdapter(directories);
		new DirectoryScanner(getDirectory(), directories).start();
		list.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ArrayAdapter<String> adapter = (ArrayAdapter<String>) arg0.getAdapter();
				String name = adapter.getItem(arg2);
				File dir = new File(new File(getDirectory()), name);
				getJsettlersActivity().showFragment(forDirectory(dir));
			}
		});

		super.onViewCreated(view, savedInstanceState);
	}

	protected void useThisDirecotry() {
		new ResourceLocationScanner(getJsettlersActivity()).setExternalDirectory(getDirectory());
		getJsettlersActivity().imageLookupFixed();
	}

	@Override
	public boolean shouldAddToBackStack() {
		return !"/".equals(getDirectory());
	}

	private String getDirectory() {
		return getArguments().getString("directory");
	}

	public static FileChoserFragment forDirectory(File directory) {
		FileChoserFragment f = new FileChoserFragment();
		Bundle args = new Bundle();
		args.putString("directory", directory.getAbsolutePath());
		f.setArguments(args);
		return f;
	}
}
