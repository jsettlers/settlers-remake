package jsettlers.logic.map.newGrid;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import jsettlers.common.map.MapLoadException;
import synchronic.timer.NetworkTimer;

/**
 * This class serializes and deserializes the {@link MainGrid} and therefore the complete game state.
 * 
 * @author Andreas Eberle
 * 
 */
public class GameSerializer {

	private static final long SAVE_STACK_SIZE = 1024 * 1024; // size of the save thread's stack
	private static final long LOAD_STACK_SIZE = 1024 * 1024; // size of the load thread's stack

	/**
	 * Saves the grid to the given output file.
	 * 
	 * @param grid
	 *            The grid to use.
	 * @param out
	 *            The output file/stream for the game.
	 * @throws IOException
	 */
	public void save(MainGrid grid, OutputStream out) throws IOException {
		final ObjectOutputStream oos = new ObjectOutputStream(out);

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
		oos.close();
	}

	public MainGrid load(InputStream inStream) throws MapLoadException {
		try {
			final ObjectInputStream ois = new ObjectInputStream(inStream);

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
		private final MainGrid grid;
		private final ObjectOutputStream oos;
		Throwable exception = null;

		private GameSaveTask(MainGrid grid, ObjectOutputStream oos) {
			this.grid = grid;
			this.oos = oos;
		}

		@Override
		public void run() {
			try {
				oos.writeInt(NetworkTimer.get().getGameTime());
				oos.writeObject(grid);
			} catch (Throwable t) {
				t.printStackTrace();
				this.exception = t;
			}
		}
	}

	private static final class LoadRunnable implements Runnable {
		private final ObjectInputStream ois;
		MainGrid grid = null;
		Throwable exception = null;

		private LoadRunnable(ObjectInputStream ois) {
			this.ois = ois;
		}

		@Override
		public void run() {
			try {
				NetworkTimer.get().setGameTime(ois.readInt());
				grid = (MainGrid) ois.readObject();
			} catch (Throwable t) {
				t.printStackTrace();
				this.exception = t;
			}
		}
	}

}
