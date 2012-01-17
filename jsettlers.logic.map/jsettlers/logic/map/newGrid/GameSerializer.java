package jsettlers.logic.map.newGrid;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import jsettlers.common.map.MapLoadException;
import jsettlers.common.resources.ResourceManager;
import synchronic.timer.NetworkTimer;

public class GameSerializer {

	private static final String QUICK_SAVE_FILE = "save/quicksave";
	private static final String NORMAL_EXTENSION = ".sav";
	private static final String GZIP_EXTENSION = ".sav.gz";
	private static final boolean SAVE_USE_GZIP = false;

	public GameSerializer() {
	}

	@Deprecated
	public void save(final MainGrid grid) throws FileNotFoundException,
	        IOException, InterruptedException {
		OutputStream file;
		if (SAVE_USE_GZIP) {
			OutputStream unzipped =
			        ResourceManager.writeFile(QUICK_SAVE_FILE + GZIP_EXTENSION);
			file = new GZIPOutputStream(unzipped);
		} else {
			file =
			        ResourceManager.writeFile(QUICK_SAVE_FILE
			                + NORMAL_EXTENSION);
		}

		final ObjectOutputStream oos =
		        new ObjectOutputStream(new BufferedOutputStream(file));

		NetworkTimer.get().setPausing(true);
		try {
			Thread.sleep(30); // FIXME @Andreas serializer should wait until
			                  // threads did their work!
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Thread t = new Thread(null, new GameSaveTask(grid, oos), "SaveThread", 256 * 1024);
		t.start();
		t.join();

		oos.flush();
		oos.close();

		NetworkTimer.get().setPausing(false);
	}

	/**
	 * Saves the grid to the given output file.
	 * @param grid The grid to use.
	 * @param out The output file/stream for the game.
	 * @throws IOException 
	 */
	public void save(MainGrid grid, OutputStream out) throws IOException {
		final ObjectOutputStream oos =
		        new ObjectOutputStream(out);

		Thread t = new Thread(null, new GameSaveTask(grid, oos), "SaveThread", 256 * 1024);
		t.start();
		try {
	        t.join();
        } catch (InterruptedException e) {
	        throw new IOException(e);
        }

		oos.flush();
		oos.close();
    }

	public MainGrid load(String filename) throws MapLoadException {

		try {
			InputStream inStream;
			try {
				inStream = ResourceManager.getFile(filename + NORMAL_EXTENSION);
			} catch (IOException e) {
				InputStream gzipped =
				        ResourceManager.getFile(filename + GZIP_EXTENSION);
				inStream = new GZIPInputStream(gzipped);
			}

			return load(inStream);
		} catch (Throwable t) {
			throw new MapLoadException(t);
		}
	}

	public MainGrid load(InputStream inStream) throws MapLoadException {
		try {
			final ObjectInputStream ois = new ObjectInputStream(inStream);

			LoadRunnable runnable = new LoadRunnable(ois);
			Thread t = new Thread(null, runnable, "LoadThread", 256 * 1024);
			t.start();
			t.join();

			return runnable.grid;
		} catch (Throwable t) {
			throw new MapLoadException(t);
		}
	}

	private final class GameSaveTask implements Runnable {
	    private final MainGrid grid;
	    private final ObjectOutputStream oos;

	    private GameSaveTask(MainGrid grid, ObjectOutputStream oos) {
		    this.grid = grid;
		    this.oos = oos;
	    }

	    @Override
	    public void run() {
	    	try {
	    		oos.writeInt(NetworkTimer.getGameTime());
	    		oos.writeObject(grid);
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    }
    }

	private static final class LoadRunnable implements Runnable {
		private final ObjectInputStream ois;
		MainGrid grid = null;

		private LoadRunnable(ObjectInputStream ois) {
			this.ois = ois;
		}

		@Override
		public void run() {
			try {
				NetworkTimer.setGameTime(ois.readInt());
				grid = (MainGrid) ois.readObject();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
