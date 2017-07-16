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

package jsettlers.main.android;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import jsettlers.common.CommonConstants;
import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.common.resources.ResourceManager;
import jsettlers.integration.replay.AutoReplaySetting;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.main.android.core.resources.AndroidMapListFactory;
import jsettlers.main.android.core.resources.AndroidResourceProvider;
import jsettlers.main.replay.ReplayUtils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.test.InstrumentationTestCase;

/**
 * Created by Andreas Eberle on 29.12.2016.
 */
public class AndroidAutoReplayIT extends InstrumentationTestCase {

	static {
		System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
	}

	@BeforeClass
	public static void setupConstants() {
		CommonConstants.ENABLE_CONSOLE_LOGGING = true;
		CommonConstants.CONTROL_ALL = true;
		CommonConstants.USE_SAVEGAME_COMPRESSION = true;
		Constants.FOG_OF_WAR_DEFAULT_ENABLED = false;
	}

	public AndroidAutoReplayIT() {
		Context context = InstrumentationRegistry.getTargetContext();

		File outputDirectory = context.getCacheDir();
		outputDirectory.mkdirs();

		MapList.setDefaultListFactory(new AndroidMapListFactory(context.getAssets(), outputDirectory));
		AndroidResourceProvider provider = new AndroidResourceProvider(context, outputDirectory);
		ResourceManager.setProvider(provider);
	}

	@Test
	public void testIfReplayWorks() throws MapLoadException, IOException, ClassNotFoundException {
		AutoReplaySetting setting = AutoReplaySetting.getDefaultSettings().iterator().next();
		MapLoader[] saveGames = ReplayUtils.replayAndCreateSavegames(setting.getReplayFile(), new int[] { 3 });
	}
}
