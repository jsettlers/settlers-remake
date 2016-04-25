/*******************************************************************************
 * Copyright (c) 2016
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.integration.replay;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import jsettlers.common.map.MapLoadException;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.main.replay.ReplayUtils;
import jsettlers.testutils.map.MapUtils;

/**
 * Created by michael on 24.04.16.
 */
public class AutoReplaySetting {
	static final String REMAINING_REPLAY_FILENAME = "out/remainingReplay.log";

	public static Collection<AutoReplaySetting> getDefaultSettings() {
		return Arrays.asList(
				new AutoReplaySetting("basicproduction", 15),

				new AutoReplaySetting("fullproduction", 10),
				new AutoReplaySetting("fullproduction", 20),
				new AutoReplaySetting("fullproduction", 30),
				new AutoReplaySetting("fullproduction", 40),
				new AutoReplaySetting("fullproduction", 50),
				new AutoReplaySetting("fullproduction", 69),

				new AutoReplaySetting("fighting", 8));
	}

	private final String typeName;
	private final int timeMinutes;

	public AutoReplaySetting(String typeName, int timeMinutes) {
		this.typeName = typeName;
		this.timeMinutes = timeMinutes;
	}

	public String getTypeName() {
		return typeName;
	}

	public int getTimeMinutes() {
		return timeMinutes;
	}

	public String getPath() {
		return getTypeName() + "/savegame-" + getTimeMinutes() + "m.zmap";
	}

	public MapLoader getMap() throws MapLoadException {
		return MapUtils.getMap(getClass(), getTypeName() + "/base.rmap");
	}

	public String getReplayName() {
		return getTypeName() + "/replay.log";
	}

	ReplayUtils.IReplayStreamProvider getReplayFile() throws MapLoadException {
		return MapUtils.createReplayForResource(getClass(), getReplayName(), getMap());
	}

	MapLoader getReferenceSavegame() throws MapLoadException, IOException {
		String replayPath = getReplayPath();

		System.out.println("Using reference file: " + replayPath);
		return MapLoader.getLoaderForListedMap(new MapList.ListedResourceMap(replayPath));
	}

	public String getReplayPath() {
		return "/" + getClass().getPackage().getName().replace('.', '/') + "/" + getPath();
	}

	@Override
	public String toString() {
		return "AutoReplaySetting{" +
				"typeName='" + typeName + '\'' +
				", timeMinutes=" + timeMinutes +
				'}';
	}
}
