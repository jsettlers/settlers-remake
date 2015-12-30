/*******************************************************************************
 * Copyright (c) 2015
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
 *******************************************************************************/
package jsettlers.main.replay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jsettlers.common.CommonConstants;
import jsettlers.common.utils.MainUtils;
import jsettlers.common.utils.OptionableProperties;
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

		OptionableProperties options = MainUtils.loadOptions(args);
		SwingManagedJSettlers.loadOptionalSettings(options);
		SwingResourceLoader.setupResourcesManager(SwingManagedJSettlers.getConfigFile(options, "config.prp"));

		int targetGameTimeMinutes = Integer.valueOf(options.getProperty("targetTime")) * 60 * 1000;

		String replayFileString = options.getProperty("replayFile");
		if (replayFileString == null)
			throw new IllegalArgumentException("Replay file needs to be specified with --replayFile=<FILE>");
		File replayFile = new File(replayFileString);
		if (!replayFile.exists())
			throw new FileNotFoundException("Found replayFile parameter, but file can not be found: " + replayFile);

		ReplayTool.replayAndCreateSavegame(replayFile, targetGameTimeMinutes, "replayForSavegame.log");

		Thread.sleep(2000);
		System.exit(0);
	}
}
