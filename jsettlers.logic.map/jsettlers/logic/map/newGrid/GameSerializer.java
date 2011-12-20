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

import jsettlers.common.resources.ResourceManager;
import synchronic.timer.NetworkTimer;

public class GameSerializer {

	private static final String QUICK_SAVE_FILE = "save/quicksave";
	private static final String NORMAL_EXTENSION = ".sav";
	private static final String GZIP_EXTENSION = ".sav.gz";
	private static final boolean SAVE_USE_GZIP = false;

	public GameSerializer() {
	}

	public void save(final MainGrid grid) throws FileNotFoundException, IOException, InterruptedException {
		OutputStream file;
		if (SAVE_USE_GZIP) {
			OutputStream unzipped = ResourceManager.writeFile(QUICK_SAVE_FILE + GZIP_EXTENSION);
			file = new GZIPOutputStream(unzipped);
		} else {
			file = ResourceManager.writeFile(QUICK_SAVE_FILE + NORMAL_EXTENSION);
		}

		final ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(file));

		NetworkTimer.get().setPausing(true);
		try {
			Thread.sleep(30); // FIXME @Andreas serializer should wait until threads did their work!
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Thread t = new Thread(null, new Runnable() {
			@Override
			public void run() {
				try {
					oos.writeInt(NetworkTimer.getGameTime());
					oos.writeObject(grid);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, "SaveThread", 256 * 1024);
		t.start();
		t.join();

		oos.flush();
		oos.close();

		NetworkTimer.get().setPausing(false);
	}

	public MainGrid load() throws IOException, InterruptedException {
		InputStream inStream;
		try {
			inStream = ResourceManager.getFile(QUICK_SAVE_FILE + NORMAL_EXTENSION);
		} catch (IOException e) {
			InputStream gzipped = ResourceManager.getFile(QUICK_SAVE_FILE + GZIP_EXTENSION);
			inStream = new GZIPInputStream(gzipped);
		}

		final ObjectInputStream ois = new ObjectInputStream(inStream);
		NetworkTimer.get().setPausing(true);

		LoadRunnable runnable = new LoadRunnable(ois);
		Thread t = new Thread(null, runnable, "LoadThread", 256 * 1024);
		t.start();
		t.join();

		NetworkTimer.get().setPausing(false);
		return runnable.grid;
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
