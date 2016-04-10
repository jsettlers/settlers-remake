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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import jsettlers.common.resources.IResourceProvider;
import jsettlers.common.resources.ResourceManager;
import jsettlers.logic.map.loading.list.DirectoryMapLister;
import jsettlers.logic.map.loading.list.IListedMap;
import jsettlers.logic.map.loading.list.IMapLister;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.common.CommonConstants;
import jsettlers.common.map.MapLoadException;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.main.replay.ReplayUtils;
import jsettlers.testutils.map.MapUtils;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AutoReplayIT {
	@BeforeClass
	public static void setConstantes() {
		CommonConstants.ENABLE_CONSOLE_LOGGING = true;
		CommonConstants.CONTROL_ALL = true;
		CommonConstants.USE_SAVEGAME_COMPRESSION = true;
		Constants.FOG_OF_WAR_DEFAULT_ENABLED = false;
	}

	private static final String REMAINING_REPLAY_FILENAME = "out/remainingReplay.log";
	private static final Object ONLY_ONE_TEST_AT_A_TIME_LOCK = new Object();

	@Parameters(name = "{index}: {0} : {1}")
	public static Collection<Object[]> replaySets() {
		return Arrays.asList(new Object[][]{
				{"basicproduction", 15},

				{"fullproduction", 10},
				{"fullproduction", 20},
				{"fullproduction", 30},
				{"fullproduction", 40},
				{"fullproduction", 50},
				{"fullproduction", 69},

				{"fighting", 8}
		});
	}

	private final String folderName;
	private final int targetTimeMinutes;

	public AutoReplayIT(String folderName, int targetTimeMinutes) {
		this.folderName = folderName;
		this.targetTimeMinutes = targetTimeMinutes;
	}

	@Before
	public void fakeSaveDirectory() {
		// The replay files contain a save action.
		final MemoryResourceProvider resourceProvider = new MemoryResourceProvider();
		ResourceManager.setProvider(resourceProvider);

		MapList.setDefaultListFactory(new MapList.DefaultMapListFactory() {
			@Override
			protected IMapLister getAdditionalMaps() {
				return resourceProvider;
			}

			@Override
			protected IMapLister getSave() {
				return resourceProvider;
			}
		});
	}

	@Test
	public void testReplay() throws IOException, MapLoadException, ClassNotFoundException {
		synchronized (ONLY_ONE_TEST_AT_A_TIME_LOCK) {
			MapLoader actualSavegame = ReplayUtils.replayAndCreateSavegame(getReplayFile(), targetTimeMinutes, REMAINING_REPLAY_FILENAME);
			MapLoader expectedSavegame = getReferenceSavegamePath();

			MapUtils.compareMapFiles(expectedSavegame, actualSavegame);
			actualSavegame.getListedMap().delete();
		}
	}

	private MapLoader getReferenceSavegamePath() throws MapLoadException, IOException {
		String replayPath = "/" + getClass().getPackage().getName().replace('.', '/');
		replayPath += "/" + folderName;
		replayPath += "/savegame-" + targetTimeMinutes + "m.zmap";

		System.out.println("Using reference file: " + replayPath);
		return MapLoader.getLoaderForListedMap(new MapList.ListedResourceMap(replayPath));
	}

	private ReplayUtils.IReplayStreamProvider getReplayFile() throws MapLoadException {
		return MapUtils.createReplayForResource(getClass(), getReplayName(), MapUtils.getMap(getClass(), folderName + "/base.rmap"));
	}

	private String getReplayName() {
		return folderName + "/replay.log";
	}

	public static void main(String[] args) throws IOException, MapLoadException, ClassNotFoundException {
		System.out.println("Creating reference files for replays...");

		for (Object[] replaySet : replaySets()) {
			String folderName = (String) replaySet[0];
			int targetTimeMinutes = (Integer) replaySet[1];

			AutoReplayIT replayIT = new AutoReplayIT(folderName, targetTimeMinutes);
			MapLoader newSavegame = ReplayUtils.replayAndCreateSavegame(replayIT.getReplayFile(), targetTimeMinutes, REMAINING_REPLAY_FILENAME);
			MapLoader expectedSavegame = replayIT.getReferenceSavegamePath();

			try {
				MapUtils.compareMapFiles(expectedSavegame, newSavegame);
				System.out.println("New savegame is equal to old one => won't replace.");
				newSavegame.getListedMap().delete();
			} catch (AssertionError | IOException ex) { // if the files are not equal, replace the existing one.
				Files.move(Paths.get(newSavegame.getListedMap().getFile().toString()),
						Paths.get(expectedSavegame.getListedMap().getFile().toString()),
						StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Replacing reference file '" + expectedSavegame + "' with new savegame '" + newSavegame + "'");
			}
		}
	}

	private static class MemoryResourceProvider implements IResourceProvider, IMapLister {
		private Map<String, ByteArrayOutputStream> files = new HashMap<>();
		private int savegame = 0;

		@Override
		public InputStream getResourcesFileStream(String name) throws IOException {
			ByteArrayOutputStream out = files.get(name);
			if (out != null) {
				return new ByteArrayInputStream(out.toByteArray());
			} else {
				return null;
			}
		}

		@Override
		public OutputStream writeFile(String name) throws IOException {
			System.out.println("Writing file " + name);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			files.put(name, out);
			return out;
		}

		@Override
		public File getResourcesDirectory() {
			return null;
		}

		@Override
		public File getOriginalMapsDirectory() {
			return null;
		}

		@Override
		public void listMaps(IMapListerCallable callable) {
			System.out.println("Scanning maps... ");
			for (Map.Entry<String, ByteArrayOutputStream> e : files.entrySet()) {
				System.out.println("Scanning map " + e.getKey());
				findMap(callable, e.getKey());
			}
		}

		private void findMap(IMapListerCallable callable, final String key) {
			if (MapLoader.isExtensionKnown(key)) {
				callable.foundMap(new IListedMap() {
					@Override
					public String getFileName() {
						return key;
					}

					@Override
					public InputStream getInputStream() throws IOException {
						return getResourcesFileStream(getFileName());
					}

					@Override
					public void delete() {
						files.remove(key);
					}

					@Override
					public boolean isCompressed() {
						return getFileName().endsWith(MapLoader.MAP_EXTENSION_COMPRESSED);
					}

					@Override
					public File getFile() {
						throw new UnsupportedOperationException();
					}
				});
			}
		}

		@Override
		public OutputStream getOutputStream(MapFileHeader header) throws IOException {
			savegame++;
			return writeFile("savegame-" + savegame + MapLoader.MAP_EXTENSION);
		}
	}
}
