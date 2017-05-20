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

package jsettlers.main.android.core.resources.scanner;

import java.io.File;

import org.androidannotations.annotations.EBean;

import jsettlers.common.resources.ResourceManager;
import jsettlers.common.resources.SettlersFolderChecker;
import jsettlers.common.resources.SettlersFolderChecker.SettlersFolderInfo;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.sound.SoundManager;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.main.android.core.resources.AndroidMapListFactory;
import jsettlers.main.android.core.resources.AndroidResourceProvider;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;

@EBean
public class AndroidResourcesLoader {
	private static final String ORIGINAL_SETTLERS_FILES_PATH_SETTING_KEY = "external-files-path";

	private final Context context;

	public AndroidResourcesLoader(Context context) {
		this.context = context;
	}

	public boolean setup() {
		File storage = Environment.getExternalStorageDirectory();
		File outputDirectory = context.getExternalFilesDir(null); // <- output dir, always writable

		String originalSettlersFolder = PreferenceManager.getDefaultSharedPreferences(context).getString(ORIGINAL_SETTLERS_FILES_PATH_SETTING_KEY, storage + "/JSettlers");

		SettlersFolderInfo settlersFolders = SettlersFolderChecker.checkSettlersFolder(originalSettlersFolder);
		if (!settlersFolders.isValidSettlersFolder()) {
			return false;
		}

		ImageProvider.setLookupPath(settlersFolders.gfxFolder);
		SoundManager.setLookupPath(settlersFolders.sndFolder);

		MapList.setDefaultListFactory(new AndroidMapListFactory(context.getAssets(), outputDirectory));
		ResourceManager.setProvider(new AndroidResourceProvider(context, outputDirectory));
		return true;
	}

	public void setResourcesDirectory(String path) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(ORIGINAL_SETTLERS_FILES_PATH_SETTING_KEY, path).apply();
	}
}
