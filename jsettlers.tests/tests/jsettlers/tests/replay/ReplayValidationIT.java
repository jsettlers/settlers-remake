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
package jsettlers.tests.replay;

import java.io.File;
import java.io.IOException;

import jsettlers.logic.map.MapLoader;
import jsettlers.logic.map.save.loader.RemakeMapLoader;
import org.junit.Test;

import jsettlers.TestUtils;
import jsettlers.common.CommonConstants;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.resources.ResourceManager;
import jsettlers.common.utils.FileUtils;
import jsettlers.common.utils.FileUtils.IFileVisitor;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.save.DirectoryMapLister;
import jsettlers.logic.map.save.IMapListFactory;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.replay.ReplayTool;
import jsettlers.network.client.OfflineNetworkConnector;
import jsettlers.tests.utils.DebugMapLister;

public class ReplayValidationIT {
	private static final String REMAINING_REPLAY_FILENAME = "out/remainingReplay.log";

	static {
		CommonConstants.ENABLE_CONSOLE_LOGGING = true;
		CommonConstants.CONTROL_ALL = true;
		CommonConstants.USE_SAVEGAME_COMPRESSION = false;
		CommonConstants.ALL_AI = true;
		Constants.FOG_OF_WAR_DEFAULT_ENABLED = false;

		TestUtils.setupResourcesManager();
		MapList.setDefaultListFactory(new IMapListFactory() {
			@Override
			public MapList getMapList() {
				File resourceDir = ResourceManager.getResourcesDirectory();
				return new MapList(new DirectoryMapLister(new File(resourceDir, "maps"), true), new DebugMapLister(new File(resourceDir, "save"), true));
			}
		});
	}

	@Test
	public void testIfReplayIsEqualToOriginalPlay() throws IOException, MapLoadException, ClassNotFoundException {
		final float targetTimeMinutes = 60f;
		final String mapName = "mountain lake";

		OfflineNetworkConnector networkConnector = ReplayTool.createPausingOfflineNetworkConnector();
		MapLoader map = MapList.getDefaultList().getMapByName(mapName);
		byte playerId = (byte) 0;
		JSettlersGame game = new JSettlersGame(map, 0L, networkConnector, playerId,
				PlayerSetting.createDefaultSettings(playerId, (byte) map.getMaxPlayers()));

		RemakeMapLoader directSavegame = ReplayTool.playGameToTargetTimeAndGetSavegame(targetTimeMinutes, networkConnector, game);

		File replayFile = findNewestReplayFile().getCanonicalFile();

		System.out.println("Found replay file for savegame: " + replayFile);

		RemakeMapLoader replayedSavegame = ReplayTool.replayAndGetSavegame(replayFile, targetTimeMinutes, REMAINING_REPLAY_FILENAME);

		// compare direct savegame with replayed savegame.
		MapUtils.compareMapFiles(directSavegame, replayedSavegame);

		// delete created files
		FileUtils.deleteRecursively(replayFile.getParentFile());
		directSavegame.getListedMap().delete();
		replayedSavegame.getListedMap().delete();
	}

	private File findNewestReplayFile() throws IOException {
		final File[] newestReplay = new File[1];

		FileUtils.walkFileTree(new File(ResourceManager.getResourcesDirectory(), "logs"), new IFileVisitor() {
			private long newestModificationTime;

			@Override
			public void visitFile(File file) throws IOException {
				if (file.isDirectory() || !file.getName().endsWith("replay.log")) {
					return;
				}

				if (newestModificationTime < file.lastModified()) {
					newestModificationTime = file.lastModified();
					newestReplay[0] = file;
				}
			}
		});

		return newestReplay[0];
	}
}
