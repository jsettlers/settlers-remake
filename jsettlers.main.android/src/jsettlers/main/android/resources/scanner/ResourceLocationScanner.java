package jsettlers.main.android.resources.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;
import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.reader.DatFileType;
import jsettlers.graphics.sound.SoundManager;
import jsettlers.logic.map.save.MapList;
import jsettlers.main.android.resources.AndroidMapListFactory;
import jsettlers.main.android.resources.ResourceProvider;

public class ResourceLocationScanner {
	private static final String PREFERENCE = "external-files-path";
	private Context context;

	public ResourceLocationScanner(Context context) {
		this.context = context;
	}

	public boolean scanForResources() {
		File storage = Environment.getExternalStorageDirectory();
		File jsettlersDirectory = new File(storage, "JSettlers");
		ArrayList<File> files = new ArrayList<File>();
		File outputDirectory = context.getExternalFilesDir(null); // <- output dir, always writable
		files.add(outputDirectory);
		files.add(jsettlersDirectory);
		files.add(storage);
		String path = PreferenceManager.getDefaultSharedPreferences(context).getString(PREFERENCE, "");
		if (!path.isEmpty()) {
			files.add(new File(path));
		}

		if (!hasImagesOnPath(files)) {
			return false;
		}

		for (File file : files) {
			ImageProvider.getInstance().addLookupPath(findDir(file, "Gfx"));
			SoundManager.addLookupPath(findDir(file, "Snd"));
		}
		MapList.setDefaultListFactory(new AndroidMapListFactory(context.getAssets(), files.get(0)));

		ResourceProvider provider = new ResourceProvider(context, outputDirectory, jsettlersDirectory);
		ResourceManager.setProvider(provider);
		return true;
	}

	public static boolean hasImagesOnPath(List<File> files) {
		boolean hasSnd = false;
		boolean hasGfx = false;
		for (File file : files) {
			File gfx = findDir(file, "Gfx");
			for (DatFileType t : DatFileType.values()) {
				hasGfx |= new File(gfx, "siedler3_00" + t.getFileSuffix()).exists();
			}
			File snd = findDir(file, "Snd");
			hasSnd |= new File(snd, "Siedler3_00.dat").exists();
		}
		return hasGfx && hasSnd;
	}

	@SuppressLint("DefaultLocale")
	private static File findDir(File file, String dirname) {
		File a = new File(file, dirname.toLowerCase());
		if (a.isDirectory()) {
			return a;
		}
		a = new File(file, dirname.toUpperCase());
		if (a.isDirectory()) {
			return a;
		}
		return a;
	}

	public void setExternalDirectory(String path) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREFERENCE, path).commit();
	}
}
