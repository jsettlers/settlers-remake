package jsettlers.main.android;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import jsettlers.common.CommonConstants;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.resources.ResourceManager;
import jsettlers.integration.replay.AutoReplaySetting;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.main.android.resources.AndroidMapListFactory;
import jsettlers.main.android.resources.AndroidResourceProvider;
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
