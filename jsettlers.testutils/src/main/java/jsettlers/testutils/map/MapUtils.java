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
package jsettlers.testutils.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import jsettlers.common.menu.UIState;
import jsettlers.logic.constants.ExtendedRandom;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.logic.map.loading.newmap.RemakeMapLoader;
import jsettlers.main.ReplayStartInformation;
import jsettlers.main.replay.ReplayUtils;
import jsettlers.network.synchronic.timer.NetworkTimer;

/**
 * 
 * @author Andreas Eberle
 *
 */
public final class MapUtils {

	private MapUtils() {
	}

	public static void compareMapFiles(MapLoader expectedSavegame, MapLoader actualSavegame)
			throws IOException, MapLoadException, ClassNotFoundException {
		System.out.println("Comparing expected '" + expectedSavegame + "' with actual '" + actualSavegame + "' (uncompressed!)");

		try (InputStream expectedStream = RemakeMapLoader.getMapInputStream(expectedSavegame.getListedMap());
				CountingInputStream actualStream = new CountingInputStream(RemakeMapLoader.getMapInputStream(actualSavegame.getListedMap()))) {
			MapFileHeader expectedHeader = MapFileHeader.readFromStream(expectedStream);
			MatchConstants.init(new NetworkTimer(true), 0L);
			MatchConstants.deserialize(new ObjectInputStream(expectedStream));
			int expectedTime = MatchConstants.clock().getTime();
			ExtendedRandom expectedRandom = MatchConstants.random();
			MatchConstants.clearState();

			MapFileHeader actualHeader = MapFileHeader.readFromStream(actualStream);
			MatchConstants.init(new NetworkTimer(true), 1L);
			MatchConstants.deserialize(new ObjectInputStream(actualStream));
			int actualTime = MatchConstants.clock().getTime();
			ExtendedRandom actualRandom = MatchConstants.random();
			MatchConstants.clearState();

			assertEquals("Map ID", expectedHeader.getBaseMapId(), actualHeader.getBaseMapId());
			assertEquals("Map time", expectedTime, actualTime);
			// Test the random behavior a bit to have a high probability of equality. An equals method does not exist for Random.
			assertEquals("Random number state", expectedRandom.nextLong(), actualRandom.nextLong());
			assertEquals("Random number state", expectedRandom.nextLong(), actualRandom.nextLong());

			int e, a;
			while (((e = expectedStream.read()) != -1) & ((a = actualStream.read()) != -1)) {
				assertEquals("difference at (uncompressed) byte " + actualStream.getByteCounter(), e, a);
			}
			assertEquals("files have different lengths (uncompressed)", e, a);
		}
	}

	public static MapLoader saveMainGrid(MainGrid mainGrid, Byte playerId, UIState uiState) {
		try {
			System.out.println("Writing savegame with final state of failed test.");
			mainGrid.save(playerId, uiState);
			return ReplayUtils.getNewestSavegame();
		} catch (IOException e) {
			System.err.println("Tried to create a savegame but failed:");
			e.printStackTrace();
			return null;
		}
	}

	public static MapLoader getMap(Class<?> relativeTo, String mapFileName) throws MapLoadException {
		String name = "/" + relativeTo.getPackage().getName().replace('.', '/') + "/" + mapFileName;
		System.out.println("Using map: " + name);
		MapList.ListedResourceMap resourceMap = new MapList.ListedResourceMap(name);
		return MapLoader.getLoaderForListedMap(resourceMap);
	}

	public static MapLoader getBigMap() throws MapLoadException {
		return getMap(MapUtils.class, "bigmap.rmap");
	}

	public static MapLoader getSpezialSumpf() throws MapLoadException {
		return getMap(MapUtils.class, "SpezialSumpf_12.map");
	}

	public static MapLoader getMountainlake() throws MapLoadException {
		return getMap(MapUtils.class, "mountainlake.rmap");
	}

	public static ReplayUtils.IReplayStreamProvider createReplayForResource(final Class<?> relativeTo, final String file, final MapLoader map) {
		assertNotNull(relativeTo.getResource(file));

		return new ReplayUtils.IReplayStreamProvider() {

			@Override
			public InputStream openStream() {
				return relativeTo.getResourceAsStream(file);
			}

			@Override
			public MapLoader getMap(ReplayStartInformation replayStartInformation) {
				assertEquals(map.getMapId(), replayStartInformation.getMapId());
				return map;
			}
		};
	}
}
