package jsettlers.main.replay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import jsettlers.common.CommonConstants;
import jsettlers.common.utils.MainUtils;
import jsettlers.graphics.swing.resources.SwingResourceLoader;
import jsettlers.main.swing.SwingManagedJSettlers;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ReplayToolApp {

	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		CommonConstants.ENABLE_CONSOLE_LOGGING = true;

		HashMap<String, String> argsMap = MainUtils.createArgumentsMap(args);
		SwingManagedJSettlers.loadDebugSettings(argsMap);
		SwingResourceLoader.setupResourcesManagerByConfigFile(SwingManagedJSettlers.getConfigFile(argsMap, "config.prp"));

		int targetGameTime = Integer.valueOf(argsMap.get("targetTime")) * 60 * 1000;

		String replayFileString = argsMap.get("replayFile");
		if (replayFileString == null)
			throw new IllegalArgumentException("Replay file needs to be specified with --replayFile=<FILE>");
		File replayFile = new File(replayFileString);
		if (!replayFile.exists())
			throw new FileNotFoundException("Found replayFile parameter, but file can not be found: " + replayFile);

		ReplayTool.replayAndCreateSavegame(replayFile, targetGameTime);

		Thread.sleep(2000);
		System.exit(0);
	}
}
