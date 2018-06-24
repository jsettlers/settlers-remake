/*
 * Copyright (c) 2017 - 2018
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

package jsettlers.main.android.core.resources.scanner;

import java.io.File;

import org.androidannotations.annotations.EBean;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import io.reactivex.Completable;
import jsettlers.common.resources.ResourceManager;
import jsettlers.common.resources.SettlersFolderChecker;
import jsettlers.common.resources.SettlersFolderChecker.SettlersFolderInfo;
import jsettlers.graphics.image.reader.DatFileUtils;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.sound.SoundManager;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.main.android.core.resources.AndroidMapListFactory;
import jsettlers.main.android.core.resources.AndroidResourceProvider;

@EBean
public class AndroidResourcesLoader {
	private static final String ORIGINAL_SETTLERS_FILES_PATH_SETTING_KEY = "external-files-path";
	private static final String ORIGINAL_SETTLERS_FILES_VERSION_ID = "external-files-version-id";

	private final Context context;

	public AndroidResourcesLoader(Context context) {
		this.context = context;
	}

	public boolean setup() {
		File storage = Environment.getExternalStorageDirectory();
		File outputDirectory = context.getExternalFilesDir(null); // <- output dir, always writable

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String originalSettlersFolder = preferences.getString(ORIGINAL_SETTLERS_FILES_PATH_SETTING_KEY, storage + "/JSettlers");

		SettlersFolderInfo settlersFolders = SettlersFolderChecker.checkSettlersFolder(originalSettlersFolder);
		if (!settlersFolders.isValidSettlersFolder()) {
			return false;
		}

		String settlersVersionId = preferences.getString(ORIGINAL_SETTLERS_FILES_VERSION_ID, null);
		if (settlersVersionId == null) {
			settlersVersionId = DatFileUtils.generateOriginalVersionId(settlersFolders.gfxFolder);
			preferences.edit().putString(ORIGINAL_SETTLERS_FILES_VERSION_ID, settlersVersionId).apply();
		}

		ImageProvider.setLookupPath(settlersFolders.gfxFolder, settlersVersionId);
		SoundManager.setLookupPath(settlersFolders.sndFolder);

		MapList.setDefaultListFactory(new AndroidMapListFactory(context.getAssets(), outputDirectory));
		ResourceManager.setProvider(new AndroidResourceProvider(context, outputDirectory));
		return true;
	}

	public Completable setupSingle() {
		return Completable.create(emitter -> {
			boolean success = setup();

			if (success) {
				emitter.onComplete();
			} else {
				emitter.onError(new Exception("Not a valid settlers folder"));
			}
		});
	}

	public void setResourcesDirectory(String path) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(ORIGINAL_SETTLERS_FILES_PATH_SETTING_KEY, path).apply();
	}
}
