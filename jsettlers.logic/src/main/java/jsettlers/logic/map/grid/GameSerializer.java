/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
package jsettlers.logic.map.grid;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.trading.HarborBuilding;
import jsettlers.logic.buildings.trading.MarketBuilding;
import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.logic.movable.Movable;

/**
 * This class serializes and deserializes the {@link MainGrid} and therefore the complete game state.
 *
 * @author Andreas Eberle
 */
public class GameSerializer {

	private static final long SAVE_STACK_SIZE = 1024 * 1024; // size of the save thread's stack
	private static final long LOAD_STACK_SIZE = 1024 * 1024; // size of the load thread's stack

	/**
	 * Saves the grid to the given output file.
	 *
	 * @param grid
	 * 		The grid to use.
	 * @param oos
	 * 		The output file/stream for the game.
	 * @throws IOException
	 * IOException
	 */
	public void save(MainGrid grid, final ObjectOutputStream oos) throws IOException {
		GameSaveTask runnable = new GameSaveTask(grid, oos);
		Thread t = new Thread(null, runnable, "SaveThread", SAVE_STACK_SIZE);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			throw new IOException(e);
		}

		if (runnable.exception != null) {
			throw new IOException("Error saving map.", runnable.exception);
		}

		oos.flush();
	}

	public MainGrid load(final ObjectInputStream ois) throws MapLoadException {
		try {
			LoadRunnable runnable = new LoadRunnable(ois);
			Thread t = new Thread(null, runnable, "LoadThread", LOAD_STACK_SIZE);
			t.start();
			t.join();

			if (runnable.grid != null) {
				return runnable.grid;
			} else {
				throw new MapLoadException("Error loading map.", runnable.exception);
			}
		} catch (Throwable t) {
			throw new MapLoadException(t);
		}
	}

	private final class GameSaveTask implements Runnable {
		private final MainGrid           grid;
		private final ObjectOutputStream oos;
		Throwable exception = null;

		private GameSaveTask(MainGrid grid, ObjectOutputStream oos) {
			this.grid = grid;
			this.oos = oos;
		}

		@Override
		public void run() {
			try {
				Building.writeStaticState(oos);
				MarketBuilding.writeStaticState(oos);
				HarborBuilding.writeStaticState(oos);
				Movable.writeStaticState(oos);
				oos.writeObject(grid);
			} catch (Throwable t) {
				t.printStackTrace();
				this.exception = t;
			}
		}
	}

	private static final class LoadRunnable implements Runnable {
		private final ObjectInputStream ois;
		MainGrid  grid      = null;
		Throwable exception = null;

		private LoadRunnable(ObjectInputStream ois) {
			this.ois = ois;
		}

		@Override
		public void run() {
			try {
				Building.readStaticState(ois);
				MarketBuilding.readStaticState(ois);
				HarborBuilding.readStaticState(ois);
				Movable.readStaticState(ois);
				grid = (MainGrid) ois.readObject();
			} catch (Throwable t) {
				t.printStackTrace();
				this.exception = t;
			}
		}
	}
}
