/*******************************************************************************
 * Copyright (c) 2015, 2016
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
package jsettlers.logic.constants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jsettlers.network.client.interfaces.IGameClock;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public final class MatchConstants {
	/**
	 * if true, the user will be able to see other players people and buildings
	 */
	public static boolean ENABLE_ALL_PLAYER_FOG_OF_WAR = false;

	/**
	 * if true, the user will be able to select other player's people and buildings.
	 */
	public static boolean ENABLE_ALL_PLAYER_SELECTION = false;

	public static boolean ENABLE_FOG_OF_WAR_DISABLING = false;

	/**
	 * NOTE: this value has only an effect if it's changed before the MainGrid is created! IT MUSTN'T BE CHANGED AFTER A MAIN GRID HAS BEEN CREATED <br>
	 * if false, no debug coloring is possible (but saves memory) <br>
	 * if true, debug coloring is possible.
	 */
	public static boolean ENABLE_DEBUG_COLORS = true;

	private MatchConstants() {
	}

	private static IGameClock clock;
	private static ExtendedRandom gameRandom;
	private static ExtendedRandom aiRandom;

	public static void init(IGameClock clock, long randomSeed) {
		clearState();
		MatchConstants.clock = clock;
		MatchConstants.gameRandom = new ExtendedRandom(randomSeed);
		MatchConstants.aiRandom = new ExtendedRandom(randomSeed);
	}

	public static void clearState() {
		if (clock != null) {
			clock.stopExecution();
		}
		clock = null;
		gameRandom = null;
		aiRandom = null;
	}

	public static IGameClock clock() {
		return clock;
	}

	public static ExtendedRandom random() {
		return gameRandom;
	}

	public static ExtendedRandom aiRandom() {
		return aiRandom;
	}

	public static void serialize(ObjectOutputStream oos) throws IOException {
		oos.writeInt(clock.getTime());
		oos.writeObject(gameRandom);
		oos.writeObject(aiRandom);
	}

	public static void deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		clock.setTime(ois.readInt());
		gameRandom = (ExtendedRandom) ois.readObject();
		aiRandom = (ExtendedRandom) ois.readObject();
	}

}
