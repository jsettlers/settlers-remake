/*******************************************************************************
 * Copyright (c) 2015
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.integration.replay;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import jsettlers.common.CommonConstants;
import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.main.replay.ReplayUtils;
import jsettlers.main.swing.resources.SwingResourceLoader;
import jsettlers.testutils.TestUtils;
import jsettlers.testutils.map.MapUtils;

/**
 * Created by Andreas Eberle on 23.04.2016.
 */
public class RegenerateAutoReplayITReferences {

	static {
		CommonConstants.ENABLE_CONSOLE_LOGGING = true;
		CommonConstants.CONTROL_ALL = true;
		CommonConstants.USE_SAVEGAME_COMPRESSION = true;
		Constants.FOG_OF_WAR_DEFAULT_ENABLED = false;

		TestUtils.setupTempResourceManager();
	}

	public static void main(String[] args) throws IOException, MapLoadException, ClassNotFoundException {
		System.out.println("Creating reference files for replays...");

		for (AutoReplaySetting setting : AutoReplaySetting.getDefaultSettings()) {
			int[] gameTimeMinutes = setting.getTimeMinutes();
			MapLoader[] actualSavegames = ReplayUtils.replayAndCreateSavegames(setting.getReplayFile(), gameTimeMinutes);

			for (int i = 0; i <actualSavegames.length;i++) {
				MapLoader actualSavegame = actualSavegames[i];
				try {
					MapLoader expectedSavegame = setting.getReferenceSavegame(i);

					MapUtils.compareMapFiles(expectedSavegame, actualSavegame);
					System.out.println("New savegame is equal to old one => won't replace.");
					actualSavegame.getListedMap().delete();

				} catch (AssertionError | IOException | MapLoadException ex) { // if the files are not equal, replace the existing one.
					System.out.println("Replacing reference file with new savegame '" + actualSavegame + "'");
					Files.move(Paths.get(actualSavegame.getListedMap().getFile().toString()),
							Paths.get("jsettlers.testutils/src/main/resources" + setting.getReplayPath(i)),
							StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}
}
